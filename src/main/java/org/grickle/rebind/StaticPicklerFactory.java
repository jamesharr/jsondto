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
 * Singleton used to get a static class for pickling and unpickling.
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

    private static final class FailFactory implements Factory
    {
        @Override
        public StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory,
                JType t) throws UnableToCompleteException {
            String qsn = t.getParameterizedQualifiedSourceName();
            logger.log(TreeLogger.ERROR, "Don't know how to pickle " + qsn + ".");
            throw new UnableToCompleteException();
        }
    }

    private static final class PrimitiveFailFactory implements Factory
    {
        @Override
        public StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory,
                JType t) throws UnableToCompleteException {
            if ( t.isPrimitive() == null )
                return null;
            String qsn = t.getParameterizedQualifiedSourceName();
            logger.log(TreeLogger.ERROR, "Primitive type " + qsn + " isn't supported yet.");
            throw new UnableToCompleteException();
        }
    }

    private StaticPicklerFactory()
    {
        picklerImpls.put("int", IntPickler.class.getName());
        picklerImpls.put(Integer.class.getName(), IntegerPickler.class.getName());
        picklerImpls.put(String.class.getName(), StringPickler.class.getName());

        picklerFactories.add(new StaticArrayPicklerGenerator.Factory());
        picklerFactories.add(new StaticListPicklerGenerator.Factory());
        picklerFactories.add(new StaticSetPicklerGenerator.Factory());
        picklerFactories.add(new StaticMapPicklerGenerator.Factory());
        picklerFactories.add(new StaticObjectPicklerGenerator.Factory());

        // Stub to throw an error for primitives we don't already have an implementation for
        picklerFactories.add(new PrimitiveFailFactory());

        // Factory that is guaranteed to as our last fall back
        picklerFactories.add(new FailFactory());
    }

    Map<String,String> picklerImpls = new TreeMap<String,String>();
    List<Factory> picklerFactories = new LinkedList<Factory>();

    /**
     * Get a pickler for a particular type. Generate if need be.
     * 
     * @param type
     * @return
     * @throws UnableToCompleteException
     */
    public String getPickler(TreeLogger logger, GeneratorContext context, JType type) throws UnableToCompleteException
    {
        // This needs to be first to handle recursive objects
        String qsn = type.getParameterizedQualifiedSourceName();
        if ( picklerImpls.containsKey(qsn) )
            return picklerImpls.get(qsn);

        // See who can implement it
        StaticPicklerGenerator spg = null;
        for (Factory f : picklerFactories)
        {
            spg = f.getPickler(logger, context, this, type);
            if ( spg != null )
                break;
        }

        // Save mapping first, then generate code (recursive objects)
        String picklerImplName = spg.getPicklerClassName();
        picklerImpls.put(qsn, picklerImplName);
        spg.generate();

        // Done
        return picklerImplName;
    }
}
