package org.grickle.rebind;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

public class StaticMapPicklerGenerator extends AbstractStaticPicklerGenerator
{
    public static class Factory implements StaticPicklerGenerator.Factory
    {
        @Override
        public StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context,
                StaticPicklerFactory factory, JType type) throws UnableToCompleteException {
            JParameterizedType pType = type.isParameterized();
            if ( pType == null )
                return null;
            if ( ! isAssignable(context, pType, Map.class) )
                return null;
            return new StaticMapPicklerGenerator(logger, context, factory, type);
        }
    }

    private JClassType keyType;
    private JClassType valueType;
    private String valuePickler;

    private static final String treeMapQSN = TreeMap.class.getName();
    private static final String mapQSN = Map.class.getName();
    private static final String keyQSN = String.class.getName();
    private static final String entryQSN = Map.class.getName() + ".Entry";
    private String valueQSN;

    StaticMapPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType type) throws UnableToCompleteException
    {
        super(logger, context, factory, type);

        JParameterizedType pType = type.isParameterized();
        assert(pType != null);

        JClassType rawType = pType.getRawType();
        assert(rawType != null);

        JClassType[] typeArgs = pType.getTypeArgs();
        assert(typeArgs.length == 2);
        keyType = typeArgs[0];
        valueType = typeArgs[1];

    }

    @Override
    void sanityCheck(TreeLogger logger) throws UnableToCompleteException
    {
        valuePickler = factory.getPickler(logger, context, valueType);

        if ( ! keyType.getQualifiedSourceName().equals(String.class.getName()) )
            fail(logger, "Map<> pickled values can currently only use string keys");

        valueQSN = valueType.getParameterizedQualifiedSourceName();
    }

    @Override
    void writePickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        String entryPQSN = entryQSN + "<" + keyQSN + "," + valueQSN + ">";

        src.println("if ( obj == null )");
        src.indentln("return " + JSONNULL_CLS + ".getInstance();");
        src.println(JSONOBJECT_CLS + " rv = new " + JSONOBJECT_CLS + "();");
        src.println("for(" + entryPQSN + " entry : obj.entrySet() )");
        src.indentln("rv.put( entry.getKey(), " + valuePickler + ".pickle(entry.getValue()));");
        src.println("return rv;");
    }

    @Override
    void writeUnpickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        String rvPQSN = mapQSN + "<" + keyQSN + "," + valueQSN + ">";
        String rvConstructor = treeMapQSN + "<" + keyQSN + "," + valueQSN + ">";

        src.println( "if ( json == " + JSONNULL_CLS + ".getInstance() )");
        src.indentln("return null;");

        src.println(JSONOBJECT_CLS + " jsonObject = json.isObject();");
        src.println("if ( jsonObject == null )");
        src.indentln("throw new " + UNPICKLE_EXCEPTION_CLS + "(\"Expected object/null in JSON.\");");

        src.println(rvPQSN + " rv = new " + rvConstructor + "();");
        src.println("for(String s : jsonObject.keySet())");
        src.indentln("rv.put(s," + valuePickler + ".unpickle(jsonObject.get(s)));");
        src.println("return rv;");
    }

}
