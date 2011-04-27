package org.grickle.rebind;

import com.google.gwt.core.ext.typeinfo.JType;

public interface StaticPicklerGenerator
{
    JType getPicklerType();
    String getPicklerClassName();
    void generate();
}
