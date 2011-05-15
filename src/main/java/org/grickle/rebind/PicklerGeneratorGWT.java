package org.grickle.rebind;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;

/**
 * GWT generator front-end for PicklerGenerator.
 * 
 * All this class is really here for is to handle the "already-created" condition sanely.
 */
public class PicklerGeneratorGWT extends Generator
{
    private static Map<String,String> builtPicklers = new TreeMap<String, String>();

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
    {
        if ( ! builtPicklers.containsKey(typeName) )
        {
            logger = logger.branch(TreeLogger.INFO, "Generating implementaiton for " + typeName);
            PicklerGenerator generator = new PicklerGenerator(logger, context, typeName);
            generator.generate(logger);
            builtPicklers.put(typeName, generator.getImplName());
        }
        return builtPicklers.get(typeName);
    }

}
