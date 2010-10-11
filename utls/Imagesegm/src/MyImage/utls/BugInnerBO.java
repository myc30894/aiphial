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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.imaging.LuvPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
class BugInnerBO
{

    ClustersMap clustersMap;
    Cluster cluster;

    public BugInnerBO(ClustersMap clustersMap)
    {
        this.clustersMap = clustersMap;
    }

    public Collection<Collection<LuvPoint>> getOrderedBoundaryes(Cluster<LuvPoint> cluster)
    {
        if (this.cluster != null)
        {
            throw new IllegalStateException("cant process two clusters  simultaneously");
        }
        this.cluster = cluster;

        Collection<LuvPoint> boundary = clustersMap.get4Boundary(cluster);

        Set<LuvPoint> unprocesed = new HashSet<LuvPoint>(boundary);


        Collection<Collection<LuvPoint>> countuors = new ArrayList<Collection<LuvPoint>>();

        while (!unprocesed.isEmpty())
        {
            List<LuvPoint> resultCountour = new ArrayList(boundary.size());
            countuors.add(resultCountour);
            LuvPoint onboundary = unprocesed.iterator().next();

            LuvPoint firstPoint = onboundary;

            resultCountour.add(firstPoint);
            unprocesed.remove(firstPoint);

            LuvPoint prevprev = null;
            LuvPoint prev = firstPoint;
            while (true)
            {
                LuvPoint next = getNextPoint(prev, prevprev);

                if (next == null)
                {
                    break;
                }

                resultCountour.add(next);
                unprocesed.remove(next);

                if (next == firstPoint)
                {
                    break;
                }

                ClusterPanelDebug.clusterPanel.drawCoutours(countuors, 5);

                prevprev = prev;
                prev = next;
            }
        }

       
        this.cluster = null;
        return countuors;
    }

    public Collection<LuvPoint> getOrderedBoundary(Cluster<LuvPoint> cluster)
    {
        Collection<Collection<LuvPoint>> orderedBoundaryes = getOrderedBoundaryes(cluster);
        Iterator<Collection<LuvPoint>> iterator = orderedBoundaryes.iterator();

        Collection<LuvPoint> lengthest = iterator.next();

        while (iterator.hasNext())
        {
            Collection<LuvPoint> collection = iterator.next();
            if (collection.size()>lengthest.size())
            {
                lengthest = collection;
            }
        }

        return lengthest;

    }


    private LuvPoint getNextPoint(LuvPoint cur, LuvPoint prev)
    {
        LuvPoint result = null;
      
            result = getStepFromQeury(clustersMap.getPointsMap().get8CouterClockwise(cur), cur, prev);
 
        return result;

    }

    private LuvPoint getStepFromQeury(CircleList<LuvPoint> cc4, LuvPoint cur, LuvPoint pstep)
    {

        LuvPoint result = null;
        Iterator<LuvPoint> iterator;

        if (pstep == null)
        {
            iterator = cc4.iterator();

            Iterable<LuvPoint> nearest4 = clustersMap.getPointsMap().get4Nearest(cur);
            LuvPoint in4outer = null;
            for (LuvPoint luvPoint : nearest4)
            {
                if (clustersMap.getAt(luvPoint) != cluster)
                {
                    in4outer = luvPoint;
                    break;
                }
            }
            if (in4outer != null)
            {
                iterator = cc4.iterator(in4outer);
            }

        } else
        {
            iterator = cc4.iterator(pstep);
        }

        LuvPoint first = iterator.next();
        LuvPoint prev = first;

        while (iterator.hasNext())
        {
            LuvPoint curr = iterator.next();
            if (clustersMap.getAt(prev) != cluster && clustersMap.getAt(curr) == cluster)
            {
                result = curr;
                break;
            }
            prev = curr;
        }
        if (result == null && clustersMap.getAt(first) == cluster && clustersMap.getAt(prev) != cluster)
        {
            result = first;
        }
        return result;
    }
    
}
