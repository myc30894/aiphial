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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.abs;

/**
 * DataStore implementation specialized for LuvPoints.
 * Assumes that points form a non-sparse matrix, so two-dimensional search could be implemented easily.
 * Naturally this is an Image presentation as a dataStore
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class LuvDataStore implements DataStore<LuvPoint>
{

    private LuvPoint[][] luvPointses;
    //private int count = 0;

    private Set<LuvPoint> pset;

    public LuvDataStore(LUV[][] luvarray)
    {
        int linelen = luvarray[0].length;

        luvPointses = new LuvPoint[luvarray.length][linelen];

        pset = new HashSet<LuvPoint>(linelen*luvPointses.length);

        for (int y = 0; y < luvarray.length; y++)
        {
            LUV[] line = luvarray[y];
            for (int x = 0; x < linelen; x++)
            {
                luvPointses[y][x] = new LuvPoint(x, y, luvarray[y][x]);
                //count++;
                pset.add(luvPointses[y][x]);

            }

        }

    }

    public void add(LuvPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addAll(Collection<LuvPoint> all)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LuvPoint addOrGet(LuvPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<LuvPoint> asList()
    {
        List<LuvPoint> result = new ArrayList<LuvPoint>(luvPointses.length * luvPointses[0].length);

        for (LuvPoint[] luvPoints : luvPointses)
        {
            for (LuvPoint luvPoint : luvPoints)
            {
                result.add(luvPoint);
            }
        }

        return result;

    }

    public DataStore<LuvPoint> clone()
    {


        try
        {
            LuvDataStore copy = (LuvDataStore) super.clone();
            copy.luvPointses = luvPointses.clone();
            copy.pset = new HashSet<LuvPoint>(pset);
            
            return copy;

        } catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public int getDim()
    {
        return 5;
    }

    public LuvPoint getFirst()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LuvPoint getNearest(NDimPoint v)
    {
        LuvPoint anypoint = pset.iterator().next();
        return anypoint;
    }


    public LuvPoint getNearest0(NDimPoint v)
    {
        //return luvPointses[v.get(1).intValue()][v.get(0).intValue()];

        LuvPoint centr = new LuvPoint(v);

        for (int d = 0; d < max(luvPointses.length, luvPointses[0].length); d++)
        {

            for (int y = max(0, centr.getY() - d); y < min(luvPointses.length, centr.getY() + d + 1); y++)
            {
                for (int x = max(0, centr.getX() - d); x < min(luvPointses[0].length, centr.getX() + d + 1); x++)
                {
                    if (luvPointses[y][x] != null)
                    {
                        return luvPointses[y][x];
                    }

                }

            }
        }

        return null;

    }

    public Collection<LuvPoint> getWithinWindow(NDimPoint window, NDimPoint v)
    {
        LuvPoint w = new LuvPoint(window);
        LuvPoint centr = new LuvPoint(v);

        List<LuvPoint> result = new ArrayList<LuvPoint>();

        for (int y = max(0, centr.getY() - w.getY()); y < min(luvPointses.length, centr.getY() + w.getY() + 1); y++)
        {
            for (int x = max(0, centr.getX() - w.getX()); x < min(luvPointses[0].length, centr.getX() + w.getX() + 1); x++)
            {
                check(y, x, centr, w, result);

            }

        }

        return result;

    }

    private void check(int y, int x, LuvPoint centr, LuvPoint w, List<LuvPoint> result)
    {
        if (luvPointses[y][x] != null)
        {
            LUV c = luvPointses[y][x].getLUV();
            if (abs(c.l() - centr.getLUV().l()) < w.getLUV().l() && abs(c.u() - centr.getLUV().u()) < w.getLUV().u() && abs(c.v() - centr.getLUV().v()) < w.getLUV().v())
            {
                result.add(luvPointses[y][x]);
            }
        }
    }


    int falses = 0;

    public boolean isEmpty()
    {
        /*if(!pset.isEmpty())
            falses++;
        if(falses>10000 || pset.isEmpty())
        {
        System.out.println("isEmpty:"+pset.isEmpty()+" f:"+falses);
        falses = 0;
        }*/
        return pset.isEmpty();
        //return count <= 0;
    }

    public Iterator<LuvPoint> iterator()
    {
        return this.asList().iterator();
    }

    public void remove(NDimPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<LuvPoint> removeWithinWindow(NDimPoint window, NDimPoint v)
    {
        LuvPoint w = new LuvPoint(window);
        LuvPoint centr = new LuvPoint(v);

        List<LuvPoint> result = new ArrayList<LuvPoint>();

        for (int y = max(0, centr.getY() - w.getY()); y < min(luvPointses.length, centr.getY() + w.getY() + 1); y++)
        {
            for (int x = max(0, centr.getX() - w.getX()); x < min(luvPointses[0].length, centr.getX() + w.getX() + 1); x++)
            {
                if (luvPointses[y][x] != null)
                {
                    LUV c = luvPointses[y][x].getLUV();
                    if (abs(c.l() - centr.getLUV().l()) < w.getLUV().l() && abs(c.u() - centr.getLUV().u()) < w.getLUV().u() && abs(c.v() - centr.getLUV().v()) < w.getLUV().v())
                    {
                        result.add(luvPointses[y][x]);
                        pset.remove(luvPointses[y][x]);
                        luvPointses[y][x] = null;
                        //count--;
                        
                    }
                }
            }
        }

        return result;
    }

    public void setOptimalWindow(Float... window)
    {
        
    }

    public Float[] getOptimalWindow()
    {
        return null;
    }

    public void optimize()
    {
       
    }


}
