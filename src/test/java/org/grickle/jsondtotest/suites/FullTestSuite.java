package org.grickle.jsondtotest.suites;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.grickle.jsondtotest.client.ArrayPicklerTest;
import org.grickle.jsondtotest.client.CollectionPicklerTest;
import org.grickle.jsondtotest.client.MapPicklerTest;
import org.grickle.jsondtotest.client.ObjectPicklerTest;
import org.grickle.jsondtotest.client.RpcTest;
import org.grickle.jsondtotest.client.SimplePicklerTest;
import org.grickle.jsondtotest.client.TestUtilTest;

import com.google.gwt.junit.tools.GWTTestSuite;

/**
 * 
 */
public class FullTestSuite extends GWTTestSuite
{
    public static Test suite()
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(SimplePicklerTest.class);
        suite.addTestSuite(TestUtilTest.class);
        suite.addTestSuite(ArrayPicklerTest.class);
        suite.addTestSuite(CollectionPicklerTest.class);
        suite.addTestSuite(MapPicklerTest.class);
        suite.addTestSuite(ObjectPicklerTest.class);
        suite.addTestSuite(RpcTest.class);
        return suite;
    }
}
