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

package MyImage.utls;

import MyImage.ClusterPanelDebug;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.imaging.ImgUtls;
import me.uits.aiphial.imaging.LuvPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class RegionGrowBO
{

    ClustersMap clustersMap;

    public RegionGrowBO(ClustersMap clustersMap)
    {
        this.clustersMap = clustersMap;
    }

    public Collection<LuvPoint> getOrderedBoundary(Cluster<LuvPoint> cluster)
    {

        LinkedList<LuvPoint> result = new LinkedList<LuvPoint>();

        Set<LuvPoint> unprocessed = new HashSet<LuvPoint>(cluster);

        Set<LuvPoint> processed = new HashSet<LuvPoint>();

        while (!unprocessed.isEmpty())
        {
            LuvPoint first = unprocessed.iterator().next();

            result.add(first);
            unprocessed.remove(first);

            List<LuvPoint> nonBoundary = getNonBoundary(result, cluster);

            while (true)
            {
                if (nonBoundary.isEmpty())
                {
                    nonBoundary = getNonBoundary(result, cluster);
                }
                if (nonBoundary.isEmpty())
                {
                    break;
                }

                LuvPoint anynb = nonBoundary.iterator().next();
                nonBoundary.remove(anynb);

                Collection<LuvPoint> nearestWithinCluster = clustersMap.get8NearestWithinCluster(cluster, anynb);

                nearestWithinCluster = filterExist(processed, nearestWithinCluster);
                //nearestWithinCluster = filternonExist(unprocessed, nearestWithinCluster);
                Collection<LuvPoint> clockwise = get8Clockwise(nearestWithinCluster, anynb);

                unprocessed.removeAll(clockwise);
                unprocessed.remove(anynb);

                int indexOfanynb = result.indexOf(anynb);

                result.addAll(indexOfanynb, clockwise);
                result.remove(indexOfanynb + clockwise.size());
                processed.add(anynb);
                processed.addAll(clockwise);

                ClusterPanelDebug.clusterPanel.drawCoutour(result, 100);


            }



            System.out.println("new growing");

            //TODO remove it
            break;


        }

        return result;

    }

    private List<LuvPoint> getNonBoundary(List<LuvPoint> l, Cluster<LuvPoint> cluster)
    {
        List<LuvPoint> result = new ArrayList<LuvPoint>();

        for (LuvPoint luvPoint : l)
        {
            if (!clustersMap.isOnBoundary(luvPoint, cluster))
            {
                result.add(luvPoint);
            }
        }

        return result;
    }

    private Collection<LuvPoint> filterExist(Collection<LuvPoint> where, Collection<LuvPoint> what)
    {
        Collection<LuvPoint> result = new ArrayList<LuvPoint>(what.size());

        for (LuvPoint luvPoint : what)
        {
            if (!where.contains(luvPoint))
            {
                result.add(luvPoint);
            }
        }

        return result;
    }

    private Collection<LuvPoint> filternonExist(Collection<LuvPoint> where, Collection<LuvPoint> what)
    {
        Collection<LuvPoint> result = new ArrayList<LuvPoint>(what.size());

        for (LuvPoint luvPoint : what)
        {
            if (where.contains(luvPoint))
            {
                result.add(luvPoint);
            }
        }

        return result;
    }

    private Collection<LuvPoint> get8Clockwise(Collection<LuvPoint> points, LuvPoint c)
    {
        int cx = c.getX();
        int cy = c.getY();

        PointsMap pm = new PointsMap(cx - 1, cy - 1, 3, 3).buildMap(points);

        List<LuvPoint> result = new ArrayList<LuvPoint>(points.size());

        addifexist(result, pm, cx - 1, cy - 1);
        addifexist(result, pm, cx + 0, cy - 1);
        addifexist(result, pm, cx + 1, cy - 1);

        addifexist(result, pm, cx + 1, cy + 0);
        addifexist(result, pm, cx + 1, cy + 1);
        addifexist(result, pm, cx + 0, cy + 1);
        addifexist(result, pm, cx + 0, cy - 1);
        addifexist(result, pm, cx - 1, cy + 0);

        //TODO update it;

        return result;


    }

    private void addifexist(List<LuvPoint> result, PointsMap pm, int x, int y)
    {
        LuvPoint at = pm.getAt(x, y);
        if (at != null)
        {
            result.add(at);
        }
    }
}
