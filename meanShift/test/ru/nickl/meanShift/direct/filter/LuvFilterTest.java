/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.filter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ru.nickl.meanShift.TestData;
import static org.junit.Assert.*;

/**
 *
 * @author nickl
 */
@Ignore("Isnt a test") public class LuvFilterTest {

    protected  File sourseFile = TestData.getSourceImage();
    protected  File folder = TestData.getOutputFolder();

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }



    /**
     * Тестирует фильтр и записывает результат в файл с именем тестирующего класса
     * и папку, указанную в настроцках
     * @param filter
     */
    public void testFilter(LUVFilter filter)
    {
        try
        {
            LuvFilterImageProcessor<LUVFilter> lfip = new LuvFilterImageProcessor<LUVFilter>(filter);
            BufferedImage sourceImg = ImageIO.read(sourseFile);
            lfip.setSourceImage(sourceImg);
        
            lfip.process();
            System.out.println();
            BufferedImage filtredImage = lfip.getProcessedImage();
          
            
            ImageIO.write(filtredImage, "bmp", new File(folder,this.getClass().getSimpleName()+".bmp"));



        } catch (IOException ex)
        {
            fail(ex.getMessage());
        }

    }

}
