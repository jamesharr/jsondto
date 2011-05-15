package org.grickle.rebind;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.grickle.client.primitivepicklers.IntPickler;
import org.grickle.client.primitivepicklers.IntegerPickler;
import org.grickle.client.primitivepicklers.StringPickler;
import org.grickle.rebind.StaticPicklerGenerator.Factory;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;

/**
 * Singleton used to get a static pickle/unpickle implementation.
 */
public class StaticPicklerFactory
{
    private static StaticPicklerFactory instance = null;
    public static StaticPicklerFactory getInstance()
    {
        if ( instance == null )
            instance = new StaticPicklerFactory();
        return instance;
    }

    private StaticPicklerFactory()
    {
        prebuilt.put("int", IntPickler.class.getName());
        prebuilt.put(Integer.class.getName(), IntegerPickler.class.getName());
        prebuilt.put(String.class.getName(), StringPickler.class.getName());

        picklerFactories.add(new StaticArrayPicklerGenerator.Factory());
        picklerFactories.add(new StaticListPicklerGenerator.Factory());
        picklerFactories.add(new StaticSetPicklerGenerator.Factory());
        picklerFactories.add(new StaticMapPicklerGenerator.Factory());
        picklerFactories.add(new StaticObjectPicklerGenerator.Factory());
    }

    Map<String,String> prebuilt = new TreeMap<String,String>();
    List<Factory> picklerFactories = new LinkedList<Factory>();

    /**
     * Generate a pickler implementation for type.
     * 
     * @param type
     * @return
     * @throws UnableToCompleteException
     */
    public String getPickler(TreeLogger logger, GeneratorContext context, JType type) throws UnableToCompleteException
    {
        // This needs to be first to handle recursive objects
        String qsn = type.getParameterizedQualifiedSourceName();
        if ( prebuilt.containsKey(qsn) )
            return prebuilt.get(qsn);

        // See who can implement it
        StaticPicklerGenerator spg = null;
        for (Factory f : picklerFactories)
        {
            spg = f.getPickler(logger, context, this, type);
            if ( spg != null )
                break;
        }

        // Didn't find anything
        if ( spg == null )
        {
            if ( type.isPrimitive() != null )
                logger.log(TreeLogger.ERROR, "Don't know how to pickle primitive " + qsn + " (yet).");
            else
                logger.log(TreeLogger.ERROR, "Don't know how to pickle " + qsn + ".");
            throw new UnableToCompleteException();
        }

        // Generate it
        spg.generate();

        // Done
        return spg.getPicklerClassName();
    }
}
