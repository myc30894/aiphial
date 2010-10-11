/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.Point;

/**
 * Алогоритм разрастания регионов, расширяющий базовый алгоритм возможностью
 * поглощения региона меньшего определенного размера ближайшим соседом.
 * @author nickl
 */
public class RegionGrowingAndAbsorbtionPU extends IndexedRegionPU
{

    private int minRegionSize = 0;

    public RegionGrowingAndAbsorbtionPU()
    {
    }

    /**
     *
     * @param minRegionSize минимальный размер региона.
     * Если ноль то регионы сокращаться не будут
     */
    public RegionGrowingAndAbsorbtionPU(int minRegionSize)
    {
        this.minRegionSize = minRegionSize;
    }

    /**
     *
     * @return минимальный размер региона.
     * Если ноль то регионы сокращаться не будут
     */
    public int getMinRegionSize()
    {
        return minRegionSize;
    }

    /**
     * Устанавливает минимальный размер региона.
     * Если ноль то регионы сокращаться не будут
     * @param minRegionSize минимальный размер региона.

     */
    public void setMinRegionSize(int minRegionSize)
    {
        this.minRegionSize = minRegionSize;
    }

    @Override
    public void formRegions()
    {
        super.formRegions();

        if (minRegionSize > 0)
        {
            removeSmallregions();
        }

    }

    protected  void removeSmallregions()
    {

        List<IndexedRegion> newRegionLIst = new ArrayList<IndexedRegion>();

        for (IndexedRegion region : regions)
        {
            if (region.getPoints().size() < minRegionSize)
            {
                absorbRegion(region);
            } else
            {
                newRegionLIst.add(region);
            }
        }

        regions = newRegionLIst;

        rebuildIndexMatrix();
    }

    

    protected  void absorbRegion(Region region)
    {
        IndexedRegion nearestRegion = getNearestRegion(region);

        if (nearestRegion!=null)
        {
           
            for (Point p : region.getPoints())
            {                
                nearestRegion.add(p);
            //region.remove(p);
            }
        }

    }

    private IndexedRegion getNearestRegion(Region region)
    {
        LuvData data1 = getData();
        int height = data1.getHeight();
        int width = data1.getWidth();
        HashMap<IndexedRegion, Integer> neibourdMap = new HashMap<IndexedRegion, Integer>();
        for (Point p : region.getCountour())
        {
            for (short y0 = (short) (p.y - 1); y0 <= p.y + 1; y0++)
            {
                if (y0 >= 0 && y0 < height)
                {
                    for (short x0 = (short) (p.x - 1); x0 <= p.x + 1; x0++)
                    {
                        if (x0 >= 0 && x0 < width)
                        {
                            final IndexedRegion pointRegion = (IndexedRegion) this.getPointRegion(x0, y0);
                            if (pointRegion != region)
                            {
                                Integer count = neibourdMap.get(pointRegion);
                                if (count == null)
                                {
                                    count = 0;
                                }
                                count++;
                                neibourdMap.put(pointRegion, count);
                            }
                        }
                    }
                }
            }
        }
        int max = 0;
        IndexedRegion nearestRegion = null;
        for (Entry<IndexedRegion, Integer> entry : neibourdMap.entrySet())
        {
            final Integer value = entry.getValue();
            if (value > max)
            {
                max = value;
                nearestRegion = entry.getKey();
            }
        }
        return nearestRegion;
    }
}
