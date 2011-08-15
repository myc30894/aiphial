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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.general.basic.Cluster;

import static java.lang.Math.*;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class BoundaryOrderer1 {

    private Cluster<LuvPoint> cluster;
    private Collection<LuvPoint> unorderedBoundary;

    public BoundaryOrderer1(Cluster<LuvPoint> cluster, Collection<LuvPoint> unorderedBoundary)
    {
        this.cluster = cluster;
        this.unorderedBoundary = unorderedBoundary;
    }

    public  Collection<LuvPoint> getOrdered()
    {

        Collection<LuvPoint> result = new ArrayList<LuvPoint>(unorderedBoundary.size());
        final Map<LuvPoint, Integer> distanses = new HashMap<LuvPoint, Integer>(unorderedBoundary.size());
        LuvPoint centerPoint = calkcenter(cluster);
        for (LuvPoint luvPoint : unorderedBoundary)
        {
            int cx = centerPoint.getX();
            int cy = centerPoint.getY();
            int nx = luvPoint.getX();
            int ny = luvPoint.getY();
            distanses.put(luvPoint, (cx - nx) * (cx - nx) + (cy - ny) * (cy - ny));
        }
        Set<LuvPoint> unprocesed = new HashSet<LuvPoint>(unorderedBoundary.size());
        unprocesed.addAll(unorderedBoundary);
        LuvPoint elem = null;
        while (!unprocesed.isEmpty())
        {
            if (elem == null)
            {
                elem = unprocesed.iterator().next();
            }
            result.add(elem);
            unprocesed.remove(elem);
            Collection<LuvPoint> nearest = searchForNearest(elem, unprocesed);
            if (!nearest.isEmpty())
            {
                Iterator<LuvPoint> nearests = nearest.iterator();
                elem = nearests.next();
                while (nearests.hasNext())
                {
                    LuvPoint c = nearests.next();
                    if (distanses.get(c) > distanses.get(elem))
                    {
                        elem = c;
                    }
                }
            } else
            {
                elem = null;
            }
        }
        return result;
    }


     private  LuvPoint calkcenter(Iterable<LuvPoint> cluster)
     {
         int x =0;
         int y =0;
         int n = 0;
         for (LuvPoint luvPoint : cluster)
         {
             x+=luvPoint.getX();
             y+=luvPoint.getY();
             n++;
         }

         return new LuvPoint(x/n, y/n, null);
     }

      private  Collection<LuvPoint> searchForNearest(LuvPoint point, Collection<LuvPoint> collection)
    {
        Collection<LuvPoint> result = new ArrayList<LuvPoint>(4);

        for (LuvPoint luvPoint : collection)
        {
            if(abs(luvPoint.getX()-point.getX())<=1 &&
                abs(luvPoint.getY()-point.getY())<=1
                    )
            {
                result.add(luvPoint);
            }
        }

        return result;
    }



// <editor-fold defaultstate="collapsed" desc="gettrsandsetters">
    /**
     * @return the cluster
     */
    public Cluster<LuvPoint> getCluster()
    {
        return cluster;
    }

    /**
     * @param cluster the cluster to set
     */
    public void setCluster(Cluster<LuvPoint> cluster)
    {
        this.cluster = cluster;
    }

    /**
     * @return the unorderedBoundary
     */
    public Collection<LuvPoint> getUnorderedBoundary()
    {
        return unorderedBoundary;
    }

    /**
     * @param unorderedBoundary the unorderedBoundary to set
     */
    public void setUnorderedBoundary(Collection<LuvPoint> unorderedBoundary)
    {
        this.unorderedBoundary = unorderedBoundary;
    }// </editor-fold>

}
