package org.grickle.rebind;

import com.google.gwt.core.ext.typeinfo.JType;

/**
 * Singleton used to get a static class for pickling and unpickling.
 */
public class PicklerFactory
{
    private static PicklerFactory instance = null;
    public static PicklerFactory getInstance()
    {
        if ( instance != null )
            instance = new PicklerFactory();
        return instance;
    }

    private PicklerFactory()
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
