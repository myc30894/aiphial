/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanshiftport.test;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import ru.nickl.meanshiftport.MeanShiftImageProcessorPort;
import ru.nickl.meanshiftport.RegionList;
import ru.nickl.meanshiftport.SpeedUpLevel;
import ru.nickl.meanshiftport.imageType;
import static ru.nickl.meanShift.ImageUtls.*;

/**
 *
 * @author nickl
 */
public class Main
{

    static String testfilename = "DSCN4909s400.bmp";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            
            

            BufferedImage sourceImg = ImageIO.read(new File(testfilename));


            long begining = System.currentTimeMillis();

            long now;


            //byte[] pixels = BGRtoRGB(((DataBufferByte) sourceImg.getRaster().getDataBuffer()).getData());




            int width = sourceImg.getWidth();
            int height = sourceImg.getHeight();

            int[] pixels = new int[height * width * 3];

            PixelGrabber pg = new PixelGrabber(sourceImg, 0, 0, width, height, true);

            pg.grabPixels();

            int[] gra = (int[]) pg.getPixels();

              
            
            //PrintStream spixelsStram = new PrintStream("spicels.txt");
            
            for (int i = 0; i < gra.length; i++)
            {

                int r = ((gra[i] & 0x00ff0000) >> 16);
                int g = ((gra[i] & 0x0000ff00) >> 8);
                int b = ((gra[i] & 0x000000ff) >> 0);
                
                //spixelsStram.printf("[%d] r=\t%d g=\t%d b=\t%d\n", i,r,g,b);

                pixels[3 * i] = r;
                pixels[3 * i + 1] = g;
                pixels[3 * i + 2] = b;

            }
            
            //spixelsStram.close();

             now = System.currentTimeMillis();
            System.out.println(now-begining);
            begining = now;

            ImageIO.write(filter(pixels, height, width, gra), "bmp", new File("portedfilt.bmp"));

          //  ImageIO.write(meanshift(pixels, height, width, SpeedUpLevel.NO_SPEEDUP), "bmp", new File("portedsegm.bmp"));

/*
            now = System.currentTimeMillis();
            System.out.println(now-begining);
            begining = now;

            ImageIO.write(meanshift(pixels, height, width, SpeedUpLevel.MED_SPEEDUP), "bmp", new File("fsu1.bmp"));
      
            now = System.currentTimeMillis();
            System.out.println(now-begining);
            begining = now;
            
            ImageIO.write(meanshift(pixels, height, width, SpeedUpLevel.HIGH_SPEEDUP), "bmp", new File("fsu2.bmp"));
        */
            now = System.currentTimeMillis();
            System.out.println(now-begining);            
       
        } catch (InterruptedException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private static BufferedImage filter(final int[] pixels, int height, int width, int[] gra) throws FileNotFoundException, IOException
    {
        //meanshift(pixels, height, width);
        //meanshift(pixels,height, width);
        MeanShiftImageProcessorPort msipp = new MeanShiftImageProcessorPort();
        msipp.DefineImage(pixels, imageType.COLOR, height, width);
        msipp.Filter(20, 7f, SpeedUpLevel.HIGH_SPEEDUP);
        int[] result = new int[pixels.length];
        msipp.GetResults(result);
        int[] resPixels = new int[gra.length];
        //PrintStream opixelsStream = new PrintStream("opicels.txt");
        for (int i = 0; i < resPixels.length; i++)
        {
            //opixelsStream.printf("[%d] r=\t%d g=\t%d b=\t%d\n", i, result[3 * i], result[3 * i + 1], result[3 * i + 2]);
            resPixels[i] = (0xff << 24) | (result[3 * i] << 16) | (result[3 * i + 1] << 8) | result[3 * i + 2];
        }
        //opixelsStream.close();
        //File fout = new File("ints.txt");
        //PrintStream fos = new PrintStream(fout);
       /* for (int i = 0; i < resPixels.length; i++)
        {
            fos.print('[');
            fos.print(i);
            fos.println(']');
            fos.println(gra[i]);
            fos.println(resPixels[i]);
            fos.println();
        }
        fos.close();
        System.out.println(fout.getPath());*/
        
        return toImage(resPixels, width, height);
        
        //ImageIO.write(toImage(resPixels, width, height), "bmp", new File("/home/nickl/NetBeansProjects/cpp/meanShift/filt.bmp"));
    }

    private static BufferedImage meanshift(int[] pixels, int height, int width,SpeedUpLevel speedup) throws IOException
    {
        MeanShiftImageProcessorPort msipp = new MeanShiftImageProcessorPort();
        msipp.DefineImage(pixels, imageType.COLOR, height, width);
        
        msipp.Segment(30, 7f, 20,speedup);
        RegionList regionList = msipp.GetBoundaries();
        int ri = regionList.GetRegionIndeces(0);
        int[] regionIndexes = regionList.indexTable;
        int boundaryPointCount = 0;
        int numRegions = regionList.GetNumRegions();
        for (int i = 0; i < numRegions; i++)
        {
            boundaryPointCount += regionList.GetRegionCount(i);
        }
        List<Point> points = new ArrayList<Point>(boundaryPointCount);
        for (int i = 0; i < boundaryPointCount; i++)
        {
            points.add(new Point(regionIndexes[ri + i] % width, regionIndexes[ri + i] / width));
        }
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (Point point : points)
        {
            output.setRGB(point.x, point.y, 0xffffff);
        }
        return output;
        //ImageIO.write(output, "bmp", new File("/home/nickl/NetBeansProjects/cpp/meanShift/count.bmp"));
    }
}
