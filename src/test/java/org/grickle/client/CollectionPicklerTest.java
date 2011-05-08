package org.grickle.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.gwt.core.client.GWT;

/**
 * 
 */
public class CollectionPicklerTest extends AbstractPicklerTest
{
    public interface StringListPickler extends Pickler<List<String>> { }
    public interface StringSetPickler extends Pickler<Set<String>> { }

    /**
     * Convenient function for adding a bunch of things to a collection at once and then returning that collection
     * 
     * @param <C> Collection
     * @param <T> Type in the collection
     * @param collection Collection to add to
     * @param things Items to add to collection
     * @return collection
     */
    private <C extends Collection<T>, T> C addArray(C collection, T[] things)
    {
        for ( T item : things )
            collection.add(item);
        return collection;
    }

    public void testListPickler()
    {
        StringListPickler pickler = GWT.create(StringListPickler.class);
        runPUTest(pickler, null);
        runPUTest(pickler, new ArrayList<String>());
        runPUTest(pickler, addArray(new ArrayList<String>(), new String[]{"foo", "bar", "baz", null}));
    }

    public void testSetPickler()
    {
        StringSetPickler pickler = GWT.create(StringSetPickler.class);
        runPUTest(pickler, null);
        runPUTest(pickler, new TreeSet<String>());
        runPUTest(pickler, addArray(new TreeSet<String>(), new String[]{"foo", "bar", "baz"}));
    }
}
