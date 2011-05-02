package org.grickle.client;

import com.google.gwt.core.client.GWT;

/**
 * 
 */
public class ObjectPicklerTest extends AbstractPicklerTest
{
    /* (non-Javadoc)
     * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
     */
    @Override
    public String getModuleName()
    {
        return "org.grickle.jsondtoJUnit";
    }

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
    }

    public interface FooPickler extends Pickler<Foo> { }

    public void testFooPickler()
    {
        FooPickler fooPickler = GWT.create(FooPickler.class);
        runPicklerTest(fooPickler, new Foo(100,100,"hello"));
        runPicklerTest(fooPickler, new Foo(100,null,null));
    }

    public void testOptionalFields()
    {
        FooPickler fooPickler = GWT.create(FooPickler.class);
        // TODO
        runPicklerTest(fooPickler, new Foo(100,100,"hello"));
        // runPicklerTest(fooPickler, "{i1:123,i2:456,s1:\"optionalTest\"}"); // No 'optional' field
    }
}
