package org.grickle.jsondto.rebind;

import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;

public class StaticSetPicklerGenerator extends AbstractStaticCollectionPicklerGenerator
{
    public static class Factory implements StaticPicklerGenerator.Factory
    {
        @Override
        public StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context,
                StaticPicklerFactory factory, JType t) throws UnableToCompleteException
                {
            JParameterizedType pType = t.isParameterized();
            if ( pType == null )
                return null;
            if ( isAssignable(context, pType, Set.class) )
                return new StaticSetPicklerGenerator(logger, context, factory, t);
            return null;
                }

    }

    StaticSetPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType type)
    {
        super(logger, context, factory, type);
    }

    @Override
    String getCollectionImplementation()
    {
        return TreeSet.class.getName();
    }

}
