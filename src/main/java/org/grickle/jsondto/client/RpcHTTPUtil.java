package org.grickle.jsondto.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This is used by the RPC generator to make HTTP requests.
 *
 * Probably not a good idea to use this directly.
 */
public class RpcHTTPUtil
{

    private static String chooseUrl(String url, String devUrl)
    {
        return GWT.isProdMode() ? url : devUrl;
    }

    /**
     * NOT IMPLEMENTED YET
     *
     * @param url
     * @param devUrl
     * @param method
     * @param params
     * @param cb
     */
    public static void get_base64(String url, String devUrl, String method, JSONArray params, final AsyncCallback<JSONValue> cb)
    {
        throw new RuntimeException("Not implemented yet. What are you doing here anyway?");
    }

    public static void get_urlencoded(String url, String devUrl, String method, JSONArray params, final AsyncCallback<JSONValue> cb)
    {
        String requestUrl = chooseUrl(url,devUrl)
        + "?method=" + URL.encodeQueryString(method)
        + "&params=" + URL.encodeQueryString(params.toString())
        + "&id=null";

        sendRequest(RequestBuilder.GET, requestUrl, null, null, cb);
    }

    public static void post(String url, String devUrl, String method, JSONArray params, final AsyncCallback<JSONValue> cb)
    {
        JSONObject data = new JSONObject();
        data.put("method", new JSONString(method));
        data.put("params", params);
        data.put("id", JSONNull.getInstance());
        sendRequest(RequestBuilder.POST, chooseUrl(url,devUrl), "application/json-rpc", data.toString(), cb);
    }

    public static void post_urlencoded(String url, String devUrl, String method, JSONArray params, final AsyncCallback<JSONValue> cb)
    {
        String data = "method=" + URL.encodeQueryString(method)
        + "&params=" + URL.encodeQueryString(params.toString())
        + "&id=null";

        sendRequest(RequestBuilder.POST, chooseUrl(url,devUrl), "application/x-www-form-urlencoded", data, cb);
    }

    private static void sendRequest(Method method, String requestUrl, String content_type, String data, final AsyncCallback<JSONValue> cb)
    {
        RequestBuilder rb = new RequestBuilder(method, requestUrl);
        if ( content_type != null )
            rb.setHeader("Content-Type", content_type);
        // rb.setHeader("Content-Type", "application/json-rpc");
        rb.setHeader("Accept", "application/json-rpc");

        // TODO - simplify the nesting with a convenience function or two
        // TODO - handle exceptions thrown by onFailure()?
        try
        {
            rb.sendRequest(data, new RequestCallback(){

                @Override
                public void onResponseReceived(Request request, Response response)
                {
                    if ( response.getStatusCode() != Response.SC_OK )
                    {
                        cb.onFailure(new RequestException("HTTP Error Code " + response.getStatusCode()
                                + " " + response.getStatusText()));
                    }
                    else
                    {
                        try {
                            JSONValue jsv = JSONParser.parseStrict(response.getText());
                            JSONObject obj = jsv.isObject();
                            if ( obj != null )
                            {
                                JSONValue error = obj.get("error");
                                if ( error != null && error != JSONNull.getInstance() )
                                    cb.onFailure(new ServerSideException(error));
                                else if ( obj.containsKey("result"))
                                    cb.onSuccess(obj.get("result"));
                                else
                                    cb.onFailure(new MalformedJSONException(error));
                            }
                            else
                            {
                                cb.onFailure(new MalformedJSONException(jsv));
                            }
                        }
                        catch(Exception e)
                        {
                            cb.onFailure(e);
                        }
                    }
                }

                @Override
                public void onError(Request request, Throwable e)
                {
                    cb.onFailure(e);
                }
            });
        } catch (Exception e)
        {
            cb.onFailure(e);
        }
    }

}
