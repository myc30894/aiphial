/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.*;
import java.util.Collection;
import static ru.nickl.meanShift.direct.PointUtils.*;

/**
 * Простейшиая реальзация {@link MeanShiftFilter}
 * @author nickl
 */
public class SimpleMSFilter extends BaseMeanShiftFilter
{

    /**
     * {@link NearestFinder}, используемый в методе
     * {@link #calkMh(ru.nickl.meanShift.direct.Point)}
     */
    protected  NearestFinder nearestFinder;
    private LUV nulllUV = new LUV(0.0F, 0.0F, 0.0F);
   /**
    * Исходные данные изображения.
    * используются многими методами и поэтому выносятся как поле,
    * хотя по логике не отражают состояние объекта и полем не являются
    */
    protected  LuvData LUVArray = null;
    /**
     * Результирующим данные изображения, то есть то что длжен вернуть метод
     * {@link #filter(ru.nickl.meanShift.direct.LuvData)
     * в результате своей работы
     */
    protected  LuvData resultLUVData = null;

    public LuvData filter(LuvData rawData)
    {

        this.LUVArray = rawData;

        int height = rawData.getHeight();
        int width = rawData.getWidth();

        resultLUVData = new LuvData(width, height);

        nearestFinder = new DimNearestFinder(LUVArray, squareRange, colorRange);


        int pointNumber = 1;
        int totalPoints = height * width;

        for (short y = 0; y < height; y++)
        {
            for (short x = 0; x < width; x++)
            {
                moveOnePoint(x, y);

                fireProgressListener((pointNumber += 100) / totalPoints);

            }
        }

        nearestFinder = null;
        this.LUVArray = null;
        LuvData result = this.resultLUVData; this.resultLUVData = null;
        return result;
    }

    protected Point calkMh(Point old)
    {
        Collection<Point> nearest = nearestFinder.getNearest(old);

        //mean.set(0, 0, 0, 0, 0); 
        Point mean = new Point((short) 0, (short) 0, new LUV(0, 0, 0));

        for (Point point : nearest)
        {
            mean.incrBy(point.minus(old));
        }

        if (nearest.size() != 0)
        {
            mean.divide(nearest.size());
        }

        return mean;
    }

    protected void moveOnePoint(short x, short y)
    {
        Point curPossition = new Point(x, y, LUVArray.getLUV(x, y).clone());
        Point Mh;
        int n = 1000;
        do
        {
            Mh = calkMh(curPossition);
            curPossition.incrBy(Mh);
        } while (Dim(Mh.c, nulllUV) > 0.01f && n-- > 0);
        resultLUVData.setLUV(x, y, curPossition.c);
    }
}
