package org.grickle.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

/**
 * GWT compiler tie in for PicklerProxyGenerator
 */
public class PicklerProxyGeneratorGWT extends Generator
{

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
    throws UnableToCompleteException
    {
        try
        {
            PicklerProxyGeneratorFactory f = PicklerProxyGeneratorFactory.getInstance();
            JType type = context.getTypeOracle().getType(typeName);
            TreeLogger sublogger = logger.branch(TreeLogger.INFO, "Generating implementaiton for " + typeName);
            PicklerProxyGenerator pgen = f.getProxyGenerator(sublogger, context, type);
            pgen.generate(sublogger);
            return pgen.getImplName();
        } catch (NotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
