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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.general.basic.Cluster;

import static java.lang.Math.*;


/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class BoundaryOrderer2
{

    private Cluster<LuvPoint> cluster;
    private Collection<LuvPoint> unorderedBoundary;

    public BoundaryOrderer2(Cluster<LuvPoint> cluster, Collection<LuvPoint> unorderedBoundary)
    {
        this.cluster = cluster;
        this.unorderedBoundary = unorderedBoundary;
    }


    public Collection<LuvPoint> getOrdered()
    {
        

        Set<LuvPoint> unprocesed = new HashSet<LuvPoint>(unorderedBoundary.size());
        unprocesed.addAll(unorderedBoundary);
        Path2 path = new Path2();
        LuvPoint elem = null;
        while (!unprocesed.isEmpty())
        {
            if (elem == null)
            {
                elem = unprocesed.iterator().next();
                unprocesed.remove(elem);
            }

            Collection<LuvPoint> nearest = searchFor4Nearest(elem, unprocesed);

            if(nearest.size()==1)
            {
                path.add(elem);
                elem = nearest.iterator().next();
                unprocesed.remove(elem);
            }
            else if(nearest.size()>1)
            {
                path.addBranching(elem);
                elem = nearest.iterator().next();
                unprocesed.remove(elem);
            }
            else{

               elem = path.newbranch(unprocesed);

               //TODO removeit
               if(elem==null) break;

               unprocesed.remove(elem);
            }
            //TODO removeit
            ClusterPanelDebug.clusterPanel.drawCoutour(path.asList(), 100);

        }
        return path.asList();
    }

    private LuvPoint calkcenter(Iterable<LuvPoint> cluster)
    {
        int x = 0;
        int y = 0;
        int n = 0;
        for (LuvPoint luvPoint : cluster)
        {
            x += luvPoint.getX();
            y += luvPoint.getY();
            n++;
        }

        return new LuvPoint(x / n, y / n, null);
    }

    public static Collection<LuvPoint> searchFor8Nearest(LuvPoint point, Collection<LuvPoint> collection)
    {
        Collection<LuvPoint> result = new ArrayList<LuvPoint>(4);

        for (LuvPoint luvPoint : collection)
        {
            if (abs(luvPoint.getX() - point.getX()) <= 1 &&
                    abs(luvPoint.getY() - point.getY()) <= 1)
            {
                result.add(luvPoint);
            }
        }

        return result;
    }

    public static Collection<LuvPoint> searchFor4Nearest(LuvPoint p, Collection<LuvPoint> collection)
    {
        Collection<LuvPoint> result = new ArrayList<LuvPoint>(4);

        for (LuvPoint l : collection)
        {
            final boolean xinbounds = abs(l.getX() - p.getX()) <= 1;
            final boolean yinbounds = abs(l.getY() - p.getY()) <= 1;
            if (xinbounds && p.getY() == l.getY() ||
                    yinbounds && p.getX() == l.getX())
            {
                result.add(l);
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
    }

    // </editor-fold>
}
class Path2
{

    private Set<LuvPoint> pathpoints = new LinkedHashSet<LuvPoint>();
    private LinkedList<LuvPoint> linkedList = new LinkedList<LuvPoint>();
    private Deque<LuvPoint> branchingoponts = new ArrayDeque<LuvPoint>();
    private ListIterator<LuvPoint> lead = linkedList.listIterator();

    public Path2()
    {
    }

    public void add(LuvPoint point)
    {
        if (!pathpoints.contains(point))
        {            
            pathpoints.add(point);
            lead.add(point);
        }
    }
  
    public void addBranching(LuvPoint l)
    {
        pathpoints.add(l);
        lead.add(l);
        branchingoponts.push(l);
    }

    public LuvPoint newbranch(Collection<LuvPoint> unprocessed)
    {
        LuvPoint peek = branchingoponts.peek();
        if(peek==null) return null;

        Collection<LuvPoint> nearest = BoundaryOrderer2.searchFor4Nearest(peek, unprocessed);
        if(nearest.isEmpty())
        {
            branchingoponts.pop();
            return newbranch(unprocessed);
        }
        else
        {
            LuvPoint result = nearest.iterator().next();
            //TODO optimize
            lead = linkedList.listIterator(linkedList.lastIndexOf(peek));
            System.out.println("changed");
            if(nearest.size()==1)
            {
                branchingoponts.pop();
            }
            return result;
        }

    }

    public List<LuvPoint> asList()
    {
        return new ArrayList<LuvPoint>(linkedList);
    }
}
