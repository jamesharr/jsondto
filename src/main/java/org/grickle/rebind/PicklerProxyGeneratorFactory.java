package org.grickle.rebind;

import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;

public class PicklerProxyGeneratorFactory
{
    private static PicklerProxyGeneratorFactory instance;
    public static PicklerProxyGeneratorFactory getInstance()
    {
        if (instance == null)
            instance = new PicklerProxyGeneratorFactory();
        return instance;
    }

    private Map<String,PicklerProxyGenerator> generators = new TreeMap<String,PicklerProxyGenerator>();
    private PicklerProxyGeneratorFactory()
    {
    }

    public PicklerProxyGenerator getProxyGenerator(TreeLogger logger, GeneratorContext context, JType type) throws UnableToCompleteException
    {
        String qsn = type.getQualifiedSourceName();
        PicklerProxyGenerator rv;
        if ( ! generators.containsKey(qsn))
        {
            rv = new PicklerProxyGenerator(logger, context, type);
            generators.put(qsn, rv);
        }
        else
        {
            rv = generators.get(qsn);
        }
        return rv;
    }
}
