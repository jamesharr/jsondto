package org.grickle.client;

import com.google.gwt.core.client.GWT;

/**
 * 
 */
public class SimplePicklerTest extends AbstractPicklerTest
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
        runPicklerTest(stringPickler, "Hello world");
        runPicklerTest(stringPickler, "");
        runPicklerTest(stringPickler, null);
    }

    public void testIntegerPickler()
    {
        IntegerPicklerInterface integerPickler = GWT.create(IntegerPicklerInterface.class);
        runPicklerTest(integerPickler, 123);
        runPicklerTest(integerPickler, 0);
        runPicklerTest(integerPickler, -100000);
        runPicklerTest(integerPickler, null);
    }
}
