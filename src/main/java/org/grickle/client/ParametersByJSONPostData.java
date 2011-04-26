package org.grickle.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Pass parameters by JSON POST data.
 * 
 * Method.foo(1,2,3,"hello") will result in the POST data of '[1,2,3,"hello"]'.
 * More complicated types will be auto-pickled.
 */
@Target(ElementType.TYPE)
public @interface ParametersByJSONPostData
{

}
