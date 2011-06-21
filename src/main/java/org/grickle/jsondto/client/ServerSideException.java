package org.grickle.jsondto.client;

import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * A server-side exception.
 */
public class ServerSideException extends RpcException
{
    private static final long serialVersionUID = 1L;

    private JSONValue error;

    protected ServerSideException(JSONValue error)
    {
        this.error = error;
    }

    /**
     * Returns a text representation of the server. If the server returned text as the error,
     * that text is returned verbatim. If the return is JSON, the .toString() of that JSON
     * is returned.
     */
    @Override
    public String getMessage()
    {
        String rv = error.toString();
        JSONString jsonStr = error.isString();
        if ( jsonStr != null )
            rv = jsonStr.stringValue();
        return rv;
    }

    /**
     * Returns the JSON the server sent in the error. This can be used to convey more
     * detailed information about the error.
     * 
     * NOTE: Do not modify the return value.
     * 
     * @return
     */
    public JSONValue getJSONError()
    {
        return error;
    }
}
