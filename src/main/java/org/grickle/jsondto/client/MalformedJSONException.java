package org.grickle.jsondto.client;

import com.google.gwt.json.client.JSONValue;

/**
 * JSON object in return was proper JSON, but was malformed.
 */
public class MalformedJSONException extends RpcException
{
    private static final long serialVersionUID = 1L;
    private JSONValue data;

    protected MalformedJSONException(JSONValue data)
    {
        this.data = data;
    }

    /**
     * 
     */
    @Override
    public String getMessage()
    {
        return "JSON response malformed: " + data.toString();
    }

    /**
     * Get malformed JSON datastructure
     * 
     * NOTE: Do not modify
     * 
     * @return
     */
    public JSONValue getJSON()
    {
        return data;
    }
}
