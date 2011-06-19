package org.grickle.jsondto.client;

import com.google.gwt.json.client.JSONValue;

/**
 * Wraps the real pickler code in a class that's usable by the user directly.
 */
public interface Pickler<T>
{
    /**
     * Returns JSONValue representation of obj.
     * 
     * @param obj
     * @return Always a JSONValue (possibly JSONNull instance)
     */
    JSONValue pickle(T obj);

    /**
     * Returns object represented by json.
     * 
     * @param json
     * @return T instance, possibly null.
     */
    T unpickle(JSONValue json);

    /**
     * Return the json-string representation of an object.
     * 
     * @param obj
     * @return
     */
    String pickleToString(T obj);

    /**
     * Unpickle a T from a string.
     * 
     * @param str
     * @return Objected represented by str
     * @throws NullPointerException if str is null
     * @throws IllegalArgumentException if str is empty
     */
    T unpickle(String str);
}
