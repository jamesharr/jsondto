package org.grickle.rebind;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;

public interface StaticPicklerGenerator
{
    JType getPicklerType();
    String getPicklerClassName();
    void generate() throws UnableToCompleteException;

    public static interface Factory
    {
        /**
         * Get a pickler for type t. Return null if t cannot be pickled by this type.
         * 
         * @param logger
         * @param context
         * @param factory
         * @param t
         * @return
         */
        StaticPicklerGenerator getPickler(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType t) throws UnableToCompleteException;
    }
}
