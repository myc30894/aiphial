/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift;

import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.MeanShiftImageProcessor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.LuvImageProcessor;
import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.filter.LUVFilter;
import ru.nickl.meanShift.direct.filter.LuvFilterImageProcessor;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor;
import ru.nickl.meanShift.direct.filter.FastMSFilter;
import ru.nickl.meanShift.direct.filter.FastMeanShiftFilterImageProcessor;
import ru.nickl.meanShift.direct.segmentator.Region;
import ru.nickl.meanShift.direct.segmentator.SimpleSegmentator;
import ru.nickl.meanShift.direct.filter.SimpleMSFilter;
import ru.nickl.meanShift.direct.filter.SobelFilter;
import ru.nickl.meanShift.direct.port.Ported2Filter;
import ru.nickl.meanShift.direct.port.moveport.PortedFilter;
import ru.nickl.meanShift.direct.port.moveport.PortedFilterMedOptimized;
import ru.nickl.meanShift.direct.port.moveport.PortedSegmentatorWithBoundaries;
import ru.nickl.meanShift.direct.port.moveport.RegionList;
import ru.nickl.meanShift.direct.segmentator.MeanShiftSegmentator;
import ru.nickl.meanShift.direct.segmentator.RegionGrowingSegmentator;
import ru.nickl.meanShift.direct.segmentator.Segmentator;
import ru.nickl.meanShift.direct.segmentator.SobelMeanShiftPOLESegmentator;
import ru.nickl.meanShift.direct.segmentator.SobelMeanShiftSegmentator;
import ru.nickl.meanShift.enhaus.FieldIterator;
import ru.nickl.meanShift.enhaus.FloatRange;
import ru.nickl.meanShift.enhaus.IntRange;
import ru.nickl.meanShift.enhaus.ProcessEnhaustioner;
import ru.nickl.meanShift.enhaus.WriteCountuorToFile;
import ru.nickl.meanShift.enhaus.WriteResultToFile;

/**
 *
 * @author nickl
 */
public class TestProcedures
{

    public String filename;
    public short sr;
    public float cr;

    public TestProcedures(String filename, short sr, float crf)
    {
        this.filename = filename;
        this.sr = sr;
        this.cr = crf;
    }

    public void run() throws IOException
    {
        // RGSegmentatorEnhaus();
        // SobelSegmentatorEnhaus();
        // POLESegmentatorEnhaus();
        // MSSegmentatorEnhaus();

        FastMSFilterEnhaus();

//        this.sr = 20;
//        this.cr = 7;
//        this.doTestnew(new FastMSFilter(20,10), new File("SFastFilt.bmp"));
        // this.doTestnew(new SimpleMSFilter(), new File("SimplBigFilt.bmp"));
        // this.doTestnew(new Ported2Filter(), new File("PortedFilt.bmp"));
        //  this.doTestnew(new PortedFilter(), new File("MovedFilt.bmp"));
    }

    public static TestProcedures getCur()
    {

        //String testfilename = "DSC00104s400.bmp";
        // String testfilename = "DSCN4909small100.bmp";

        // static String testfilename = "DSCN4909.bmp";

        String filename = "DSC00104s200.bmp";
        short sr = 10;
        float cr = 7f;


        return new TestProcedures(filename, sr, cr);
    }

    public void MSFilterEnhaus() throws IOException
    {
        MeanShiftFilter filter = new SimpleMSFilter();
        MeanShiftFilterImageProcessor<MeanShiftFilter> msfip = new MeanShiftFilterImageProcessor<MeanShiftFilter>(filter);
        msfip.setSourceImage(ImageIO.read(new File(filename)));
        ProcessEnhaustioner<LuvImageProcessor> pE =
                new ProcessEnhaustioner<LuvImageProcessor>(msfip,
                new FieldIterator<Float>("SquareRange", new FloatRange(10, 31, 10)),
                new FieldIterator<Float>("ColorRange", new FloatRange(7, 12, 1)));
        pE.process(new WriteResultToFile(new File(filename + "_" + msfip.getClass().getSimpleName() + "_" + filter.getClass().getSimpleName())));

    }

