/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.filter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.nickl.meanShift.direct.LUV;

/**
 *
 * @author nickl
 */
public class SobelFilterTest extends LuvFilterTest {

    public SobelFilterTest() {
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
     * Test of filter method, of class SobelFilter.
     */
    @Test
    public void testFilter()
    {
       testFilter(new SobelFilter());
    }

}