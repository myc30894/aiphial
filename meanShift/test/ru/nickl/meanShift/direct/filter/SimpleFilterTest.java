/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.filter;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.nickl.meanShift.direct.LUV;

/**
 *
 * @author nickl
 */
public class SimpleFilterTest extends LuvFilterTest {

    public SimpleFilterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    /**
     * Test of filter method, of class SimpleMSFilter.
     */
    @Test
    public void testFilter()
    {
        SimpleMSFilter sm = new SimpleMSFilter();
        sm.setColorRange(7);
        sm.setSquareRange((short)10);
        super.testFilter(sm);
    }

}