package org.grickle.client;

import com.google.gwt.core.client.GWT;

/**
 * 
 */
public class SimplePicklerTest extends AbstractPicklerTest
{
    interface StringPickler extends Pickler<String> {}
    interface IntegerPickler extends Pickler<Integer> {}

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
        StringPickler p = GWT.create(StringPickler.class);
        runTest(p, "Hello world");
        runTest(p, "");
        runTest(p, null);
    }

    public void testIntegerPickler()
    {
        IntegerPickler p = GWT.create(IntegerPickler.class);
        runTest(p, 123);
        runTest(p, 0);
        runTest(p, -100000);
        runTest(p, null);
    }
}
