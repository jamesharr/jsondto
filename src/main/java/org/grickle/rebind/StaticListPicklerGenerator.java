package org.grickle.rebind;

import java.util.LinkedList;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.typeinfo.JType;

public class StaticListPicklerGenerator extends StaticCollectionPicklerGeneratorBase
{
    StaticListPicklerGenerator(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType type)
    {
        super(logger, context, factory, type);
    }

    @Override
    String getCollectionImplementation()
    {
        return LinkedList.class.getName();
    }

}
