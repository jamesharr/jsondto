package org.grickle.client;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Several syntactic use cases
 */
@RpcUrl(URL="../remote.php/", devURL="http://localhost/~jharr/php/remote.php", devProxy=true)
public interface UseCaseSet1 extends JsonRpcService
{
    // If a method is a fire-and-forget method, we shouldn't require an async callback
    void nullMethod();
    void nullMethod(AsyncCallback<Void> cb);

    // Simple cases. Pickler should be smart enough to figure out what to do with nulls on
    // boxed methods
    void case2a(AsyncCallback<String> cb);
    void case2b(String s, int foo, Integer bar, AsyncCallback<Integer> cb);

    // Pickling object is supported. Will implement by the pickler subclassing the type (just so it has access
    // privileges to member variables).
    @IsJSONSerializable
    static class BasicObject
    {
        // If these aren't present in JSON, throw an exception
        int number;
        String str;

        // If this isn't present, leave as default (as set up by default constructor)
        @Optional int j;

        // This must be present, but can be null.
        Integer k;

        // Can be present & set, can be present and null, or can be absent.
        @Optional Integer l;

        @IgnoreNull
        String foo = "default value";

        // These are not touched by the pickler.
        transient String password;

        // Will just skip these
        @SuppressWarnings("unused") private String iDontHaveAccessToThis;
        final String isFinal = "";
    }
    void case3a(BasicObject o, AsyncCallback<BasicObject> cb);

    // Self-nesting objects ill be supported, though circular references won't be.
    // Pickling object graphs with cycles has undefined behavior (probably an infinite loop).
    @IsJSONSerializable
    static class TreeObject
    {
        String name;
        TreeObject left;
        TreeObject right;
    }
    void case4a(TreeObject o, AsyncCallback<TreeObject> cb);

    // Pickling of arrays, and standard collection types work fine.
    // Collection types that aren't concrete will be implemented via TreeX or ArrayX
    void case5a(BasicObject[] o, AsyncCallback<BasicObject[]> cb);
    void case5b(Set<BasicObject> o, AsyncCallback<Set<BasicObject>> cb);
    void case5c(Map<String,BasicObject> o, AsyncCallback<Map<String,BasicObject>> cb);
    void case5d(List<BasicObject> o, AsyncCallback<List<BasicObject>> cb);

    // In the case that they are initialized already for us, we use the defaults
    @IsJSONSerializable
    static class MyPickleable
    {
        // The DontNullify annotation tells the pickler to avoid nullifying this attribute if the
        // JSON looks like '{"stuff":null}', but rather to leave it as default.
        @IgnoreNull
        Set<Integer> stuff = new TreeSet<Integer>();
    }

    // TODO -- these items need more thought

    // JSON doens't support non-string keys.. Maybe support these either via type mushing (Integer.parseInt for
    // integer keys).
    void case6a(Map<Integer,BasicObject> o, AsyncCallback<Map<Integer,BasicObject>> cb);
    void case6b(Map<BasicObject,String> o, AsyncCallback<Map<BasicObject,String>> cb);

    // Use non-natural ordering. Either the pickler needs to be smart enough to pick up on the fact that this is
    // a TreeSet, or we need to provide a way to manually pickle this damn thing.
    @SuppressWarnings("serial")
    @IsJSONSerializable
    static class BasicObjectSet extends TreeSet<BasicObject>
    {
        static class BasicObjectComparator implements Comparator<BasicObject>
        {
            @Override
            public int compare(BasicObject arg0, BasicObject arg1)
            {
                // ...
                return 0;
            }
        }
        public BasicObjectSet()
        {
            super(new BasicObjectComparator());
        }
    }
    void case7a(BasicObjectSet set, AsyncCallback<BasicObjectSet> cb);
}
