/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor;
import ru.nickl.meanShift.direct.filter.NearestFinder;
import ru.nickl.meanShift.direct.filter.SobelFilter;
import ru.nickl.meanShift.direct.filter.SpartialNearestFinder;

/**
 * path of least effort
 * @author nickl
 */
public class SobelMeanShiftPOLESegmentator extends AbstractMeanShiftSegmentator
{

    private PointUniter regionManager = new PointUniterImpl();
    private LuvData gradientData;
    private final int minRegionSize = 60;

    public SobelMeanShiftPOLESegmentator()
    {
    }
    private double equalityRange = 1;
    private double gradTreshold = 5000;

    public SobelMeanShiftPOLESegmentator(MeanShiftFilter filter)
    {
        super(filter);
    }
    private SobelFilter sobelFilter = new SobelFilter();


    
    /**
     * Обрабатывает изображение и разбивает его на отдельные области
     */
    @Override
    public void process()
    {
     
            //pw = new PrintWriter("points.txt");
            super.process();
            regionManager.formRegions();
            //pw.close();
        
    }

    @Override
    protected void rebuildSources()
    {
        gradientData = sobelFilter.filter(super.getLUVArray());
        regionManager.setData(super.getResultLUVArray());
    }

    private class PointUniterImpl extends RegionGrowingAndAbsorbtionPU
    {

        public PointUniterImpl()
        {
            super(minRegionSize);
        }

        private void reinit()
        {

            nearestFinder = new SpartialNearestFinder(data, (short) 1, new SpartialNearestFinder.Criteria()
            {

                public boolean isNear(Point a, Point b)
                {
                    LUV d = a.c.minus(b.c);
                    double C = equalityRange;
                    if (Math.abs(d.l) < C && Math.abs(d.u) < C && Math.abs(d.v) < C)
                    {
                        return true;
                    } else
                    {
                        return false;
                    }
                }
            });

        }

        public void formRegions()
        {

            reinit();

            super.formRegions();
        }

        @Override
        protected void growRegion(Point first, IndexedRegion region)
        {

         
            double gradLimit = getGradTreshold();

            TreeMap<Double, Point> procList = new TreeMap<Double, Point>();


            procList.put(gradientData.getLUV(first.x, first.y).l, first);

            while (!procList.isEmpty())
            {
                Entry<Double, Point> firstEntry = procList.pollFirstEntry();
                Point point = firstEntry.getValue();

            

                for (Point nearestPoint : nearestFinder.getNearest(point))
                {
                    if (getPointRegion(nearestPoint)==null)
                    {                        
                        region.add(nearestPoint);                        
                        
                        double pointgrad = gradientData.getLUV(nearestPoint.x, nearestPoint.y).l;
                        if (pointgrad > getGradTreshold()/10)
                        {
                            gradLimit -= pointgrad;
                        }
                        procList.put(pointgrad, nearestPoint);

                       
                    }
                    if (gradLimit < 0)
                    {
                        break;
                    }
                }
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="get/set methods">
    public double getGradTreshold()
    {
        return gradTreshold;
    }

    public double getEqualityRange()
    {
        return equalityRange;
    }

    public void setGradTreshold(double gradTreshold)
    {
        this.gradTreshold = gradTreshold;
    }

    public void setEqualityRange(double equalityRange)
    {
        this.equalityRange = equalityRange;
    }

    /**
     * Возвращает коллекцию выделенных регионов в процессе сегментации.
     * @return регионы, выделенные в процесе сегментации
     */
    public Collection<Region> getRegions()
    {
        return regionManager.getRegions();
    }
//</editor-fold>
}
