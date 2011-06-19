package org.grickle.jsondto.client.primitivepicklers;

import org.grickle.jsondto.client.UnpickleException;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * Boxed int pickler.
 * 
 * Note - This class attempts to parse a number if it's a string.
 */
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