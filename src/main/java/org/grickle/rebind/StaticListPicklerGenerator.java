package org.grickle.rebind;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;

public class StaticListPicklerGenerator extends AbstractStaticCollectionPicklerGenerator
{
    public static class Factory implements StaticPicklerGenerator.Factory
    {
        @Override
        public StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context,
                StaticPicklerFactory factory, JType t) throws UnableToCompleteException {
            JParameterizedType pType = t.isParameterized();
            if ( pType == null )
                return null;
            if ( isAssignable(context, pType, List.class))
                return new StaticListPicklerGenerator(logger, context, factory, t);
            return null;
        }
    }

    StaticListPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType type)
    {
        super(logger, context, factory, type);
    }

    @Override
    String getCollectionImplementation()
    {
        return LinkedList.class.getName();
    }

}
