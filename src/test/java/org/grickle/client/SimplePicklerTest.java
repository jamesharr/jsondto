package org.grickle.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * 
 */
public class SimplePicklerTest extends GWTTestCase
{
    /* (non-Javadoc)
     * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
     */
    @Override
    public String getModuleName()
    {
        return "org.grickle.jsondtoJUnit";
    }

    public void testStringPickler()
    {
        StringPicklerInterface stringPickler = GWT.create(StringPicklerInterface.class);
        testPickler(stringPickler, "Hello world");
        testPickler(stringPickler, "");
        testPickler(stringPickler, null);
    }

    public void testIntegerPickler()
    {
        IntegerPicklerInterface integerPickler = GWT.create(IntegerPicklerInterface.class);
        testPickler(integerPickler, 123);
        testPickler(integerPickler, 0);
        testPickler(integerPickler, -100000);
        testPickler(integerPickler, null);
    }

    public <T> void testPickler(Pickler<T> pickler, T value)
    {
        System.out.println("=== Pickling with " + pickler.getClass().getName() + " ===");
        System.out.println(" original: " + value);

        JSONValue pickled = pickler.pickle(value);
        System.out.println(" pickled: " + pickled);

        T unpickled = pickler.unpickle(pickled);
        System.out.println(" unpickled: " + unpickled);

        assertEquals(value, unpickled);
    }
}
