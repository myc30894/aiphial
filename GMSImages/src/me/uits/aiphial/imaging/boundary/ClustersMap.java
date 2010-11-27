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

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.Utls;
import me.uits.aiphial.imaging.ImgUtls;

import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.imaging.Region;
import static java.lang.Math.*;

/**
 * Class to determinate which point is assigned to which cluster and visa-versa
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
class ClustersMap
{
    //возможно его стоит сделать наследником PointsMap

    private Region[][] array;
    private PointsMap pointsMap;
    protected int sx;
    protected int sy;
    int width;
    int height;
    Map<Region, Collection<LuvPoint>> bounary8map;
    Map<Region, Collection<LuvPoint>> bounary4map;

    /**
     * @param x lowest x-coordinate of image plane
     * @param y lowest y-coordinate of image plane
     * @param width width of image plane
     * @param height height of image plane
     * @param add - size of additional border buffer
     */
    public ClustersMap(int x, int y, int width, int height, int add)
    {
        this.sx = x - add;
        this.sy = y - add;
        this.width = width + add;
        this.height = height + add;
        array = new Region[this.width - sx][this.height - sy];
        //points = new LuvPoint[width][height];
        pointsMap = new PointsMap(x, y, width, height, add);
    }

    /**
     * @param width width of image plane
     * @param height height of image plane
     */
    public ClustersMap(int width, int height)
    {
        this(0, 0, width, height, 1);
    }

    public static ClustersMap fromOneCluster(Region cluster)
    {

        Rectangle r = ImgUtls.getBoundingRect(cluster);
        ClustersMap clustersMap = new ClustersMap(r.x + r.width + 1, r.y + r.height + 1);
        clustersMap.buildMap(Arrays.asList(cluster));
        return clustersMap;
    }

    /**
     * adds clusters to the map
     * @param clusters
     */
    public void buildMap(Iterable<Region> clusters)
    {
        int count = 0;


        for (Region cluster : clusters)
        {
            count++;
            for (LuvPoint point : cluster)
            {
                array[point.getX() - sx][point.getY() - sy] = cluster;
                //points[point.getX()][point.getY()] = point;
                pointsMap.putAt(point.getX(), point.getY(), point);
            }


        }
        buildBoundaryes(clusters, count);

    }

    private void buildBoundaryes(Iterable<Region> clusters, int count)
    {
        bounary8map = new HashMap<Region, Collection<LuvPoint>>(count);
        bounary4map = new HashMap<Region, Collection<LuvPoint>>(count);
        for (Region cluster : clusters)
        {

            bounary8map.put(cluster, buildcluster8boundary(cluster));
            bounary4map.put(cluster, buildcluster4boundary(cluster));
        }
    }

    /**
     * returns the cluster which given point is assigned to
     * @param point
     * @return
     */
    public Cluster getAt(LuvPoint point)
    {
        return getAt(point.getX(), point.getY());
    }

    /**
     * returns the cluster which given point is assigned to
     * @param point
     * @return
     */
    public Cluster getAt(int x, int y)
    {
        return array[x - sx][y - sy];
    }

    /**
     * returns point by given coordinates
     * @param x
     * @param y
     * @return
     */
    public LuvPoint getPointAt(int x, int y)
    {
        return pointsMap.getAt(x, y);
    }

    /**
     * returns cluster boundary consists of points that have
     * non-this-cluster points in 8-neighborhood
     * @param cluster
     * @return
     */
    public Collection<LuvPoint> get8Boundary(Cluster cluster)
    {
        return Collections.unmodifiableCollection(bounary8map.get(cluster));
    }

    /**
     * returns cluster boundary consists of points that have
     * non-this-cluster points in 4-neighborhood
     * @param cluster
     * @return
     */
    public Collection<LuvPoint> get4Boundary(Cluster cluster)
    {
        return Collections.unmodifiableCollection(bounary4map.get(cluster));
    }

    private Collection<LuvPoint> buildcluster8boundary(Region cluster)
    {
        Collection<LuvPoint> boundary = new ArrayList<LuvPoint>();
        for (LuvPoint point : cluster)
        {
            if (isOnBoundary(point, cluster))
            {
                boundary.add(point);
            }
        }
        return boundary;
    }

    private Collection<LuvPoint> buildcluster4boundary(Region cluster)
    {
        Collection<LuvPoint> boundary = new ArrayList<LuvPoint>();
        for (LuvPoint point : cluster)
        {

            if (isOn4Boundary(point, cluster))
            {
                boundary.add(point);
            }
        }
        return boundary;
    }

    /**
     *
     * @param point
     * @param cluster
     * @return true if this point have points in 8-neighborhood
     * which are not assigned to given cluster
     * otherwise false
     */
    public boolean isOnBoundary(LuvPoint point, Cluster cluster)
    {

        for (LuvPoint p : pointsMap.get8Nearest(point))
        {
            if (getAt(p.getX(), p.getY()) != cluster)
            {
                return true;
            }
        }

        return false;

    }

    /**
     *
     * @param point
     * @param cluster
     * @return true if this point have points in 4-neighborhood
     * which are not assigned to given cluster
     * otherwise false
     */
    public boolean isOn4Boundary(LuvPoint point, Cluster cluster)
    {
        int i = 0;
        for (LuvPoint p : pointsMap.get4Nearest(point))
        {
            if (getAt(p.getX(), p.getY()) != cluster)
            {
                return true;
            }
            i++;
        }
        if (i < 4)
        {
            return true;
        }

        return false;
    }

    /**
     *
     * @param cluster
     * @param point
     * @return points from 8-neighborhood of given point which are members of given cluster
     */
    public Collection<LuvPoint> get8NearestWithinCluster(Region cluster, LuvPoint point)
    {
        Collection<LuvPoint> result = new ArrayList<LuvPoint>(8);

        for (LuvPoint l : get8Nearest(point))
        {
            if (getAt(l.getX(), l.getY()) == cluster)
            {
                result.add(l);
            }
        }

        return result;
    }

    /**
     * @param point
     * @return points from 8-neighborhood of given point
     */
    public Iterable<LuvPoint> get8Nearest(LuvPoint point)
    {
        return pointsMap.get8Nearest(point);
    }

    /**
     *
     * @param point
     * @return points from 4-neighborhood of given point
     */
    public Iterable<LuvPoint> get4Nearest(LuvPoint point)
    {
        return pointsMap.get4Nearest(point);
    }


    public PointsMap getPointsMap()
    {
        return pointsMap;
    }
}
