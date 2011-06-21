package org.grickle.jsondtotest.client;

import org.grickle.jsondto.client.JsonRpcService;
import org.grickle.jsondto.client.RpcEndpoint;
import org.grickle.jsondto.client.RpcRequestType;
import org.grickle.jsondto.client.ServerSideException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 */
public class RpcTest extends AbstractGWTTest
{
    // Test argument pickling
    @RpcEndpoint(URL="/echoParams", requestMethod=RpcRequestType.GET_URLENCODED)
    public interface ArgumentPicklingTest extends JsonRpcService
    {
        void a(int a1, boolean b2, String c3, AsyncCallback<String> cb);
    }
    public void testArgumentPickling()
    {
        ArgumentPicklingTest apt = GWT.create(ArgumentPicklingTest.class);
        delayTestFinish(2000);
        apt.a(1, false, "Hello There", new AsyncCallback<String>() {
            @Override
            public void onSuccess(String args)
            {
                assertEquals("[1,false,\"Hello There\"]", args);
                finishTest();
            }
            @Override
            public void onFailure(Throwable arg0)
            {
                arg0.printStackTrace();
            }
        });
    }

    // Test some basic server-side exception handling
    @RpcEndpoint(URL="/errorServlet", requestMethod=RpcRequestType.GET_URLENCODED)
    public interface ErrorService extends JsonRpcService
    {
        void run(String errorToReturn, AsyncCallback<String> cb);
    }
    public void testServerSideException()
    {
        ErrorService svc = GWT.create(ErrorService.class);
        final String err = "This is my error.";
        delayTestFinish(2000);
        svc.run(err, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String arg0)
            {
                fail("We got success somehow...");
            }

            @Override
            public void onFailure(Throwable e)
            {
                if ( e instanceof ServerSideException )
                {
                    String serverSideErrorMessage = e.getMessage();
                    assertEquals(err, serverSideErrorMessage);
                    finishTest();
                }
                else
                {
                    e.printStackTrace();
                    fail("Didn't get a ServerSideException back...\n");
                }
            }
        });
    }
}
