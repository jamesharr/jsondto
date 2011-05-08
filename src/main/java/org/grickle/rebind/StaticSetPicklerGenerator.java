package org.grickle.rebind;

import java.util.TreeSet;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JType;

public class StaticSetPicklerGenerator extends StaticCollectionPicklerGeneratorBase
{

    StaticSetPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType type)
    {
        super(logger, context, factory, type);
    }

    @Override
    String getCollectionImplementation()
    {
        return TreeSet.class.getName();
    }

}
