package org.grickle.jsondtotest.test;

import org.grickle.jsondto.client.Pickler;

import com.google.gwt.core.client.GWT;

/**
 * 
 */
public class SimplePicklerTest extends AbstractPicklerTest
{
    interface StringPickler extends Pickler<String> {}
    interface IntegerPickler extends Pickler<Integer> {}

    public void testStringPickler()
    {
        StringPickler p = GWT.create(StringPickler.class);
        runPUTest(p, "Hello world");
        runPUTest(p, "");
        runPUTest(p, null);
    }

    public void testIntegerPickler()
    {
        IntegerPickler p = GWT.create(IntegerPickler.class);
        runPUTest(p, 123);
        runPUTest(p, 0);
        runPUTest(p, -100000);
        runPUTest(p, null);
    }
}
