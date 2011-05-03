package org.grickle.client;

import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * 
 */
public abstract class AbstractPicklerTest extends GWTTestCase
{

    /**
     * 
     */
    public AbstractPicklerTest()
    {
        super();
    }

    @Override
    public String getModuleName()
    {
        return "org.grickle.jsondtoJUnit";
    }

    /**
     * Test pickling, then unpickling.
     * 
     * @param <T>
     * @param pickler
     * @param value
     */
    public <T> void runTest(Pickler<T> pickler, T value)
    {
        System.out.println("=== Pickling with " + pickler.getClass().getName() + " ===");
        System.out.println(" original: " + value);

        JSONValue pickled = pickler.pickle(value);
        System.out.println(" pickled: " + pickled);

        T unpickled = pickler.unpickle(pickled);
        System.out.println(" unpickled: " + unpickled);

        assertEquals(value, unpickled);
    }

    /**
     * Just test unpickling
     * 
     * @param <T>
     * @param p
     * @param string
     */
    protected <T> void runTest(Pickler<T> pickler, JSONValue pickled, JSONValue expectedRepickle)
    {
        System.out.println("=== Unpickling with " + pickler.getClass().getName() + " ===");
        System.out.println(" original json: " + pickled);

        T obj = pickler.unpickle(pickled);
        System.out.println(" unpickled: " + obj);

        JSONValue repickled = pickler.pickle(obj);
        System.out.println(" repickled: " + repickled);
        System.out.println(" expectedRepickle: " + expectedRepickle);

        assertEquals(repickled, expectedRepickle);
    }

    protected void assertEquals(JSONValue expected, JSONValue actual)
    {
        if ( ! isEqual(expected, actual) )
            failNotEquals("JSON Values are not the same", expected, actual);
    }

    protected boolean isEqual(JSONValue expected, JSONValue actual)
    {
        if ( expected.isArray() != null && actual.isArray() != null)
            return isEqual(expected.isArray(), actual.isArray());
        else if ( expected.isBoolean() != null && actual.isBoolean() != null)
            return isEqual(expected.isBoolean(), actual.isBoolean());
        else if ( expected.isNull() != null && actual.isNull() != null )
            return true;
        else if ( expected.isNumber() != null && actual.isNumber() != null )
            return isEqual(expected.isNumber(), actual.isNumber());
        else if ( expected.isObject() != null && actual.isObject() != null )
            return isEqual(expected.isObject(), actual.isObject());
        else if ( expected.isString() != null & actual.isString() != null )
            return isEqual(expected.isString(), actual.isString());
        return false;
    }

    private boolean isEqual(JSONString expected, JSONString actual)
    {
        return expected.stringValue().equals(actual.stringValue());
    }

    private boolean isEqual(JSONNumber expected, JSONNumber actual)
    {
        return expected.doubleValue() == actual.doubleValue();
    }

    private boolean isEqual(JSONBoolean expected, JSONBoolean actual)
    {
        return expected.booleanValue() == actual.booleanValue();
    }

    private boolean isEqual(JSONObject expected, JSONObject actual)
    {
        Set<String> keys = new TreeSet<String>();
        keys.addAll(expected.keySet());
        keys.addAll(actual.keySet());
        for(String k : keys)
        {
            if ( ! expected.containsKey(k) )
                return false;
            if ( ! actual.containsKey(k) )
                return false;
            if ( ! isEqual(expected.get(k), actual.get(k)) )
                return false;
        }
        return true;
    }

    private boolean isEqual(JSONArray expected, JSONArray actual)
    {
        if ( expected.size() != actual.size() )
            return false;
        for(int i=0,max=expected.size(); i<max; ++i)
            if ( ! isEqual(expected.get(i), actual.get(i)) )
                return false;
        return true;
    }

}