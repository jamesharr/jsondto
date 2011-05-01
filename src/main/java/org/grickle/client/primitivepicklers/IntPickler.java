package org.grickle.client.primitivepicklers;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class IntPickler
{
    public static JSONValue pickle(int val)
    {
        return new JSONNumber(val);
    }

    public static int unpickle(JSONValue val)
    {
        int rv;

        JSONNumber num = val.isNumber();
        JSONString str = val.isString();
        if (num != null)
            rv = (int) num.doubleValue();
        else if (str != null)
            // TODO - catch exception and throw something useful.
            rv = Integer.parseInt(str.stringValue());
        else
            // TODO - meaningfull exception
            throw new RuntimeException();

        return rv;
    }
}