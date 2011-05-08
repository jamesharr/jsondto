package org.grickle.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JArrayType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

public class StaticArrayPicklerGenerator extends AbstractStaticPicklerGenerator
{
    public static class Factory implements StaticPicklerGenerator.Factory
    {
        @Override
        public StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context,
                StaticPicklerFactory factory, JType t) throws UnableToCompleteException {
            JArrayType array = t.isArray();
            if ( array != null )
                return new StaticArrayPicklerGenerator(logger, context, factory, array);
            return null;
        }
    }

    JArrayType arrayType;

    StaticArrayPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JArrayType type)
    {
        super(logger, context, factory, type);
        arrayType = type.isArray();
        assert(arrayType != null );
    }

    @Override
    void generateJavaSourceCode() throws UnableToCompleteException
    {
        TreeLogger logger = this.logger.branch(TreeLogger.DEBUG, "Generating pickler for " + type.getQualifiedSourceName());

        SourceWriter src = startClassFile(logger, null);
        createPickleMethod(logger, src);
        createUnpickleMethod(logger, src);
        src.commit(logger);
    }

    private void createPickleMethod(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        logger.log(TreeLogger.DEBUG, "Creating pickle method");

        JType itemType = arrayType.getComponentType();
        String itemPickler = factory.getPickler(logger, context, itemType);

        src.println("public static " + JSONVALUE_CLS + " pickle(" + type.getQualifiedSourceName() + " arr)");
        src.println("{");
        src.indent();
        src.println("if ( arr == null )");
        src.indentln("return " + JSONNULL_CLS + ".getInstance();");
        src.println(JSONARRAY_CLS + " rv = new " + JSONARRAY_CLS + "();");
        src.println("for (int i=0; i<arr.length; ++i)");
        src.indentln("rv.set(i, " + itemPickler + ".pickle(arr[i]));");
        src.println("return rv;");
        src.outdent();
        src.println("}");
    }

    private void createUnpickleMethod(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        logger.log(TreeLogger.DEBUG, "Creating unpickle method");

        JType itemType = arrayType.getComponentType();
        String itemPickler = factory.getPickler(logger, context, itemType);

        String rootItemQSN = getRootComponentType(arrayType).getQualifiedSourceName();
        String rootSubscriptFiller = getSubscriptFiller(arrayType);

        String qsn = arrayType.getQualifiedSourceName();
        src.println("public static " + qsn + " unpickle(" + JSONVALUE_CLS + " jsonValue)");
        src.println("{");
        src.indent();
        src.println("if ( jsonValue == " + JSONNULL_CLS + ".getInstance() )");
        src.indentln("return null;");
        src.println(JSONARRAY_CLS + " jsonArray = jsonValue.isArray();");
        src.println("if ( jsonArray == null )" );
        src.indentln("throw new " + UNPICKLE_EXCEPTION_CLS + "(\"Excpected array/null in JSON.\");");
        src.println("int size = jsonArray.size();");
        src.println(qsn + " rv = new " + rootItemQSN + "[size]" + rootSubscriptFiller + ";");
        src.println("for ( int i=0; i<size; ++i )");
        src.indentln("rv[i] = " + itemPickler + ".unpickle(jsonArray.get(i));");
        src.println("return rv;");
        src.outdent();
        src.println("}");
    }

    /**
     * Get absolute root type for the array type. This is useful for multi-demensional.
     * 
     * For example: String[][][] -> String
     * @param type
     * @return
     */
    private JType getRootComponentType(JType type)
    {
        JType root = type;
        while ( root.isArray() != null )
            root = root.isArray().getComponentType();
        return root;
    }

    /**
     * For a chain of arrays, get a string representing []
     * 
     * Examples:
     *   String[][][] -> "[][]"
     *   String[] -> ""
     * 
     * @param type
     * @return
     */
    private String getSubscriptFiller(JArrayType type)
    {
        String rv = "";
        JType tmp = type.getComponentType();
        while ( tmp.isArray() != null )
        {
            rv += "[]";
            tmp = tmp.isArray().getComponentType();
        }
        return rv;
    }
}