    public void FastMSFilterEnhaus() throws IOException
    {
        FastMSFilter filter = new FastMSFilter();

        FastMeanShiftFilterImageProcessor<FastMSFilter> msfip = new FastMeanShiftFilterImageProcessor<FastMSFilter>(filter);
        msfip.setSourceImage(ImageIO.read(new File(filename)));
        ProcessEnhaustioner<LuvImageProcessor> pE =
                new ProcessEnhaustioner<LuvImageProcessor>(msfip,
                new FieldIterator<Float>("SquareRange", new FloatRange(10, 31, 10)),
                new FieldIterator<Float>("ColorRange", new FloatRange(7, 12, 1)),
                new FieldIterator<Float>("DiffSquare", new FloatRange(2, 20, 5)),
                new FieldIterator<Float>("DiffColor", new FloatRange(1, 12, 2)));
        pE.process(new WriteResultToFile(new File(filename + "_" + msfip.getClass().getSimpleName() + "_" + filter.getClass().getSimpleName()), 2));

    }

    public void MSSegmentatorEnhaus() throws IOException
    {
        MeanShiftFilter filter = new FastMSFilter(5, 10);
        Segmentator msip = new SimpleSegmentator(filter, 20);
        msip.setSourceImage(ImageIO.read(new File(filename)));
        ProcessEnhaustioner<Segmentator> pE = new ProcessEnhaustioner<Segmentator>(msip,
                new FieldIterator<Float>("SquareRange", new FloatRange(10, 31, 10)),
                new FieldIterator<Float>("ColorRange", new FloatRange(7, 12, 1)),
                new FieldIterator<Float>("EqualityRange", new FloatRange(0.1f, 10.1f, 1f)));
        pE.process(new WriteCountuorToFile(new File(filename + "_" + msip.getClass().getSimpleName() + "_" + filter.getClass().getSimpleName()), 2));
    }

    public void POLESegmentatorEnhaus() throws IOException
    {
        MeanShiftFilter filter = new FastMSFilter();
        Segmentator msip2 = new SobelMeanShiftPOLESegmentator(filter);
        msip2.setSourceImage(ImageIO.read(new File(filename)));
        ProcessEnhaustioner<Segmentator> pE2 = new ProcessEnhaustioner<Segmentator>(msip2,
                new FieldIterator<Float>("SquareRange", new FloatRange(10, 31, 10)),
                new FieldIterator<Float>("ColorRange", new FloatRange(7, 12, 1)),
                new FieldIterator<Float>("EqualityRange", new FloatRange(0.1f, 10.1f, 1f)),
                new FieldIterator<Float>("GradTreshold", new FloatRange(500, 1200, 100)));
        pE2.process(new WriteCountuorToFile(new File(filename + "_" + msip2.getClass().getSimpleName() + "_" + filter.getClass().getSimpleName()), 2));
    }

    public void SobelSegmentatorEnhaus() throws IOException
    {
        MeanShiftFilter filter = new FastMSFilter();
        Segmentator msip2 = new SobelMeanShiftSegmentator(filter, 40);
        msip2.setSourceImage(ImageIO.read(new File(filename)));
        ProcessEnhaustioner<Segmentator> pE2 = new ProcessEnhaustioner<Segmentator>(msip2,
                new FieldIterator<Float>("SquareRange", new FloatRange(10, 31, 10)),
                new FieldIterator<Float>("ColorRange", new FloatRange(7, 12, 1)),
                new FieldIterator<Float>("EqualityRange", new FloatRange(0.1f, 10.1f, 1f)),
                new FieldIterator<Float>("GradTreshold", new FloatRange(500, 5000, 500)));
        pE2.process(new WriteCountuorToFile(new File(filename + "_" + msip2.getClass().getSimpleName() + "_" + filter.getClass().getSimpleName()), 2));
    }

    public void RGSegmentatorEnhaus() throws IOException
    {

        Segmentator msip2 = new RegionGrowingSegmentator();
        msip2.setSourceImage(ImageIO.read(new File(filename)));
        ProcessEnhaustioner<Segmentator> pE2 = new ProcessEnhaustioner<Segmentator>(msip2,
                new FieldIterator<Float>("EqualityRange", new FloatRange(240f, 500f, 20f)),
                new FieldIterator<Integer>("MinRegionSize", new IntRange(50, 50, 5)));

        pE2.process(new WriteCountuorToFile(new File(filename + "_" + msip2.getClass().getSimpleName())));
    }

