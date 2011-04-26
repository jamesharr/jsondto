package org.grickle.client;

/**
 * Pass parameters by JSON POST data.
 * 
 * Method.foo(1,2,3,"hello") will result in the POST data of '[1,2,3,"hello"]'.
 * More complicated types will be auto-pickled.
 */
public @interface ParametersByJSONPostData
{

}
