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
public @interface MethodByUrl
{
    String url();
    String dev_url();
}
