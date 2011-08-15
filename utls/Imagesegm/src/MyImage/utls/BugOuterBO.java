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
import java.util.Iterator;
import java.util.List;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.imaging.LuvPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class BugOuterBO
{

    ClustersMap clustersMap;
    Cluster cluster;

    public BugOuterBO(ClustersMap clustersMap)
    {
        this.clustersMap = clustersMap;
    }

    public Collection<LuvPoint> getOrderedBoundary(Cluster<LuvPoint> cluster)
    {
        if (this.cluster != null)
        {
            throw new IllegalStateException("cant process two clusters  simultaneously");
        }
        this.cluster = cluster;

        Collection<LuvPoint> boundary = clustersMap.get4Boundary(cluster);
        LuvPoint onboundary = boundary.iterator().next();

        LuvPoint firstPoint = null;

        for (LuvPoint luvPoint : clustersMap.get4Nearest(onboundary))
        {
            if (clustersMap.getAt(luvPoint) != cluster)
            {
                firstPoint = luvPoint;
                break;
            }
        }


        List<LuvPoint> resultCountour = new ArrayList(boundary.size());

        resultCountour.add(firstPoint);
        LuvPoint prevprev = null;
        LuvPoint prev = firstPoint;
        while (true)
        {
            LuvPoint next = getNextPoint(prev, prevprev);

            resultCountour.add(next);

            if (next == firstPoint)
            {
                break;
            }

            ClusterPanelDebug.clusterPanel.drawCoutour(resultCountour, 100);

            prevprev = prev;
            prev = next;
        }



        this.cluster = null;
        return resultCountour;
    }


    private LuvPoint getNextPoint(LuvPoint cur, LuvPoint prev)
    {
        LuvPoint result = null;

//        result = getStepFromQeury(get4CouterClockwise(cur), prev);
//        if (result == null)
//        {
            result = getStepFromQeury(clustersMap.getPointsMap().get8CouterClockwise(cur), prev);
////        }

        return result;

    }

    private LuvPoint getStepFromQeury(CircleList<LuvPoint> cc4, LuvPoint pstep)
    {

        LuvPoint result = null;
        Iterator<LuvPoint> iterator;

        if (pstep == null)
        {
            iterator = cc4.iterator();
        } else
        {
            iterator = cc4.iterator(pstep);            
        }

        LuvPoint first = iterator.next();
        LuvPoint prev = first;

        while (iterator.hasNext())
        {
            LuvPoint curr = iterator.next();
            if (clustersMap.getAt(prev) == cluster && clustersMap.getAt(curr) != cluster)
            {
                result = curr;
                break;
            }
            prev = curr;
        }
        if (result == null && clustersMap.getAt(prev) == cluster)
        {
            result = first;
        }
        return result;
    }

    private CircleList<LuvPoint> get4CouterClockwise(LuvPoint cur)
    {
        int cx = cur.getX();
        int cy = cur.getY();

        //Collection<LuvPoint> r = new ArrayList<LuvPoint>(4);

        CircleList<LuvPoint> r = new CircleList<LuvPoint>();

        r.add(clustersMap.getPointAt(cx, cy - 1));
        r.add(clustersMap.getPointAt(cx - 1, cy));
        r.add(clustersMap.getPointAt(cx, cy + 1));
        r.add(clustersMap.getPointAt(cx + 1, cy));

        return r;
    }

    private CircleList<LuvPoint> get8CouterClockwise(LuvPoint cur)
    {
        int cx = cur.getX();
        int cy = cur.getY();

        //Collection<LuvPoint> r = new ArrayList<LuvPoint>(8);

        CircleList<LuvPoint> r = new CircleList<LuvPoint>();

        r.add(clustersMap.getPointAt(cx, cy - 1));
        r.add(clustersMap.getPointAt(cx - 1, cy - 1));
        r.add(clustersMap.getPointAt(cx - 1, cy));
        r.add(clustersMap.getPointAt(cx - 1, cy + 1));
        r.add(clustersMap.getPointAt(cx, cy + 1));
        r.add(clustersMap.getPointAt(cx + 1, cy + 1));
        r.add(clustersMap.getPointAt(cx + 1, cy));
        r.add(clustersMap.getPointAt(cx + 1, cy - 1));

        return r;
    }

    
}