    public void SegmentatorTester(MeanShiftSegmentator segmentator, File file) throws IOException
    {

        long begining = System.currentTimeMillis();
        BufferedImage sourceImg = ImageIO.read(new File(filename));

        segmentator.setSourceImage(sourceImg);


        segmentator.process();
        BufferedImage filtredImage = segmentator.getProcessedImage();
        BufferedImage regionsImage = filtredImage;//new BufferedImage(filtredImage.getWidth(), filtredImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (Region region : segmentator.getRegions())
        {
            for (Point point : region.getCountour())
            {
                regionsImage.setRGB(point.x, point.y, 0xffffff);
            }
        }
        long now = System.currentTimeMillis();
        System.out.println(now - begining);
        ImageIO.write(regionsImage, "bmp", file);
    // ImageIO.write(filtredImage, "bmp", file);
    }

    public void SobelTester() throws IOException
    {
        LuvFilterImageProcessor<LUVFilter> lfip = new LuvFilterImageProcessor<LUVFilter>(new SobelFilter());
        BufferedImage sourceImg = ImageIO.read(new File(filename));
        lfip.setSourceImage(sourceImg);
        long begining = System.currentTimeMillis();
        lfip.process();
        System.out.println();
        BufferedImage filtredImage = lfip.getProcessedImage();


        PrintWriter pw = new PrintWriter("sobel.txt");
        LuvData resultLUVArray = lfip.getResultLUVArray();

        for (int y = resultLUVArray.getHeight() - 1; y > 0; y--)
        {
            for (int x = 0; x < resultLUVArray.getWidth(); x++)
            {
                double l = resultLUVArray.getLUV(x, y).l;

                if (l < 30)
                {
                    l = 0;
                }

                pw.print(l + "\t");
            }
            pw.println();
        }

        pw.close();


        long now = System.currentTimeMillis();
        System.out.println(now - begining);
        ImageIO.write(filtredImage, "bmp", new File("Sobel.bmp"));
    }

    public void doTestnew(MeanShiftFilter imageSegmentator, File outFile) throws IOException
    {

        MeanShiftImageProcessor ofip = new MeanShiftFilterImageProcessor<MeanShiftFilter>(imageSegmentator);


        BufferedImage sourceImg = ImageIO.read(new File(filename));

        long begining = System.currentTimeMillis();



        ofip.setSquareRange(sr);
        ofip.setColorRange(cr);
        ofip.setSourceImage(sourceImg);

        ofip.process();
        System.out.println();

        BufferedImage filtredImage = ofip.getProcessedImage();

        long now = System.currentTimeMillis();
        System.out.println(now - begining);

        ImageIO.write(filtredImage, "bmp", outFile);
    }

    public void doTestold(MeanShiftImageProcessor imageSegmentator, File outFile) throws IOException
    {


        BufferedImage sourceImg = ImageIO.read(new File(filename));

        long begining = System.currentTimeMillis();



        imageSegmentator.setSquareRange(sr);
        imageSegmentator.setColorRange(cr);
        imageSegmentator.setSourceImage(sourceImg);

        imageSegmentator.process();
        System.out.println();

        BufferedImage filtredImage = imageSegmentator.getProcessedImage();

        long now = System.currentTimeMillis();
        System.out.println(now - begining);

        ImageIO.write(filtredImage, "bmp", outFile);
    }

    public void drawBoudaries(PortedSegmentatorWithBoundaries portedSegmentator, File file) throws IOException
    {
        int width = portedSegmentator.getWidth();
        int height = portedSegmentator.getHeight();
        RegionList regionList = portedSegmentator.GetBoundaries();
        int ri = regionList.GetRegionIndeces(0);
        int[] regionIndexes = regionList.indexTable;
        int boundaryPointCount = 0;
        int numRegions = regionList.GetNumRegions();
        for (int i = 0; i < numRegions; i++)
        {
            boundaryPointCount += regionList.GetRegionCount(i);
        }
        List<java.awt.Point> points = new ArrayList<java.awt.Point>(boundaryPointCount);
        for (int i = 0; i < boundaryPointCount; i++)
        {
            points.add(new java.awt.Point(regionIndexes[ri + i] % width, regionIndexes[ri + i] / width));
        }
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (java.awt.Point point : points)
        {
            output.setRGB(point.x, point.y, 0xffffff);
        }
        ImageIO.write(output, "bmp", file);
    }
}
