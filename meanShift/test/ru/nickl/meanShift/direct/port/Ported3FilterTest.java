/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.port;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.filter.LuvFilterTest;

/**
 *
 * @author nickl
 */
public class Ported3FilterTest extends LuvFilterTest
{

    public Ported3FilterTest()
    {
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
     * Test of filter method, of class Ported3Filter.
     */
    @Test
    public void testFilter()
    {
        Ported3Filter f = new Ported3Filter();
        f.setColorRange(7);
        f.setSquareRange((short) 10);
        super.testFilter(f);
    }
}