/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import java.util.Collection;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.PointUtils;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;
import ru.nickl.meanShift.direct.filter.SobelFilter;
import ru.nickl.meanShift.direct.filter.SpartialNearestFinder;

/**
 *
 * @author nickl
 */
public class SobelMeanShiftSegmentator extends AbstractMeanShiftSegmentator
{

    private RGMWithNonRegionAbsorbtion regionManager = new RGMWithNonRegionAbsorbtion();
    private LuvData gradientData;

    public SobelMeanShiftSegmentator()
    {
    }
    private double equalityRange = 1;
    private double gradTreshold = 5000;

    /**
     *
     * @param filter используемый для сегментации
     * @param minRegionSize минимальный размер региона.
     * Если ноль то регионы сокращаться не будут
     */
    public SobelMeanShiftSegmentator(MeanShiftFilter filter, int minRegionSize)
    {
        this(filter);
        setMinRegionSize(minRegionSize);
    }

    public SobelMeanShiftSegmentator(MeanShiftFilter filter)
    {
        super(filter);

        SpartialNearestFinder spartialNearestFinder = new SpartialNearestFinder();
        spartialNearestFinder.setSquareRange((short) 2);

        spartialNearestFinder.setAdditionalCriteria(new SpartialNearestFinder.Criteria()
        {

            public boolean isNear(Point a, Point b)
            {
                //return mathquality(a, b) && gradientData[a.y][a.x].l < gradTreshold;

                return (PointUtils.Dim(a, b)*equalityRange + gradientData.getLUV(a.x, a.y).l)/(equalityRange+1) < gradTreshold;


            }

            private boolean mathquality(Point a, Point b)
            {
                LUV d = a.c.minus(b.c);
                if (Math.abs(d.l) < equalityRange && Math.abs(d.u) < equalityRange && Math.abs(d.v) < equalityRange)
                {
                    return true;
                } else
                {
                    return false;
                }

            }
        });

        regionManager.setNearestFinder(spartialNearestFinder);
    }

    @Override
    public void process()
    {
        super.process();
        regionManager.formRegions();
    }

    @Override
    protected void rebuildSources()
    {
        gradientData = sobelFilter.filter(super.getLUVArray());
        super.rebuildSources();
        regionManager.setData(super.getResultLUVArray());
    }
    private SobelFilter sobelFilter = new SobelFilter();

    // <editor-fold defaultstate="collapsed" desc="get/set methods">
    public double getGradTreshold()
    {
        return gradTreshold;
    }

    /**
     * Возвращает установленный критерий еквивалентности точек
     * @return
     */
    public double getEqualityRange()
    {
        return equalityRange;
    }

    public void setGradTreshold(double gradTreshold)
    {
        this.gradTreshold = gradTreshold;
    }

    /**
     * Устанавливает критерий еквивалентности точек,
     * чем болше значение тем критерий менее строг
     * @param C критерий еквивалентности точек
     */
    public void setEqualityRange(double equalityRange)
    {
        this.equalityRange = equalityRange;
    }

    /**
     *
     * @return минимальный размер региона.
     * Если ноль то регионы сокращаться не будут
     */
    public int getMinRegionSize()
    {
        return regionManager.getMinRegionSize();
    }

    /**
     * Устанавливает минимальный размер региона.
     * Если ноль то регионы сокращаться не будут
     * @param minRegionSize минимальный размер региона.
     */
    public void setMinRegionSize(int minRegionSize)
    {
        regionManager.setMinRegionSize(minRegionSize);
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
