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

    /**
     * Test pickling, then unpickling.
     * 
     * @param <T>
     * @param pickler
     * @param value
     */
    public <T> void runTest(Pickler<T> pickler, T value)
    {
        System.out.println("=== Pickling with " + pickler.getClass().getName() + " ===");
        System.out.println(" original: " + value);

        JSONValue pickled = pickler.pickle(value);
        System.out.println(" pickled: " + pickled);

        T unpickled = pickler.unpickle(pickled);
        System.out.println(" unpickled: " + unpickled);

        assertEquals(value, unpickled);
    }

    /**
     * Just test unpickling
     * 
     * @param <T>
     * @param p
     * @param string
     */
    protected <T> void runTest(Pickler<T> pickler, JSONValue pickled, JSONValue expectedRepickle)
    {
        System.out.println("=== Unpickling with " + pickler.getClass().getName() + " ===");
        System.out.println(" original json: " + pickled);

        T obj = pickler.unpickle(pickled);
        System.out.println(" unpickled: " + obj);

        JSONValue repickled = pickler.pickle(obj);
        System.out.println(" repickled: " + repickled);
        System.out.println(" expectedRepickle: " + expectedRepickle);

        // HACK. No apparent guarantee to object property order.
        // For now, lets cross our fingers and hope we don't have to write something to deep-inspect
        // JSON.
        assertEquals(repickled.toString(), expectedRepickle.toString());
    }

}