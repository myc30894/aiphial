/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift;

import ru.nickl.meanShift.direct.LUVConverter;
import ru.nickl.meanShift.direct.LUV;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nickl
 */
public class LUVConverterTest {

    private static final  File TEST_FILE_NAME = TestData.getSourceImage();
    private static final  String OUTPUT_FILE_NAME = "DSCN4909small100luvandback.bmp";
    
    public LUVConverterTest() {
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
    public void testToLUVArrayAndBack() throws IOException
    {
        System.out.println("toLUVArray");
        BufferedImage bufferedImage = ImageIO.read(TEST_FILE_NAME);
        LUVConverter instance = new LUVConverter();
        
        LUV[] result = instance.toLUVArray(bufferedImage);

        BufferedImage out = instance.LUVArrayToBufferedImage(result, bufferedImage.getHeight(), bufferedImage.getWidth());
        
        ImageIO.write(out, "BMP", new File(TestData.getOutputFolder(),OUTPUT_FILE_NAME));
        
        
        System.out.println("check manually visual equality of "+TEST_FILE_NAME.getName()+" "+OUTPUT_FILE_NAME);
    }

    

}