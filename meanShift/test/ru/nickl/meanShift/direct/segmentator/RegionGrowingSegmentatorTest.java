/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.segmentator;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nickl
 */
public class RegionGrowingSegmentatorTest extends SegmentatorTest {

    public RegionGrowingSegmentatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

@Test
    public void testProcess()
    {
        RegionGrowingSegmentator s = new RegionGrowingSegmentator();

        s.setMinRegionSize(3);
        s.setEqualityRange(200f);


        testSegmentator(s);

    }

}