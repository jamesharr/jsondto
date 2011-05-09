package org.grickle.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks that the RPC mechanism should identify the method by URL.
 * 
 * Specifically, the URL will be:
 *   url() + classShortName + "." + methodName
 */
@Target(value=ElementType.TYPE)
public @interface RPCPostURL
{
    /**
     * URL to use for the RPC call
     * @return
     */
    String URL();

    /**
     * URL to use for the RPC call in development mode. If left default, then it is the same
     * as the URL parameter
     * 
     * @return
     */
    String devURL() default "";

    /**
     * Whether or not to proxy development RPC calls to avoid browser cross-domain request
     * restrictions. Defaults to false.
     * 
     * @return
     */
    boolean devProxy() default false;

    /**
     * Whether or not to use the class name in the RPC call. The class name used is the short version
     * of the java interface.
     * 
     * @return
     */
    boolean useClassName() default true;
}
