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
    void writePickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        JType itemType = arrayType.getComponentType();
        String itemPickler = factory.getPickler(logger, context, itemType);

        src.println("if ( obj == null )");
        src.indentln("return " + JSONNULL_CLS + ".getInstance();");
        src.println(JSONARRAY_CLS + " rv = new " + JSONARRAY_CLS + "();");
        src.println("for (int i=0; i<obj.length; ++i)");
        src.indentln("rv.set(i, " + itemPickler + ".pickle(obj[i]));");
        src.println("return rv;");
    }

    @Override
    void writeUnpickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        JType itemType = arrayType.getComponentType();
        String itemPickler = factory.getPickler(logger, context, itemType);

        String rootItemQSN = getRootComponentType(arrayType).getQualifiedSourceName();
        String rootSubscriptFiller = getSubscriptFiller(arrayType);

        String qsn = arrayType.getQualifiedSourceName();
        src.println("if ( json == " + JSONNULL_CLS + ".getInstance() )");
        src.indentln("return null;");
        src.println(JSONARRAY_CLS + " jsonArray = json.isArray();");
        src.println("if ( jsonArray == null )" );
        src.indentln("throw new " + UNPICKLE_EXCEPTION_CLS + "(\"Excpected array/null in JSON.\");");
        src.println("int size = jsonArray.size();");
        src.println(qsn + " rv = new " + rootItemQSN + "[size]" + rootSubscriptFiller + ";");
        src.println("for ( int i=0; i<size; ++i )");
        src.indentln("rv[i] = " + itemPickler + ".unpickle(jsonArray.get(i));");
        src.println("return rv;");
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
