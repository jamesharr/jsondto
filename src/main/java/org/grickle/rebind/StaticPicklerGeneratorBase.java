package org.grickle.rebind;

import com.google.gwt.core.ext.typeinfo.JType;

/**
 * Generate pickler
 */
public abstract class StaticPicklerGeneratorBase implements StaticPicklerGenerator
{
    private StaticPicklerFactory factory;
    private JType type;
    boolean isGenerated = false;

    StaticPicklerGeneratorBase(StaticPicklerFactory factory, JType type)
    {
        this.factory = factory;
        this.type = type;
    }

    final StaticPicklerFactory getFactory()
    {
        return factory;
    }

    final public JType getPicklerType()
    {
        return type;
    }

    final public String getPicklerClassName()
    {
        // TODO
        return null;
    }

    final public void generate()
    {
        if ( isGenerated )
            return;

        // Need to mark as generated first in case we have a recursive type.
        isGenerated = true;
        generateJavaSourceCode();
    }

    /**
     * Generate the code for the pickler.
     */
    abstract void generateJavaSourceCode();
}
