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
        public int i;
        public Foo()
        {
        }

        public Foo(int i)
        {
            this.i = i;
        }

        @Override
        public String toString()
        {
            return "Foo<" + i + ">";
        }
    }

    public interface FooPickler extends Pickler<Foo> { }

    public void testFooPickler()
    {
        FooPickler fooPickler = GWT.create(FooPickler.class);
        runPicklerTest(fooPickler, new Foo(1));
        runPicklerTest(fooPickler, new Foo(0));
        runPicklerTest(fooPickler, new Foo(1000));
    }
}
