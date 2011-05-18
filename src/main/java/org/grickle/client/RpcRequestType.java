package org.grickle.client;

public enum RpcRequestType {
    /**
     * Pickle parameters using the query string
     * 
     * For example: Foo.barThis(1,2,3, new AsyncCallback...);
     * Results in these parameters (they'll be URL Encoded - don't worry)
     *   method="Foo.barThis"
     *   params=[1,2,3]
     *   id=null
     */
    GET,

    /**
     * Pickle parameters using post data. The post data is raw JSON.
     * 
     * For example: Foo.barThis(1,2,3, new AsyncCallback...);
     * Results in this POST data (minus the new lines)
     * {
     *   "method":"Foo.barThis",
     *   "params":[1,2,3],
     *   "id":null
     * }
     */
    POST,

    /**
     * Pickle parameters using post data. The post data is similar to GET
     * 
     * For example: Foo.barThis(1,2,3, new AsyncCallback...);
     * Results in this POST data (minus the new lines, plus url encoding)
     *   method=Foo.barThis&params=[1,2,3]&id=null
     */
    POST_ENCODED,
}