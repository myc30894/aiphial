/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.enhaus;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.imageio.ImageIO;
import ru.nickl.meanShift.direct.LuvImageProcessor;
import ru.nickl.meanShift.direct.MeanShiftImageProcessor;
import ru.nickl.meanShift.direct.filter.LUVFilter;
import ru.nickl.meanShift.direct.filter.LuvFilterImageProcessor;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor;
import ru.nickl.meanShift.direct.filter.SimpleMSFilter;
import ru.nickl.meanShift.direct.port.Ported3Filter;
import ru.nickl.meanShift.direct.port.moveport.PortedFilter;

/**
 *
 * @author nickl
 */
public class MeanShiftFilterExhaustioner implements Iterable<MeanShiftFilter>
{

    private MeanShiftFilter meanShiftFilter;

    public MeanShiftFilterExhaustioner(MeanShiftFilter meanShiftFilter, int minSpartialRange, int maxSpartialRange, int srStep, double minColorRange, double maxColorRange, double crStep)
    {
        this.meanShiftFilter = meanShiftFilter;
        this.minSpartialRange = (short) minSpartialRange;
        this.maxSpartialRange = (short) maxSpartialRange;
        this.srStep = (short) srStep;
        this.minColorRange = minColorRange;
        this.maxColorRange = maxColorRange;
        this.crStep = crStep;
    }
    private short minSpartialRange;
    private short maxSpartialRange;
    private short srStep;
    private double minColorRange;
    private double maxColorRange;
    private double crStep;

    public void exhaus(File src, File folder) throws IOException
    {


        if (folder.exists())
        {
            for (File file : folder.listFiles())
            {
                file.delete();
            }
        }

        folder.mkdir();



        for (MeanShiftFilter msf : this)
        {
            doTestnew(src, msf, new File(folder, msf.getSquareRange() + "_" + msf.getColorRange() + ".bmp"));
        }





    }

    private static void doTestnew(File srcFile, LUVFilter imageSegmentator, File outFile) throws IOException
    {

        LuvImageProcessor ofip = new LuvFilterImageProcessor(imageSegmentator);


        BufferedImage sourceImg = ImageIO.read(srcFile);

        long begining = System.currentTimeMillis();


        ofip.setSourceImage(sourceImg);
        /*
        imageSegmentator.setProgressListener(new ProgressListener() {
        public void proc(int proc)
        {
        System.out.print("\r"+proc+"%");
        System.out.flush();
        }
        });
         */
        ofip.process();
        System.out.println();

        BufferedImage filtredImage = ofip.getProcessedImage();

        long now = System.currentTimeMillis();
        System.out.println(now - begining);

        ImageIO.write(filtredImage, "bmp", outFile);
    }

    public Iterator<MeanShiftFilter> iterator()
    {
        return new MSfilterIterator();
    }

    private class MSfilterIterator implements Iterator<MeanShiftFilter>
    {

        private short s = minSpartialRange;
        private double c = minColorRange;

        public MSfilterIterator()
        {
        }

        public boolean hasNext()
        {
            return !(c >= maxColorRange && s >= maxSpartialRange);
        }

        public MeanShiftFilter next()
        {
            if (c > maxColorRange && s > maxSpartialRange)
            {
                throw new NoSuchElementException("iteration has no more elements");
            }

            if (s <= maxSpartialRange && c >= maxColorRange)
            {
                c = minColorRange;
                s += srStep;
            }



            meanShiftFilter.setColorRange((float) c);
            meanShiftFilter.setSquareRange(s);


            c += crStep;



            return meanShiftFilter;
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Not supported");
        }
    }
}
