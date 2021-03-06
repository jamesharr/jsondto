package org.grickle.jsondto.client.primitivepicklers;

import org.grickle.jsondto.client.UnpickleException;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
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
        JSONNumber num = val.isNumber();
        JSONNull jNull = val.isNull();
        if (bool != null)
            rv = bool.booleanValue();
        else if (str != null)
            rv = Boolean.parseBoolean(str.stringValue());
        else if (num != null )
            rv = new Boolean(num.doubleValue() != 0.0 );
        else if ( jNull != null || val == null )
            throw new UnpickleException("Attempt to unpickle null into a boolean");
        else
            throw new UnpickleException("Attempt to unpickle unsupported type into a boolean: " + val);

        return rv;
    }
}