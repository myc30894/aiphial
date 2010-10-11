/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.TreeMap;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.PointUtils;
import ru.nickl.meanShift.direct.filter.BaseLuvImageProcessor;
import ru.nickl.meanShift.direct.filter.SpartialNearestFinder;
//import ru.nickl.meanShift.direct.segmentator.IndexedRegionPU.IndexedRegion;

/**
 * Сегментатор, использующий алгорим разрастания решионов в пространстве LUV
 * @author nickl
 */
public class RegionGrowingSegmentator extends BaseLuvImageProcessor implements Segmentator
{

    private RegionGrowingAndAbsorbtionPU regionManager = new RegionGrowingAndAbsorbtionPU()
    {

        @Override
        protected void growRegion(Point first, IndexedRegion region)
        {

            LUV averpoint = first.c.clone();

            TreeMap<Double,Point> procList = new TreeMap<Double, Point>();

            //Deque<Point> procList = new LinkedList<Point>();
            procList.put(0., first);

            while (!procList.isEmpty())
            {
                Point point = procList.pollFirstEntry().getValue();

                for (Point nearestPoint : getNearestFinder().getNearest(point))
                {
                    if (getPointRegion(nearestPoint) == null && PointUtils.Dim(averpoint, nearestPoint.c) < getEqualityRange())
                    {
                        final int size = region.getPoints().size();

                        averpoint = averpoint.mult(size).plus(nearestPoint.c).div(size + 1);

                        region.add(nearestPoint);
                        procList.put(PointUtils.Dim(averpoint, nearestPoint.c), nearestPoint);
                    }
                }
            }
        }
    };

    public RegionGrowingSegmentator()
    {
        regionManager.setNearestFinder(new SpartialNearestFinder((short) 1, null));
    }

    /**
     * Устанавлиевает значение цветовой еквивалентности точек,
     * которые должны быть включены в один регион. обычны значения 100-500
     * @param er
     */
    public void setEqualityRange(double er)
    {
        regionManager.setEqualityRange(er);
    }

    /**
     * Возвращвет значение цветовой еквивалентности точек
     * @return
     */
    public double getEqualityRange()
    {
        return regionManager.getEqualityRange();
    }

    /**
     * Минимальный размер региона, регионы меньшие данного
     * должны быть расформимрованы
     * @param mrs
     */
    public void setMinRegionSize(int mrs)
    {
        regionManager.setMinRegionSize(mrs);
    }

    /**
     * Возвращает минимальный размер региона, регионы меньшие данного
     * должны быть расформимрованы
     * @return
     */
    public int getMinRegionSize()
    {
        return regionManager.getMinRegionSize();
    }

    public void process()
    {
        regionManager.setData(LUVArray);
        resultLUVArray = LUVArray;
        regionManager.formRegions();

    }

    public Collection<Region> getRegions()
    {
        return regionManager.getRegions();
    }
}
