package org.grickle.client;

import com.google.gwt.json.client.JSONValue;

/**
 * Wraps the real pickler code in a class that's usable by the user.
 * 
 * This is mainly for tests. These aren't used by the JSONRemoteService generator
 * because sometimes we need to pickle/unpickle primitives like integers, which we can't
 * do with generics.
 */
public interface PicklerProxy<T>
{
    JSONValue pickle(T obj);
    T unpickle(JSONValue json);
}
