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
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.imaging.boundary.BoundaryOrderer;

/**
 * Cluster of luvpoints which assumes that points are adjacent
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class Region extends Cluster<LuvPoint>{
   
    /**
     * creates an empty region
     * @param cluster
     */
    public Region()
    {
    }

    /**
     * creates region from given cluster
     * @param cluster
     */
    public Region(Cluster<LuvPoint> cluster) {
        super(cluster.getBasinOfAttraction(), cluster);
    }

    /**
     * creates region from given cluster with given center
     * @param avragePoint
     * @param points
     */
    public Region(NDimPoint avragePoint, Collection<LuvPoint> points) {
        super(avragePoint, points);
    }
    
    /**
     * creates region from given points
     * @param avragePoint
     * @param points
     */
    public Region(Collection<LuvPoint> points) {
        super(points);
    }


    /**
     * returns counterclockwise-ordered contour for this region
     * @return
     */
    public me.uits.aiphial.imaging.boundary.Contour getContour()
    {
        return BoundaryOrderer.orderedBoundary(this);
    }

}
