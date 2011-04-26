package org.grickle.rebind;

import com.google.gwt.core.ext.typeinfo.JType;

/**
 * Singleton to get PicklerGenerator objects.
 * 
 * We need to keep PicklerGenrators around for the duration of the GWT compile,
 * and this is the only way GWT allows.
 */
public class PicklerGeneratorFactory
{
    private static PicklerGeneratorFactory instance = null;
    public static PicklerGeneratorFactory getInstance()
    {
        if ( instance != null )
            instance = new PicklerGeneratorFactory();
        return instance;
    }

    private PicklerGeneratorFactory()
    {
    }

    /**
     * Get a pickler generator. If the pickler genrator has already been created
     * for a specific JType, then get the existing object instead of creating a new one.
     * 
     * @param type
     * @return
     */
    public PicklerGenerator getPicklerGenerator(JType type)
    {
        // TODO
        return null;
    }
}
