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

package me.uits.aiphial.imaging.boundary;

import java.util.Collection;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.imaging.Region;

/**
 * Facade for boundary ordering
 * @author nickl
 */
public class BoundaryOrderer {

    private BoundaryOrderer(){}

    /**
     * extracts an ordered boundary from given region
     * @param region
     * @return counterclockwise-ordered contour for this region
     */
    public static Contour orderedBoundary(Region region){
      return  new BugInnerBO(ClustersMap.fromOneCluster(region)).getOrderedBoundary(region);
    }


    /**
     * returns cluster boundary consists of points that have
     * non-this-cluster points in 4-neighborhood
     * @param cluster
     * @return
     */
    public static Collection<LuvPoint> boundary(Region region){
      return  ClustersMap.fromOneCluster(region).get4Boundary(region);
    }

}
