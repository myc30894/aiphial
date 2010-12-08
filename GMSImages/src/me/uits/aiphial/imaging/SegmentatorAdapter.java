/*
 *
 * This file is part of Aiphial.
 *
 * Copyright (c) 2010 Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.uits.aiphial.imaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.nickl.meanShift.direct.Point;

import ru.nickl.meanShift.direct.segmentator.Segmentator;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.Clusterer;
import me.uits.aiphial.general.basic.Utls;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;

/**
 * Adapter class to use {@link ru.nickl.meanShift.direct.segmentator.Segmentator}
 * as {@link me.uits.aiphial.general.basic.Clusterer}
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class SegmentatorAdapter implements Clusterer<LuvPoint> {
    
    Segmentator segmentator;

    public SegmentatorAdapter()
    {
    }

    public SegmentatorAdapter(Segmentator segmentator)
    {
        this.segmentator = segmentator;
    }

    

    public void doClustering()
    {
        segmentator.process();
    }

    public List<Region> getClusters()
    {
        Collection<ru.nickl.meanShift.direct.segmentator.Region> regions = segmentator.getRegions();

        List<Region> result = new ArrayList<Region>(regions.size());

        for (ru.nickl.meanShift.direct.segmentator.Region region : regions)
        {
            final Collection<Point> points = region.getPoints();

            ArrayList<LuvPoint> clusterPoints = new ArrayList<LuvPoint>(points.size());
            
            for (Point point : points)
            {
                clusterPoints.add(new LuvPoint(point.x, point.y, LUV.tomodernLUV(point.c)));
            }

            result.add(new Region(clusterPoints));
        }

        return result;
        
    }

    public void setDataStore(DataStore<? extends LuvPoint> dataStore)
    {
        //setting datastore is not implemented and do nothing, because segmentator has its own data
        
        //System.out.println("setting datastore is not implemented and do nothing, because segmentator has its own data");
    }

    /**
     * sets internal segmentator
     * @param segmentator
     */
    public void setSegmentator(Segmentator segmentator)
    {
        this.segmentator = segmentator;
    }

    /**
     * returns internal segmentator
     * @return
     */
    public Segmentator getSegmentator()
    {
        return segmentator;
    }


}
