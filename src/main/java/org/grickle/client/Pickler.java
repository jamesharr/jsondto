package org.grickle.client;

import com.google.gwt.json.client.JSONValue;

/**
 * Wraps the real pickler code in a class that's usable by the user directly.
 * 
 * This is mainly for writing code tests, but could be used to pickle data
 * outside of normal RPC calls.
 */
public interface Pickler<T>
{
    JSONValue pickle(T obj);
    T unpickle(JSONValue json);
}
