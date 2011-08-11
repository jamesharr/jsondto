package org.grickle.jsondto.client.primitivepicklers;

import org.grickle.jsondto.client.UnpickleException;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class BooleanPickler
{
    public static JSONValue pickle(Boolean val)
    {
        if ( val == null )
            return JSONNull.getInstance();
        return JSONBoolean.getInstance(val);
    }

    public static Boolean unpickle(JSONValue val)
    {
        Boolean rv;

        JSONBoolean bool = val.isBoolean();
        JSONString str = val.isString();
        JSONNumber num = val.isNumber();
        JSONNull jNull = val.isNull();
        if (bool != null)
            rv = bool.booleanValue();
        else if (str != null)
            rv = Boolean.parseBoolean(str.stringValue());
        else if (num != null)
            rv = new Boolean(num.doubleValue() != 0.0);
        else if (jNull != null || val == null)
            rv = null;
        else
            throw new UnpickleException("Attempt to unpickle unsupported type into a Boolean: " + val);

        return rv;
    }
}