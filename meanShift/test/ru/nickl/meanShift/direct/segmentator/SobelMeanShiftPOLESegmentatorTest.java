/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.segmentator;

import java.util.Collection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nickl.meanShift.direct.filter.SimpleMSFilter;
import static org.junit.Assert.*;

/**
 *
 * @author nickl
 */
public class SobelMeanShiftPOLESegmentatorTest extends SegmentatorTest{

    public SobelMeanShiftPOLESegmentatorTest() {
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
       SobelMeanShiftPOLESegmentator s = new SobelMeanShiftPOLESegmentator(new SimpleMSFilter());
        s.setEqualityRange(4);
        s.setGradTreshold(100);
        s.setColorRange(7);
        s.setSquareRange((short)10);

        testSegmentator(s);
    }

   

}