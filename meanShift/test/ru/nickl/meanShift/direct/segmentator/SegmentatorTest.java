/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.nickl.meanShift.TestData;
import ru.nickl.meanShift.direct.Point;

import static org.junit.Assert.*;

/**
 *
 * @author nickl
 */
@Ignore public class SegmentatorTest
{

    protected  File sourceFile = TestData.getSourceImage();
    protected  File folder = TestData.getOutputFolder();

    public SegmentatorTest()
    {
    }


    /**
     * Test of process method, of class SimpleSegmentator.
     */
    
    public void testSegmentator(Segmentator segmentator)
    {
        try
        {
            long begining = System.currentTimeMillis();
            BufferedImage sourceImg = ImageIO.read(sourceFile);
            segmentator.setSourceImage(sourceImg);
            segmentator.process();
            BufferedImage filtredImage = segmentator.getProcessedImage();
            BufferedImage regionsImage = filtredImage; //new BufferedImage(filtredImage.getWidth(), filtredImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (Region region : segmentator.getRegions())
            {
                for (Point point : region.getCountour())
                {
                    regionsImage.setRGB(point.x, point.y, 0xffffff);
                }
            }
            long now = System.currentTimeMillis();
            System.out.println(now - begining);
            ImageIO.write(regionsImage, "bmp",new File(folder, this.getClass().getSimpleName()+".bmp"));
        } catch (IOException ex)
        {
            fail(ex.getMessage());
        }
    }
}