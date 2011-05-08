package org.grickle.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.user.rebind.SourceWriter;

public class StaticMapPicklerGenerator extends AbstractStaticPicklerGenerator
{
    public static class Factory implements StaticPicklerGenerator.Factory
    {
        @Override
        public StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context,
                StaticPicklerFactory factory, JType t) throws UnableToCompleteException {
            // TODO Auto-generated method stub
            return null;
        }
    }

    StaticMapPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType type)
    {
        super(logger, context, factory, type);
        // TODO Auto-generated constructor stub
    }

    @Override
    void writePickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        // TODO Auto-generated method stub

    }

    @Override
    void writeUnpickleBody(TreeLogger logger, SourceWriter src) throws UnableToCompleteException
    {
        // TODO Auto-generated method stub

    }

}
