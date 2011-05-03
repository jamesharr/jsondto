package org.grickle.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class TestUtilTest extends AbstractPicklerTest
{

    /**
     * isEqual, but parameters are forced into JSONValue objects
     * 
     * @param expected
     * @param actual
     * @return
     */
    private boolean testEq(JSONValue expected, JSONValue actual)
    {
        return isEqual(expected, actual);
    }

    public void testString()
    {
        assertTrue(testEq(new JSONString("hello"), new JSONString("hello")));
        assertFalse(testEq(new JSONString("hello"), new JSONString("hello2")));
        assertFalse(testEq(new JSONString("123"), new JSONNumber(123)));
        assertFalse(testEq(new JSONString(""), JSONNull.getInstance()));
        JSONString same = new JSONString("asdf");
        assertTrue(testEq(same, same));
    }

    public void testNull()
    {
        assertTrue(testEq(JSONNull.getInstance(), JSONNull.getInstance()));
        assertFalse(testEq(JSONNull.getInstance(), new JSONString("")));
    }

    public void testNumber()
    {
        assertTrue(testEq(new JSONNumber(123), new JSONNumber(123)));
        assertFalse(testEq(new JSONNumber(0), new JSONNumber(1)));
        assertFalse(testEq(new JSONNumber(0), JSONNull.getInstance()));
    }

    public void testBoolean()
    {
        assertTrue(testEq(JSONBoolean.getInstance(true), JSONBoolean.getInstance(true)));
        assertTrue(testEq(JSONBoolean.getInstance(false), JSONBoolean.getInstance(false)));
        assertFalse(testEq(JSONBoolean.getInstance(false), JSONNull.getInstance()));
        assertFalse(testEq(JSONBoolean.getInstance(false), new JSONNumber(0)));
        assertFalse(testEq(JSONBoolean.getInstance(false), new JSONString("")));
    }

    public void testArray()
    {
        assertTrue(testEq(new JSONArray(), new JSONArray()));
        JSONArray a=new JSONArray(), b=new JSONArray();
        a.set(0, new JSONNumber(1));
        assertFalse(testEq(a,b));
        b.set(0, new JSONNumber(2));
        assertFalse(testEq(a,b));
        b.set(0, new JSONNumber(1));
        assertTrue(testEq(a,b));
    }

    public void testObject()
    {
        assertTrue(testEq(new JSONObject(), new JSONObject()));
        JSONObject a=new JSONObject(), b=new JSONObject();
        a.put("a", new JSONNumber(1));
        assertFalse(testEq(a,b));
        b.put("a", new JSONNumber(2));
        assertFalse(testEq(a,b));
        b.put("a", new JSONNumber(1));
        assertTrue(testEq(a,b));
    }
}
