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
public @interface RpcUrl
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
     * Request method type. Defaults to POST, which puts raw JSON
     * inside the POST body.
     */
    RpcRequestType requestMethod() default RpcRequestType.POST;

    /**
     * How to map the RPC method name to the JSON method name.
     */
    RpcMethodMap methodMapping() default RpcMethodMap.SHORT_CLASS;
}
