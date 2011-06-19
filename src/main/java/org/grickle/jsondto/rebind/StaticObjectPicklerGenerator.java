package org.grickle.jsondto.rebind;

import org.grickle.jsondto.client.IgnoreNull;
import org.grickle.jsondto.client.IsJSONSerializable;
import org.grickle.jsondto.client.Optional;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JConstructor;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

public class StaticObjectPicklerGenerator extends AbstractStaticPicklerGenerator
{
    public static class Factory implements StaticPicklerGenerator.Factory
    {
        @Override
        public StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context,
                StaticPicklerFactory factory, JType t) throws UnableToCompleteException {
            String qsn = t.getParameterizedQualifiedSourceName();
            JClassType classType = t.isClass();
            if ( classType == null )
                return null;
            if ( classType.getAnnotation(IsJSONSerializable.class) != null )
            {
                return new StaticObjectPicklerGenerator(logger, context, factory, classType);
            }
            else
            {
                logger.log(TreeLogger.ERROR, "Not sure how to pickle " + qsn + ". Maybe mark as @IsJSONSerializable?");
                throw new UnableToCompleteException();
            }
        }
    }

    private JClassType classType;

    /**
     * @param logger
     * @param context
     * @param factory
     * @param type
     */
    public StaticObjectPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory,
            JType type)
    {
        super(logger, context, factory, type);
        classType = type.isClass();
        assert(classType != null);
    }

    @Override
    String getSuperClass()
    {
        return type.getParameterizedQualifiedSourceName();
    }

    /**
     * @throws UnableToCompleteException
     * 
     */
    @Override
    void sanityCheck(TreeLogger logger) throws UnableToCompleteException
    {
        logger = logger.branch(TreeLogger.DEBUG, "Performing type sanity check");

        JConstructor defaultConstructor = null;
        for ( JConstructor c : classType.getConstructors() )
        {
            if ( c.getParameters().length == 0 )
                defaultConstructor = c;
        }

        if ( defaultConstructor == null )
            fail(logger, "Pickled type needs to contain a default constructor");
        if ( defaultConstructor.isPrivate() )
            fail(logger, "Default constructor cannot be private.");
        if ( ! classType.isAnnotationPresent(IsJSONSerializable.class) )
            fail(logger, "Type needs to have annotation @IsJSONSerializable");
    }

    @Override
    void writePickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        src.println("if ( obj == null )");
        src.indentln("return " + JSONNULL_CLS + ".getInstance();");
        src.println(JSONOBJECT_CLS + " rv = new " + JSONOBJECT_CLS + "();");
        for(JField f : classType.getFields())
        {
            if ( skipField(logger, f) )
                continue;

            String name = f.getName();
            JType fieldType = f.getType();
            String fieldPickler = factory.getPickler(logger, context, fieldType);
            src.println("rv.put(\"" + name + "\", " + fieldPickler + ".pickle(obj." + name + "));");
        }
        src.println("return rv;");

    }

    @Override
    void writeUnpickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        String qsn = classType.getQualifiedSourceName();
        src.println( "if ( json == " + JSONNULL_CLS + ".getInstance() )");
        src.indentln("return null;");
        src.println( JSONOBJECT_CLS + " jsonObject = json.isObject();");
        src.println( "if ( jsonObject == null )" );
        src.indentln( "throw new " + UNPICKLE_EXCEPTION_CLS + "(\"Expected Object, got something else\");" );
        src.println( qsn + " rv = new " + qsn + "();");
        for(JField f : classType.getFields())
        {
            if ( skipField(logger, f) )
                continue;

            String name = f.getName();
            JType fieldType = f.getType();
            String fieldPickler = factory.getPickler(logger, context, fieldType);

            src.println("// Field: " + name);
            src.println("if ( jsonObject.containsKey(\"" + name + "\") )");
            src.println("{"); src.indent();
            {
                src.println(JSONVALUE_CLS + " fieldValue = jsonObject.get(\"" + name + "\");");

                // key present and has value
                src.println("if ( fieldValue.isNull() != " + JSONNULL_CLS + ".getInstance() )");
                unpickle_present(src, name, fieldPickler);

                // Key present, but null
                src.println("else");
                unpickle_nullValue(src, f);

            }
            src.outdent(); src.println("}");

            // Key not present at all
            src.println("else");
            unpickle_keyNotPresent(src, f);
        }


        // Done with pickler
        src.println("return rv;");
    }

    private void unpickle_keyNotPresent(SourceWriter src, JField f)
    {
        // Key not even present
        src.println("{");
        src.indent();
        if ( f.isAnnotationPresent(Optional.class) )
            src.println("// @Optional");
        else
            src.println("throw new " + UNPICKLE_EXCEPTION_CLS + "(\"Key not present, but expected: "
                    + f.getName() + "\");");
        src.outdent();
        src.println("}");
    }

    /**
     * @param src
     * @param name
     * @param fieldPickler
     */
    private void unpickle_present(SourceWriter src, String name, String fieldPickler)
    {
        src.println("{");
        src.indent();
        src.println("rv." + name + " = " + fieldPickler + ".unpickle(jsonObject.get(\"" + name + "\"));");
        src.outdent();
        src.println("}");
    }

    /**
     * @param src
     */
    private void unpickle_nullValue(SourceWriter src, JField f)
    {
        src.println("{");
        src.indent();
        if ( f.isAnnotationPresent(IgnoreNull.class))
            src.println("// @IgnoreNull");
        else if ( f.getType().isPrimitive() != null )
            src.println("throw new " + UNPICKLE_EXCEPTION_CLS
                    + "(\"Field expected to have value, but got null instead: " + f.getName() + "\");");
        else
            src.println("rv." + f.getName() + " = null;");
        src.outdent();
        src.println("}");
    }

    /**
     * Should we skip this field?
     * 
     * @param f
     * @return
     */
    private boolean skipField(TreeLogger logger, JField f)
    {
        boolean skip = false;
        if ( f.isPrivate() )
        {
            logger.log(TreeLogger.DEBUG, "Skipping private field " + f.getName());
            skip = true;
        }
        else if ( f.isTransient() )
        {
            logger.log(TreeLogger.DEBUG, "Skipping transient field " + f.getName());
            skip = true;
        }
        else if ( f.isFinal() )
        {
            logger.log(TreeLogger.DEBUG, "Skipping final field " + f.getName());
            skip = true;
        }
        else if ( f.isStatic() )
        {
            logger.log(TreeLogger.DEBUG, "Skipping static field " + f.getName());
            skip = true;
        }
        return skip;
    }
}
