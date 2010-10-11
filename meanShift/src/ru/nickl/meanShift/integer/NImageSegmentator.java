/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.integer;

import ru.nickl.meanShift.direct.filter.ProgressListener;
import ru.nickl.meanShift.direct.LUVConverter;
import ru.nickl.meanShift.*;
import ru.nickl.meanShift.direct.LUV;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.MeanShiftImageProcessor;
import static java.lang.Math.*;

/**
 *
 * @author nickl
 */
public class NImageSegmentator implements MeanShiftImageProcessor
{

    public static final int scaleK = 50;
    protected  NLUV[][] LUVArray;
    protected  LuvData resultLUVArray;
    protected  int height;
    protected  int width;
    protected short squareRange =0;
    protected  float colorRange =0;
    protected  int colorRangeP2;
    ProgressListener progressListener = null;


    protected  NLUV toNLUV(LUV arg)
    {
        return new NLUV((short) ((arg.l / colorRange) * scaleK), (short) ((arg.u / colorRange) * scaleK), (short) ((arg.v / colorRange) * scaleK));
    }

   protected  LUV toLUV(NLUV arg)
    {
        return new LUV(arg.l * colorRange / scaleK, arg.u * colorRange / scaleK, arg.v * colorRange / scaleK);
    }

    public void setColorRange(float colorRange)
    {
        this.colorRange = colorRange;
        this.colorRangeP2 = (scaleK * scaleK);
    }

    public void process()
    {
        resultLUVArray = new LuvData(width, height);
        

        int pointNumber = 1;
        int totalPoints = height * width;

        for (short y = 0; y < height; y++)
        {
            for (short x = 0; x < width; x++)
            {
                NPoint curPossition = calkEndPoint(x, y);
                resultLUVArray.setLUV(x, y, toLUV(curPossition.c));

                fireProgressListener((pointNumber += 100) / totalPoints);

            }
        }
    }
    
    
    protected NPoint calkEndPoint(short x, short y)
    {
        NLUV nulllUV = new NLUV((short) 0, (short) 0, (short) 0);
        NPoint curPossition = new NPoint(x, y, LUVArray[y][x].clone());

        NPoint Mh;
        int n = 1000;
        do
        {
            Mh = calkMh(curPossition);
            curPossition.incrBy(Mh);
        } while (Dim(Mh.c, nulllUV) > 3 && n-- > 0);
        return curPossition;
    }
    
    protected NPoint calkMh(NPoint old)
    {

        List<NPoint> nearest = getNearest(old);

        //mean.set(0, 0, 0, 0, 0); 
        NPoint mean = new NPoint((short) 0, (short) 0, new NLUV((short) 0, (short) 0, (short) 0));

        for (NPoint point : nearest)
        {
            mean.incrBy(point.minus(old));
        }

        if (nearest.size() != 0)
        {
            mean.divide(nearest.size());
        }
        return mean;
    }

    protected List<NPoint> getNearest(NPoint old)
    {
        List<NPoint> pWSS = getPointsWithinSpartialSquare(old.x, old.y);

        List<NPoint> result = new ArrayList<NPoint>();

        for (NPoint point : pWSS)
        {
            if (Dim(point.c, old.c) <= colorRangeP2)
            {
                result.add(point);
            }
        }

        return result;
    }

    protected List<NPoint> getPointsWithinSpartialSquare(short x, short y)
    {
        List<NPoint> result = new ArrayList<NPoint>();

        for (short y0 = (short) (y - squareRange); y0 < y + squareRange; y0++)
        {

            if (y0 >= 0 && y0 < height)
            {

                for (short x0 = (short) (x - squareRange); x0 < x + squareRange; x0++)
                {
                    if (x0 >= 0 && x0 < width)
                    {
                        result.add(new NPoint(x0, y0, LUVArray[y0][x0]));
                    }
                }
            }
        }
        return result;
    }

    protected static int Dim(NLUV a, NLUV b)
    {
        return p2(a.l - b.l) + p2(a.u - b.u) + p2(a.v - b.v);
    }

    protected static int p2(int a)
    {
        return a * a;
    }

    public void setSourceImage(BufferedImage sourceImage)
    {
        
        if(squareRange==0 || colorRange ==0)
            throw new IllegalStateException("squareRange and colorRange hasn't ben setted");
        

        height = sourceImage.getHeight();
        width = sourceImage.getWidth();

        LUVConverter lUVConverter = new LUVConverter();

        LuvData toLUVDArray = new LuvData(lUVConverter.toLUVDArray(sourceImage));

        LUVArray = new NLUV[toLUVDArray.getHeight()][toLUVDArray.getWidth()];

        for (int i = 0; i < toLUVDArray.getHeight(); i++)
        {
            

            for (int j = 0; j < toLUVDArray.getWidth(); j++)
            {
                LUVArray[i][j] = toNLUV(toLUVDArray.getLUV(j, i));

            }
        }
    }

    public BufferedImage getProcessedImage()
    {
        LUVConverter lUVConverter = new LUVConverter();

        return lUVConverter.LUVArrayToBufferedImage(resultLUVArray.getLUVArray());
    }

    public float getColorRange()
    {
        return colorRange;
    }

    public int getSquareRange()
    {
        return squareRange;
    }

    public void setSquareRange(short squareRange)
    {
        this.squareRange = squareRange;
    }

    protected void fireProgressListener(int proc)
    {
        if (progressListener != null)
        {
            progressListener.proc(proc);
        }
    }

    public ProgressListener getProgressListener()
    {
        return progressListener;
    }

    public void setProgressListener(ProgressListener progressListener)
    {
        this.progressListener = progressListener;
    }

    public LuvData getResultLUVArray() {
        return resultLUVArray;
    }

    public int getHeight() {
        return height;
    }

    public LuvData getLUVArray() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getWidth() {
        return width;
    }

}
