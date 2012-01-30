/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scalarunner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import me.uits.aiphial.imaging.FastMatrixMS;
import me.uits.aiphial.imaging.Tools;

/**
 *
 * @author nickl
 */
public class NaiveSegmentationSampleJava
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        // read a buffered image from file
        BufferedImage srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"));
        // then create a Clusterer, FastMatrixMS is a simple Mean Shift Clusterer for images
        FastMatrixMS a = new FastMatrixMS(Tools.matrixFromImage(srcimg));

        // setup filter parametrs
        a.setColorRange(7f);
        a.setSquareRange((short)20);

        // process
        a.doClustering();

        // paint clusters on image
        BufferedImage img = Tools.paintClusters(srcimg.getWidth(), srcimg.getHeight(), a.getClusters(), false);

        // write results to file
        ImageIO.write(img, "bmp", new File("./out_.bmp"));
    }
}
