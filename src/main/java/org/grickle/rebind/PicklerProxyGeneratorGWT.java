package org.grickle.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;

/**
 * GWT compiler tie in for PicklerProxyGenerator.
 * 
 * Note, this is essentially just a wrapper. I'd like to keep it this way I can do something sane
 * if I find out that this gets called for every instance of GWT.create()
 */
public class PicklerProxyGeneratorGWT extends Generator
{
    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
    throws UnableToCompleteException
    {
        try
        {
            JType type = context.getTypeOracle().getType(typeName);
            TreeLogger sublogger = logger.branch(TreeLogger.INFO, "Generating implementaiton for " + typeName);
            PicklerProxyGenerator ppf = new PicklerProxyGenerator(sublogger, context, type);
            ppf.generate(sublogger);
            return ppf.getImplName();
        } catch (NotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
