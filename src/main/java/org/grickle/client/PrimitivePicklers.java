package org.grickle.client;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * These are picklers for primitive types which are used by generator code. Don't use these directly.
 */
public class PrimitivePicklers
{
    public static class IntPickler
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

    public static class IntegerPickler
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
                rv = (int) num.doubleValue();
            else if (str != null)
                // TODO - catch exception and throw something useful.
                rv = Integer.parseInt(str.stringValue());
            else if (nullval == JSONNull.getInstance())
                rv = null;
            else
                // TODO - meaningfull exception
                throw new RuntimeException();

            return rv;
        }
    }

    public static class StringPickler
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
}
