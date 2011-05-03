package org.grickle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * 
 */
public class ObjectPicklerTest extends AbstractPicklerTest
{

    @IsJSONSerializable
    public static class Foo
    {
        public int i1;
        Integer i2;
        protected String s1;
        transient String trans = "radio7";
        final int finalAnswer = 42;

        public Foo()
        {
        }

        public Foo(int i1, Integer i2, String s1)
        {
            this.i1 = i1;
            this.i2 = i2;
            this.s1 = s1;
        }

        @Override
        public String toString()
        {
            return "Foo<"
            + "i1=" + i1 + ","
            + "i2=" + i2 + ","
            + "s1=" + s1 + ","
            + "trans=" + trans + ","
            + "finalAnswer=" + finalAnswer + ">";
        }

        @Override
        public boolean equals(Object obj)
        {
            return toString().equals(obj.toString());
        }
    }

    public interface FooPickler extends Pickler<Foo> { }

    public void testFooPickler()
    {
        FooPickler fooPickler = GWT.create(FooPickler.class);
        runTest(fooPickler, new Foo(100,100,"hello"));
        runTest(fooPickler, new Foo(100,null,null));
    }

    @IsJSONSerializable
    public static class Bar
    {
        public String name;
        @Optional int optional = 42;
        @IgnoreNull String ignoreNull = "Some null-default value";

        Bar()
        {
        }

        public Bar(String name)
        {
            this.name = name;
        }

        public Bar(String name, int optional, String ignoreNull)
        {
            this.name = name;
            this.optional = optional;
            this.ignoreNull = ignoreNull;
        }

        @Override
        public String toString()
        {
            return "Foo<"
            + "name=" + name + ","
            + "optional=" + optional + ","
            + "ignoreNull=" + ignoreNull + ">";
        }

        @Override
        public boolean equals(Object obj)
        {
            return toString().equals(obj.toString());
        }
    }

    public interface BarPickler extends Pickler<Bar> {}

    public void testOptionalFields()
    {
        BarPickler p = GWT.create(BarPickler.class);
        // litmus test -- make sure normal pickling works fine.
        runTest(p, new Bar("James", 3, "asdf"));

        // Real test
        JSONObject pickled = new JSONObject();
        pickled.put("name", new JSONString("James"));
        pickled.put("ignoreNull", JSONNull.getInstance());
        JSONObject expectedRepickle = new JSONObject();
        expectedRepickle.put("name", new JSONString("James"));
        expectedRepickle.put("optional", new JSONNumber(42));
        expectedRepickle.put("ignoreNull", new JSONString("Some null-default value"));
        runTest(p, pickled, expectedRepickle);
    }
}
