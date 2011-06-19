package org.grickle.jsondtotest.test;

import java.util.Comparator;

import org.grickle.jsondto.client.IsJSONSerializable;
import org.grickle.jsondto.client.Pickler;

import com.google.gwt.core.client.GWT;

/**
 * 
 */
public class ArrayPicklerTest extends AbstractPicklerTest
{
    @IsJSONSerializable
    static class MyObject
    {
        int num;

        @Override
        public String toString()
        {
            return "MyObject<" + num + ">";
        }

        public static MyObject[] create(Integer... numbers)
        {
            MyObject[] rv = new MyObject[numbers.length];
            for(int i=0; i<numbers.length; ++i)
            {
                rv[i] = new MyObject();
                rv[i].num = numbers[i];
            }
            return rv;
        }

        @Override
        public boolean equals(Object obj)
        {
            if ( obj instanceof MyObject )
                return num == ((MyObject)obj).num;
            else
                return false;
        }
    }

    interface StringArrayPickler extends Pickler<String[]> {}
    interface MyObjectMatrixPickler extends Pickler<MyObject[][]> {}

    public void testArrayPickler()
    {
        Comparator<String[]> strArrCmp = new Comparator<String[]>()
        {
            @Override
            public int compare(String[] o1, String[] o2)
            {
                if ( o1 == null && o2 == null )
                    return 0;
                if ( o1 == null || o2 == null )
                    return 1;
                if ( o1.length != o2.length )
                    return 1;
                for(int i=0; i<o1.length; ++i)
                {
                    if ( o1[i] == null && o2[i] == null )
                        continue;
                    if ( o1[i] == null || o2[i] == null )
                        return 1;
                    if ( ! o1[i].equals(o2[i]) )
                        return 1;
                }
                return 0;
            }
        };

        StringArrayPickler p = GWT.create(StringArrayPickler.class);
        runPUTest(p, new String[]{"hello", "world"}, strArrCmp);
        runPUTest(p, new String[]{}, strArrCmp);
        runPUTest(p, null, strArrCmp);
    }

    public void testMatrixPickler()
    {
        Comparator<MyObject[][]> momCmp = new Comparator<MyObject[][]>()
        {
            @Override
            public int compare(MyObject[][] o1, MyObject[][] o2)
            {
                if ( o1 == null && o2 == null )
                    return 0;
                if ( o1 == null || o2 == null )
                    return 1;
                if ( o1.length != o2.length )
                    return 1;
                outer: for(int i=0; i<o1.length; ++i)
                {
                    if ( o1[i] == null && o2[i] == null)
                        continue outer;
                    if ( o1[i] == null || o2[i] == null)
                        return 1;
                    if ( o1[i].length != o2[i].length )
                        return 1;
                    inner: for(int j=0; j<o1[i].length; ++j)
                    {
                        if ( o1[i][j] == null && o2[i][j] == null )
                            continue inner;
                        if ( o1[i][j] == null || o2[i][j] == null )
                            return 1;
                        if ( ! o1[i][j].equals(o2[i][j]))
                            return 1;
                    }
                }
                return 0;
            }
        };

        MyObjectMatrixPickler p = GWT.create(MyObjectMatrixPickler.class);
        runPUTest(p, new MyObject[][]{
                MyObject.create(1,2,3),
                MyObject.create(4,5,6,7),
                MyObject.create(7,8,9,10,11),
                MyObject.create(),
                null
        }, momCmp);
        runPUTest(p, new MyObject[][]{}, momCmp);
        runPUTest(p, null, momCmp);
    }
}
