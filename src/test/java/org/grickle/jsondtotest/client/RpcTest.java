package org.grickle.jsondtotest.client;

import org.grickle.jsondto.client.JsonRpcService;
import org.grickle.jsondto.client.RpcEndpoint;
import org.grickle.jsondto.client.RpcRequestType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 */
public class RpcTest extends AbstractGWTTest
{
    @RpcEndpoint(URL="/echoParams", requestMethod=RpcRequestType.GET_URLENCODED)
    public interface ArgumentPicklingTest extends JsonRpcService
    {
        void a(int a1, boolean b2, String c3, AsyncCallback<String> cb);
    }

    public void testServlet()
    {
        ArgumentPicklingTest apt = GWT.create(ArgumentPicklingTest.class);
        apt.a(1, false, "Hello There", new AsyncCallback<String>() {
            @Override
            public void onSuccess(String arg0)
            {
                System.err.println(arg0);
            }

            @Override
            public void onFailure(Throwable arg0)
            {
                arg0.printStackTrace();
            }
        });
    }
}
