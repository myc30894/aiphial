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
import java.util.NoSuchElementException;
import java.util.Set;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.general.basic.Cluster;

import static java.lang.Math.*;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class BoundaryOrderer
{

    private Cluster<LuvPoint> cluster;
    private Collection<LuvPoint> unorderedBoundary;

    public BoundaryOrderer(Cluster<LuvPoint> cluster, Collection<LuvPoint> unorderedBoundary)
    {
        this.cluster = cluster;
        this.unorderedBoundary = unorderedBoundary;
    }

    public Collection<LuvPoint> getOrdered()
    {


        System.out.println("startedted");

        Set<LuvPoint> unprocesed = new HashSet<LuvPoint>(unorderedBoundary.size());
        unprocesed.addAll(unorderedBoundary);
        Path path = new Path();
        LuvPoint elem = null;
        while (!unprocesed.isEmpty())
        {
            if (elem == null)
            {
                elem = unprocesed.iterator().next();
                unprocesed.remove(elem);
            }

            Collection<LuvPoint> nearest = searchFor4Nearest(elem, unprocesed);

            if (nearest.size() == 1)
            {
                path.add(elem);
                elem = nearest.iterator().next();
                unprocesed.remove(elem);
            } else if (nearest.size() > 1)
            {
                path.addBranching(elem);
                elem = nearest.iterator().next();
                unprocesed.remove(elem);
            } else
            {

                elem = path.getReservedBranch(unprocesed);

                //TODO removeit
                if (elem == null)
                {
                    break;
                }

                unprocesed.remove(elem);
            }
            //TODO removeit
            //ClusterPanelDebug.clusterPanel.drawCoutour(path.curList());

        }

        System.out.println("painted");
        //ClusterPanelDebug.clusterPanel.drawCoutour(path.asList());
        System.out.println();
        return path.asList();
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

class Path
{

    private Set<LuvPoint> pathpoints = new LinkedHashSet<LuvPoint>();
    //private LinkedList<LuvPoint> linkedList = new LinkedList<LuvPoint>();
    private Deque<Node> branchingoponts = new ArrayDeque<Node>();
    //private ListIterator<LuvPoint> lead; //= linkedList.listIterator();
    private List<LuvPoint> resultList;
    private LuvPoint firstppoint;
    private int i = 0;

    public Path()
    {
    }

    public void add(LuvPoint l)
    {
        if (!pathpoints.contains(l))
        {
            pathpoints.add(l);
            try
            {
                branchingoponts.getFirst().lead.add(l);
            } catch (NoSuchElementException e)
            {
                firstppoint = l;
            }
        }
    }

    public void addBranching(LuvPoint l)
    {
        pathpoints.add(l);
        try
        {
            branchingoponts.getFirst().lead.add(l);
        } catch (NoSuchElementException e)
        {
            firstppoint = l;
        }
        final Node node = new Node(i++, l);
        branchingoponts.push(node);
        node.lead = node.b1.listIterator();
        System.out.println("Node " + node.number + " created");

    }

    public LuvPoint getReservedBranch(Collection<LuvPoint> unprocessed)
    {
        Node node = branchingoponts.peek();
        if (node == null)
        {
            return null;
        }

        Collection<LuvPoint> nearest = BoundaryOrderer.searchFor4Nearest(node.branchPoint, unprocessed);
        if (nearest.size() > 2)
        {
            throw new RuntimeException("more than two unprocessed nearest points");
        }
        if (!node.b1.isEmpty() && !node.b2.isEmpty())
        {
            cunsumeNode();

            System.out.println("\t:Node " + node.number + " merged: in normal way");

            return getReservedBranch(unprocessed);

        } else if (nearest.isEmpty())
        {
            //System.err.println("branch expected but wasn't found!");            
            cunsumeNode();
            System.out.println("\t:Node " + node.number + " merged: branch expected but wasn't found!");
            return getReservedBranch(unprocessed);
        } else
        {
            LuvPoint result = nearest.iterator().next();

            if (node.b1.isEmpty())
            {
                node.lead = node.b1.listIterator();
                System.out.println("in " + node.number + " changed to 1");
            } else if (node.b2.isEmpty())
            {
                node.lead = node.b2.listIterator();
                System.out.println("in " + node.number + " changed to 2");
            } else
            {
                throw new RuntimeException("impassable");
            }


            return result;
        }

    }

    private List<LuvPoint> convertFilledNodeToList(Node node)
    {
        List<LuvPoint> l1 = node.b1;
        List<LuvPoint> l2 = node.b2;
        tryRemoveDubles(l2, l1);
        tryRemoveDubles(l1, l2);
        List<LuvPoint> greater = l1;
        List<LuvPoint> lesser = l2;
        if (greater.size() < lesser.size())
        {
            greater = l2;
            lesser = l1;
        }
        //TODO optmize
        List<LuvPoint> branchList = new ArrayList<LuvPoint>(lesser.size() + greater.size());
        branchList.addAll(lesser);
        branchList.addAll(greater);
        return branchList;
    }

    private void cunsumeNode()
    {
        Node node = branchingoponts.pop();
        List<LuvPoint> converted = convertFilledNodeToList(node);

        if (!branchingoponts.isEmpty())
        {
            for (LuvPoint luvPoint : converted)
            {
                branchingoponts.peek().lead.add(luvPoint);
            }
        } else
        {
            resultList = new ArrayList<LuvPoint>(converted);
        }

        System.out.println("Node " + node.number + " merged");
    }

    private void tryRemoveDubles(List<LuvPoint> what, List<LuvPoint> by)
    {
        if (what.isEmpty())
        {
            return;
        }
        Collection<LuvPoint> l2end = BoundaryOrderer.searchFor4Nearest(what.get(what.size() - 1), by);
        if (!l2end.isEmpty())
        {
            what.clear();
        }
    }

    public List<LuvPoint> asList()
    {
        //TODO must not change branchingoponts
        while (!branchingoponts.isEmpty())
        {
            Node node = branchingoponts.peek();
            cunsumeNode();
            System.out.println("\t:Node " + node.number + " merged: after all");

        }

        resultList.add(0, firstppoint);

        return resultList;
    }

    public List<LuvPoint> curList()
    {

        ArrayList<LuvPoint> result = new ArrayList<LuvPoint>();

        Node node = branchingoponts.peek();

        List<LuvPoint> l1 = node.b1;
        List<LuvPoint> l2 = node.b2;

        List<LuvPoint> greater = l1;
        List<LuvPoint> lesser = l2;
        if (greater.size() < lesser.size())
        {
            greater = l2;
            lesser = l1;
        }

        for (LuvPoint luvPoint : lesser)
        {
            result.add(luvPoint);
        }
        for (LuvPoint luvPoint : greater)
        {
            result.add(luvPoint);
        }

        return result;

    }
}

class Node
{

    int number;
    ListIterator<LuvPoint> lead;

    public Node()
    {
    }

    public Node(int number, LuvPoint branchPoint)
    {
        this.number = number;
        this.branchPoint = branchPoint;
        this.b1 = new ArrayList<LuvPoint>();
        this.b2 = new ArrayList<LuvPoint>();
    }
    LuvPoint branchPoint;
    List<LuvPoint> b1;
    List<LuvPoint> b2;
}
