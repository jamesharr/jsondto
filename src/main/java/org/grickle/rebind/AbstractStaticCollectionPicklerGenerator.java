package org.grickle.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * 
 */
public abstract class AbstractStaticCollectionPicklerGenerator extends AbstractStaticPicklerGenerator
{

    /**
     * @param logger
     * @param context
     * @param factory
     * @param type
     */
    public AbstractStaticCollectionPicklerGenerator(TreeLogger logger, GeneratorContext context,
            StaticPicklerFactory factory, JType type)
    {
        super(logger, context, factory, type);
    }

    @Override
    void writePickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        logger.log(TreeLogger.DEBUG, "Creating pickle method");

        JType itemType = getItemType();
        String itemPickler = factory.getPickler(logger, context, itemType);

        src.println("if ( obj == null )");
        src.indentln("return " + JSONNULL_CLS + ".getInstance();");
        src.println(JSONARRAY_CLS + " rv = new " + JSONARRAY_CLS + "();");
        src.println("int i=0;");
        src.println("for (" + itemType.getQualifiedSourceName() + " item : obj)");
        src.indentln("rv.set(i++, " + itemPickler + ".pickle(item));");
        src.println("return rv;");
    }

    @Override
    void writeUnpickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        logger.log(TreeLogger.DEBUG, "Creating unpickle method");

        JType itemType = getItemType();
        String itemPickler = factory.getPickler(logger, context, itemType);

        String qsn = type.getParameterizedQualifiedSourceName();
        src.println("if ( json == " + JSONNULL_CLS + ".getInstance() )");
        src.indentln("return null;");
        src.println(JSONARRAY_CLS + " jsonArray = json.isArray();");
        src.println("if ( jsonArray == null )" );
        src.indentln("throw new " + UNPICKLE_EXCEPTION_CLS + "(\"Excpected array/null in JSON.\");");
        src.println("int size = jsonArray.size();");
        src.println(qsn + " rv = new " + getCollectionImplementation() + "<" + itemType.getQualifiedSourceName() + ">();");
        src.println("for ( int i=0; i<size; ++i )");
        src.indentln("rv.add(" + itemPickler + ".unpickle(jsonArray.get(i)) );");
        src.println("return rv;");
    }

    protected JType getItemType()
    {
        JType[] rv = type.isParameterized().getTypeArgs();
        assert(rv.length > 0);
        return rv[0];
    }

    abstract String getCollectionImplementation();
}
