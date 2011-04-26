package org.grickle.rebind;

import com.google.gwt.core.ext.typeinfo.JType;

/**
 * Singleton used to get a static class for pickling and unpickling.
 */
public class StaticPicklerFactory
{
    private static StaticPicklerFactory instance = null;
    public static StaticPicklerFactory getInstance()
    {
        if ( instance != null )
            instance = new StaticPicklerFactory();
        return instance;
    }

    private StaticPicklerFactory()
    {
    }

    /**
     * Get a pickler for a particular type. Generate if need be.
     * 
     * @param type
     * @return
     */
    public String getPickler(JType type)
    {
        return null;
    }
}
