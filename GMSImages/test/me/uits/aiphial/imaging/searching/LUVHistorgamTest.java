/*
 *
 * This file is part of Aiphial.
 *
 * Copyright (c) 2010 Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.uits.aiphial.imaging.searching;

import me.uits.aiphial.imaging.searching.LUVHistorgam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LUVConverter;
import me.uits.aiphial.imaging.ImgUtls;
import static org.junit.Assert.*;
import me.uits.aiphial.imaging.LuvPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class LUVHistorgamTest
{

    private static final int EQUALITYTREASHOLD = 150;

    public LUVHistorgamTest()
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

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }


    private void checkSimilarity(final String origname, final String samplename, boolean s) throws IOException
    {
        LUVHistorgam orig = LUVHistorgam.build(readImage(origname));
        LUVHistorgam sample = LUVHistorgam.build(readImage(samplename));
        final double c = orig.distcompare(sample);
        System.out.println(origname + " - " + samplename + ": " + c);

        if(s)
        {
        assertTrue(c+"<"+EQUALITYTREASHOLD, c < EQUALITYTREASHOLD);
        }
        else
        {
            assertTrue(c+">"+EQUALITYTREASHOLD, c > EQUALITYTREASHOLD);
        }
    }

    private Collection<LuvPoint> readImage(final String filename) throws IOException
    {
        BufferedImage orig = ImageIO.read(new File(filename));
        LUV[][] toLUVDArray = new LUVConverter().toLUVDArray(orig);
        Collection<LuvPoint> luvDArraytoLuvPoints = ImgUtls.luvDArraytoLuvPoints(toLUVDArray);
        return luvDArraytoLuvPoints;
    }

    /**
     * Test of build method, of class LUVHistorgam.
     */
    @Test
    public void testEqual() throws IOException
    {
        final String origname = "stone.gif";
        final String samplename = "stone.gif";
        
        checkSimilarity(origname, samplename, true);

    }

    @Test
    public void testScale() throws IOException
    {
        checkSimilarity("stone.gif", "stonesmall.gif", true);
    }

    @Test
    public void testRotate() throws IOException
    {
         checkSimilarity("stone.gif", "stonerot.gif", true);
    }

    @Test
    public void testFromSegm() throws IOException
    {
         checkSimilarity("stone.gif", "match_24357_inf.png", true);
    }


     @Test
    public void testCpart() throws IOException
    {
         checkSimilarity("stone.gif", "stonecpart.gif", true);
    }


    @Test
    public void testMidle() throws IOException
    {

        checkSimilarity("stone.gif", "rockswater.jpg", true);
    }

    @Test
    public void testNonEqual() throws IOException
    {

        checkSimilarity("stone.gif", "sand.jpg", false);
    }
    @Test
    public void testNonEqual2() throws IOException
    {

        checkSimilarity("stone.gif", "match_24330_1.41421356237.png", false);
    }
    @Test
    public void testWhite() throws IOException
    {
//        LUVHistorgam orig = LUVHistorgam.build(readImage("stone.gif"));
//        LUVHistorgam sample = LUVHistorgam.build(readImage("match_11_20.0.bmp"));
//
//        final double c = orig.distcompare(sample);
//
//        assertTrue(c+">"+EQUALITYTREASHOLD, c > EQUALITYTREASHOLD);
        
        checkSimilarity("stone.gif", "match_11_20.0.bmp", false);
    }

}
