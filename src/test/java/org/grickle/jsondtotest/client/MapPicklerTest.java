package org.grickle.jsondtotest.client;

import java.util.Map;
import java.util.TreeMap;

import org.grickle.jsondto.client.IsJSONSerializable;
import org.grickle.jsondto.client.Pickler;

import com.google.gwt.core.client.GWT;

/**
 * 
 */
public class MapPicklerTest extends AbstractGWTTest
{
    @IsJSONSerializable
    public static class Foo
    {
        public int id;
        public Foo(){}
        public Foo(int id){this.id = id;}
        @Override
        public boolean equals(Object rhs)
        {
            if ( rhs == null )
                return false;
            if ( ! (rhs instanceof Foo) )
                return false;
            return id == ((Foo)rhs).id;
        }
        @Override
        public String toString() { return "Foo<" + id + ">"; }
    }

    public interface StringMapPickler extends Pickler<Map<String,String>> { }
    public interface IntegerMapPickler extends Pickler<Map<String,Integer>> { }
    public interface FooMapPickler extends Pickler<Map<String,Foo>> { }

    private Map<String,Foo> makeFooMap(int... numbers)
    {
        Map<String,Foo> rv = new TreeMap<String,Foo>();
        for(int i : numbers)
            rv.put(Integer.toString(i), new Foo(100*i));
        return rv;
    }

    private Map<String,String> makeMap(String... items)
    {
        Map<String,String> rv = new TreeMap<String,String>();
        for(String i : items)
            rv.put(i, "*" + i + "*");
        return rv;
    }

    private Map<String,Integer> makeMap(int... items)
    {
        Map<String,Integer> rv = new TreeMap<String,Integer>();
        for(int i: items)
            rv.put(Integer.toString(i), i*100);
        return rv;
    }

    public void testStringMap()
    {
        StringMapPickler pickler = GWT.create(StringMapPickler.class);
        runPUTest(pickler, null);
        runPUTest(pickler, new TreeMap<String,String>());
        runPUTest(pickler, makeMap("foo", "bar", "baz"));
    }

    public void testIntegerMap()
    {
        IntegerMapPickler pickler = GWT.create(IntegerMapPickler.class);
        runPUTest(pickler, null);
        runPUTest(pickler, new TreeMap<String,Integer>());
        runPUTest(pickler, makeMap(1,2,3,4,5,6,-1));
    }

    public void testObjectMap()
    {
        FooMapPickler pickler = GWT.create(FooMapPickler.class);
        runPUTest(pickler, null);
        runPUTest(pickler, makeFooMap());
        runPUTest(pickler, makeFooMap(1,3,5,7,9,-10,-13));
    }
}
