package org.grickle.client;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * 
 */
public abstract class AbstractPicklerTest extends GWTTestCase
{

    /**
     * 
     */
    public AbstractPicklerTest()
    {
        super();
    }

    public <T> void runPicklerTest(Pickler<T> pickler, T value)
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