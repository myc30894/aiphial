/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import java.util.Collection;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;

/**
 * Обработчик изображения, выполняющий сегментацию методом MeanShift на базе {@link RegionGrowingAndAbsorbtionPU}
 * Кроме применени MeanShift фильтра, выделяет регионы,
 * которые могут быть получены путем вызова метода {@link #getRegions()}
 * после выполнения обработки изображения.
 * @author nickl
 */
public class SimpleSegmentator extends AbstractMeanShiftSegmentator
{

    private RegionGrowingAndAbsorbtionPU regionManager = new RegionGrowingAndAbsorbtionPU();

    public SimpleSegmentator()
    {
    }

    /**
     *
     * @param filter используемый для сегментации
     */
    public SimpleSegmentator(MeanShiftFilter filter)
    {
        super(filter);
    }

    /**
     *
     * @param filter используемый для сегментации
     * @param minRegionSize минимальный размер региона.
     * Если ноль то регионы сокращаться не будут
     */
    public SimpleSegmentator(MeanShiftFilter filter, int minRegionSize)
    {
        super(filter);
        setMinRegionSize(minRegionSize);
    }

    /**
     * Обрабатывает изображение и разбивает его на отдельные области
     */
    @Override
    public void process()
    {
        super.process();
        

        regionManager.formRegions();
    }

    @Override
    protected void rebuildSources()
    {
        regionManager.setData(super.getResultLUVArray());
    }   
    

    // <editor-fold defaultstate="collapsed" desc="get/set methods">
    /**
     * Возвращает коллекцию выделенных регионов в процессе сегментации.
     * @return регионы, выделенные в процесе сегментации
     */
    public Collection<Region> getRegions()
    {
        return regionManager.getRegions();
    }
    

    /**
     * Возвращает установленный критерий еквивалентности точек
     * @return
     */
    public double getEqualityRange()
    {
        return regionManager.getEqualityRange();
    }

    /**
     * Устанавливает критерий еквивалентности точек,
     * чем болше значение тем критерий менее строг
     * @param C критерий еквивалентности точек
     */
    public void setEqualityRange(double C)
    {
        regionManager.setEqualityRange(C);
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
    //</editor-fold>
}



   