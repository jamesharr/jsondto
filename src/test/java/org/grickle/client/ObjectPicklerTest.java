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
        boolean b = true;
        protected String s1;
        transient String trans = "radio7";
        final int finalAnswer = 42;

        public Foo()
        {
        }

        public Foo(int i1, Integer i2, String s1, boolean b)
        {
            this.i1 = i1;
            this.i2 = i2;
            this.s1 = s1;
            this.b = b;
        }

        @Override
        public String toString()
        {
            return "Foo<"
            + "i1=" + i1 + ","
            + "i2=" + i2 + ","
            + "s1=" + s1 + ","
            + "b=" + b + ","
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
        runPUTest(fooPickler, new Foo(100,100,"hello", true));
        runPUTest(fooPickler, new Foo(100,null,null, false));
        runPUTest(fooPickler, null);
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
        runPUTest(p, new Bar("James", 3, "asdf"));
        runPUTest(p, null);

        // Real test
        JSONObject pickled = new JSONObject();
        pickled.put("name", new JSONString("James"));
        pickled.put("ignoreNull", JSONNull.getInstance());
        JSONObject expectedRepickle = new JSONObject();
        expectedRepickle.put("name", new JSONString("James"));
        expectedRepickle.put("optional", new JSONNumber(42));
        expectedRepickle.put("ignoreNull", new JSONString("Some null-default value"));
        runUPTest(p, pickled, expectedRepickle);
    }

    @IsJSONSerializable
    public static class Recursive
    {
        public String name;
        public Recursive next;

        public static Recursive create(String... names)
        {
            Recursive rv = null;
            for(int i=names.length - 1; i>=0; i--)
                rv = new Recursive(names[i], rv);
            return rv;
        }

        Recursive()
        {
        }

        public Recursive(String name, Recursive next)
        {
            this.name = name;
            this.next = next;
        }

        public Recursive(String name, int optional, String ignoreNull)
        {
        }

        @Override
        public String toString()
        {
            return "Recursive<"
            + "name=" + name + ","
            + "next=" + (next == null ? "null" : next) + ">";
        }

        @Override
        public boolean equals(Object obj)
        {
            return toString().equals(obj.toString());
        }
    }

    public interface RecursivePickler extends Pickler<Recursive>{}

    public void testRecursive()
    {
        RecursivePickler p = GWT.create(RecursivePickler.class);
        runPUTest(p, Recursive.create("foo", "bar", "baz"));
        runPUTest(p, null);

        JSONObject inner = new JSONObject();
        inner.put("name", new JSONString("bar"));
        inner.put("next", JSONNull.getInstance());
        JSONObject pickled = new JSONObject();
        pickled.put("name", new JSONString("foo"));
        pickled.put("next", inner);
        runUPTest(p, pickled, p.pickle(Recursive.create("foo", "bar")));
    }
}
