package org.grickle.client.primitivepicklers;

import org.grickle.client.UnpickleException;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class IntegerPickler
{
    public static JSONValue pickle(Integer val)
    {
        if (val == null)
            return JSONNull.getInstance();
        else
            return new JSONNumber(val);
    }

    public static Integer unpickle(JSONValue val)
    {
        Integer rv;

        JSONNumber num = val.isNumber();
        JSONString str = val.isString();
        JSONNull nullval = val.isNull();
        if (num != null)
        {
            rv = (int) num.doubleValue();
        }
        else if (str != null)
        {
            try {
                rv = Integer.parseInt(str.stringValue());
            }
            catch (NumberFormatException nfe)
            {
                throw new UnpickleException("Failed attempt to parse integer as string");
            }
        }
        else if (nullval == JSONNull.getInstance())
        {
            rv = null;
        }
        else
        {
            throw new UnpickleException("Unable to unpickle non-integer as Integer");
        }

        return rv;
    }
}