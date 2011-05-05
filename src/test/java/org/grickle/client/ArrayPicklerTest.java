package org.grickle.client;

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
        StringArrayPickler p = GWT.create(StringArrayPickler.class);
        runTest(p, new String[]{"hello", "world"});
        runTest(p, new String[]{});
        runTest(p, null);
    }

    public void testMatrixPickler()
    {
        MyObjectMatrixPickler p = GWT.create(MyObjectMatrixPickler.class);
        runTest(p, new MyObject[][]{
                MyObject.create(1,2,3),
                MyObject.create(4,5,6,7),
                MyObject.create(7,8,9,10,11),
                MyObject.create(),
                null
        });
        runTest(p, new MyObject[][]{});
        runTest(p, null);
    }
}
