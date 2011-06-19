package org.grickle.jsondto.client.primitivepicklers;

import org.grickle.jsondto.client.UnpickleException;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class BoolPickler
{
    public static JSONValue pickle(boolean val)
    {
        return JSONBoolean.getInstance(val);
    }

    public static boolean unpickle(JSONValue val)
    {
        boolean rv;

        JSONBoolean bool = val.isBoolean();
        JSONString str = val.isString();
        if (bool != null)
        {
            rv = bool.booleanValue();
        }
        else if (str != null)
        {
            rv = Boolean.parseBoolean(str.stringValue());
        }
        else
        {
            throw new UnpickleException("Attempt to unpickle null into an boolean");
        }

        return rv;
    }
}