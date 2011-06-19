package org.grickle.jsondto.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Marks that the RPC mechanism should identify the method by URL.
 * 
 * Specifically, the URL will be:
 *   url() + classShortName + "." + methodName
 */
@Target(value=ElementType.TYPE)
public @interface RpcEndpoint
{
    /**
     * URL to use for the RPC call
     * 
     * This URL is relative to GWT.getModuleBaseURL() unless it begins with a "/", "http://", or "https://"
     * 
     * @return
     */
    String URL();

    /**
     * URL to use for the RPC call in development mode. If left default, then it is the same
     * as the URL parameter. This can be used to specify a URL using the included proxy servlet.
     * 
     * This URL is relative to GWT.getModuleBaseURL() unless it begins with a "/", "http://", or "https://"
     * 
     * TODO - bug me about a proxy example
     * 
     * @return
     */
    String devURL() default "";

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
