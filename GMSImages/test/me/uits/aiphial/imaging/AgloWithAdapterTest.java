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

package me.uits.aiphial.imaging;

import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.imaging.SegmentatorAdapter;
import me.uits.aiphial.imaging.ImgUtls;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.nickl.meanShift.direct.segmentator.RegionGrowingSegmentator;
import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack;
import me.uits.aiphial.general.basic.MeanShiftClusterer;
import me.uits.aiphial.general.dataStore.NDimPoint;
import static org.junit.Assert.*;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class AgloWithAdapterTest {

    public AgloWithAdapterTest() {
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
    public void testSegmentation() throws IOException
    {
        BufferedImage read = ImageIO.read(new File("DSC00104s400.bmp"));

        RegionGrowingSegmentator growingSegmentator = new RegionGrowingSegmentator();
        growingSegmentator.setEqualityRange(500);
        growingSegmentator.setSourceImage(read);


        AglomerativeClustererStack<LuvPoint> aglomerativeClusterer = new AglomerativeClustererStack<LuvPoint>();

        aglomerativeClusterer.setInitialClusterer(new SegmentatorAdapter(growingSegmentator));

        final MeanShiftClusterer<NDimPoint> meanShiftClusterer = new MeanShiftClusterer<NDimPoint>();

        meanShiftClusterer.setWindow(100f,100f,100f,100f,100f);

        aglomerativeClusterer.addClustererToQueue(meanShiftClusterer);

        aglomerativeClusterer.doClustering();

        BufferedImage result = ImgUtls.paintRegions(read.getWidth(), read.getHeight(), ImgUtls.asRegions(aglomerativeClusterer.getClusters()));
        final String outpic = "DSC00104s400clusters.bmp";

        ImageIO.write(result, "bmp", new File(outpic));

        System.out.println("check clustering result on "+outpic);




    }



}