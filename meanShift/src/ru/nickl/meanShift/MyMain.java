/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift;

import ru.nickl.meanShift.direct.MeanShiftImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.filter.FastMSFilter;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor;
import ru.nickl.meanShift.direct.filter.SimpleMSFilter;
import ru.nickl.meanShift.direct.segmentator.MeanShiftSegmentator;
import ru.nickl.meanShift.direct.segmentator.Region;
import ru.nickl.meanShift.direct.segmentator.SimpleSegmentator;

/**
 *
 * @author nickl
 */
public class MyMain
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {

        TestProcedures tp = null;

        if (args.length == 3)
        {
            String filename = args[0];
            short sr = Short.parseShort(args[1]);
            float cr = Float.parseFloat(args[2]);
            tp = new TestProcedures(filename, sr, cr);
        } else
        {
            tp = TestProcedures.getCur();
        }

        tp.run();

    //POLESegmentatorEnhaus();



    // SobelSegmentatorEnhaus();













    /*
    
    SobelMeanShiftPOLESegmentator smspoles = new SobelMeanShiftPOLESegmentator(new SimpleMSFilter());
    
    smspoles.setSquareRange((short) 20);
    smspoles.setColorRange(7);
    smspoles.setEqualityRange(10);
    smspoles.setGradTreshold(1200);
    
    SegmentatorTester(smspoles, new File("smspoles.bmp"));
    
     */



    //new MeanShiftFilterExhaustioner(new SimpleMSFilter(),3,20,1,5,10,0.5).exhaus(new File(filename),new File("filt"));

    //SegmentatorTester(new SimpleSegmentator(new PortedSegmentatorWithBoundaries(20)), new File("ss.bmp"));

    //SobelTester();

    // new MeanShiftSegmentatorExhaustioner().exhaus();



    }

    public static void main1(String[] args)
            throws IOException
    {
        //создание обработчика, использующщего
        //простой фильтр среднего сдвига
        MeanShiftImageProcessor ofip =
                new MeanShiftFilterImageProcessor<MeanShiftFilter>(
                new SimpleMSFilter());

        //загрузка исходного изображения
        BufferedImage sourceImg =
                ImageIO.read(new File("picture.bmp"));

        //настройка параметров
        ofip.setSquareRange((short) 7.0);
        ofip.setColorRange(8);
        ofip.setSourceImage(sourceImg);

        //выполнение фильтра
        ofip.process();

        //получение обработанного изображения
        BufferedImage filtredImage =
                ofip.getProcessedImage();

        //запись обработанного изображения в файл
        ImageIO.write(filtredImage, "bmp",
                new File("outpicture.bmp"));
    }

    public static void main2(String[] args)
            throws IOException
    {
        //Создание сегментатора
        MeanShiftSegmentator segmentator = 
                new SimpleSegmentator(
                            new FastMSFilter());
        
        //загрузка исходного изображения
        BufferedImage sourceImg =
                ImageIO.read(new File("picture.bmp"));
        segmentator.setSourceImage(sourceImg);

        //Выполнение сегментации
        segmentator.process();

        //получение обработанного фильтром
        //среднего сдвига изображения
        BufferedImage regionsImage =
                segmentator.getProcessedImage();

        //выделение границ регионов на
        //обработанном изображении
        for (Region region : segmentator.getRegions())
        {
            for (Point point : region.getCountour())
            {
                regionsImage.setRGB(
                        point.x,
                        point.y,
                        0xffffff);
            }
        }
        
        //запись сегментированного изображения в файл
        ImageIO.write(regionsImage, "bmp",
                new File("outpicture.bmp"));
    }
}
