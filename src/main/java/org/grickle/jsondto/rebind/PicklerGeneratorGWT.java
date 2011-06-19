package org.grickle.jsondto.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * GWT generator front-end for PicklerGenerator.
 * 
 * This class is really just here to wrap the generator and pass in values (like loggers)
 */
public class PicklerGeneratorGWT extends Generator
{
    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
    {
        logger = logger.branch(TreeLogger.TRACE, "Generating implementaiton for " + typeName);
        PicklerGenerator generator = new PicklerGenerator(logger, context, typeName);
        return generator.getImplName();
    }

}
