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
        StringPickler stringPickler = GWT.create(StringPickler.class);
        testPickler(stringPickler, "Hello world");
        testPickler(stringPickler, "");
        testPickler(stringPickler, null);
    }


    public <T> void testPickler(Pickler<T> pickler, T value)
    {
        JSONValue pickled = pickler.pickle(value);
        T unpickled = pickler.unpickle(pickled);
        assertEquals(value, unpickled);
    }
}
