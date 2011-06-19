package org.grickle.jsondto.client;

/**
 * Defines how a method name is mapped to the RPC method type
 */
public enum RpcMethodMap
{
    /**
     * Use fully qualified source name.
     * 
     * Example: com.example.MyRemoteService.doSomething
     */
    FULL_CLASS_NAME,

    /**
     * Use the class name, but only its short version.
     * 
     * Example: MyRemoteService.doSomething
     */
    SHORT_CLASS,

    /**
     * Use only the method name.
     * 
     * Example: doSomething
     */
    METHOD_ONLY
}
