package org.grickle.client.primitivepicklers;

import org.grickle.client.UnpickleException;

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
                throw new UnpickleException("Unable to parse as integer: " + str.stringValue());
            }
        }
        else
        {
            throw new UnpickleException("Attempt to unpickle null into an int");
        }

        return rv;
    }
}