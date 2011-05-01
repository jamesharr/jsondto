package org.grickle.client.primitivepicklers;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class StringPickler
{
    public static JSONValue pickle(String val)
    {
        if (val == null)
            return JSONNull.getInstance();
        else
            return new JSONString(val);
    }

    public static String unpickle(JSONValue val)
    {
        JSONString str = val.isString();
        JSONNull nul = val.isNull();
        if (str != null)
            return str.stringValue();
        else if (nul == JSONNull.getInstance())
            return null;
        else
            // TODO - throw something meaningful
            throw new RuntimeException();
    }
}