/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.filter.NearestFinder;
import ru.nickl.meanShift.direct.filter.SpartialNearestFinder;

/**
 * Расширяет алгоритм {@link RegionGrowingAndAbsorbtionPU} поглощением "невыделненных" регионов.
 * Проблема в том, что при использовании нестандартных {@link NearestFinder критериев поиска ближних элементов}
 * в результате получаются точки, которые не включаются ни в один регион. Стандартный алгоритм
 * {@link RegionGrowingAndAbsorbtionPU} ингорирует эти точки.
 * А данное расширение пытается включить их в отдельные регионы и затем поглотить
 * те из них размер которых меньше установленного minRegionSize
 *
 * @author nickl
 */
public class RGMWithNonRegionAbsorbtion extends RegionGrowingAndAbsorbtionPU
{

    public RGMWithNonRegionAbsorbtion()
    {
        super();
    }

    public RGMWithNonRegionAbsorbtion(int minRegionSize)
    {
        super(minRegionSize);
    }

    @Override
    public void formRegions()
    {
        super.formRegions();

        IndexedRegionPU indexedRegionPU = new IndexedRegionPU();

        indexedRegionPU.setData(getData());

        indexedRegionPU.setNearestFinder(new SpartialNearestFinder(getData(), (short) 1, new SpartialNearestFinder.Criteria()
        {

            public boolean isNear(Point a, Point b)
            {
                return getPointRegion(a)==null && getPointRegion(b) == null;
            }
        }));

        indexedRegionPU.formRegions();


        for (Region region : indexedRegionPU.getRegions())
        {
            if (region.getPoints().size() <= getMinRegionSize())
            {
                absorbRegion(region);
            }
        }



    }
}
