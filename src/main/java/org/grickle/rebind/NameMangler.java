package org.grickle.rebind;

import com.google.gwt.core.ext.typeinfo.JType;


public class NameMangler
{
    private static final String PICKLER = "__PICKLER";
    private static final String PROXY_PICKLER = "__PROXY_PICKLER";
    private static final String COMMA = "_COMMA_";
    private static final String GT = "_GT_";
    private static final String LT = "_LT_";
    private static final String DOT = "_DOT_";
    private static final String ARRAY = "_ARRAY_";

    /**
     * Note - does not return with package name
     * 
     * @param t
     * @return
     */
    public static String getProxyImplName(JType t)
    {
        return mangle(t) + PROXY_PICKLER;
    }

    /**
     * Note - does not return with package name
     * @param t
     * @return
     */
    public static String getPicklerName(JType t)
    {
        return mangle(t) + PICKLER;
    }

    private static String mangle(JType t)
    {
        String package_name = getPackageName(t) + ".";
        String rv = t.getQualifiedSourceName();
        assert(rv.startsWith(package_name));
        rv = rv.substring(package_name.length());
        rv = rv.replace(",", COMMA);
        rv = rv.replace("<", LT);
        rv = rv.replace(">", GT);
        rv = rv.replace(".", DOT);
        rv = rv.replace("[]", ARRAY);
        return rv;
    }

    /**
     * Get the package name for t
     * @param t
     * @return
     */
    public static String getPackageName(JType t)
    {
        String rv = null;
        if ( t.isArray() != null )
            rv = getPackageName(t.isArray().getComponentType());
        else if (t.isClassOrInterface() != null)
            rv = t.isClassOrInterface().getPackage().getName();
        else if (t.isParameterized() != null )
            rv = t.isParameterized().getPackage().getName();
        else
            assert(false);
        return rv;
    }

    /**
     * Get the package name for a (static/proxy) pickler for t
     * 
     * @param t
     * @return
     */
    public static String getPicklerPackageName(JType t)
    {
        String rv = getPackageName(t);
        // HAX. FIX TODO - dedicate a package to "shit outside the GWT source directories"
        if ( rv.startsWith("java.") )
            rv = "org.grickle.client." + rv;
        return rv;
    }
}
