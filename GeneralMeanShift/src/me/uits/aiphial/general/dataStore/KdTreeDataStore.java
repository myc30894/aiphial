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

package me.uits.aiphial.general.dataStore;

import com.savarese.spatial.KDTree;
import com.savarese.spatial.RangeSearchTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class KdTreeDataStore <T extends NDimPoint> implements  DataStore<T> {
    KDTree<Float,NDimPoint,T> rst;
    int dim;
    public KdTreeDataStore(int dim)
    {
        this.dim = dim;
       rst = new KDTree<Float, NDimPoint, T>(dim);
    }


    public void add(T v)
    {
        rst.put(v, v);
    }

    public void addAll(Collection<T> all)
    {
        for (T v : all)
        {
            add(v);
        }
        //rst.optimize();
    }

    public void optimize()
    {
        rst.optimize();
    }

    public T addOrGet(T v)
    {
        T r = rst.get(v);
        if(r != null)
        {
           return r;
        }
        else
        {
            add(v);
            return v;
        }
    }

    public List<T> asList()
    {
      return new ArrayList<T>(rst.values());
    }

    public KdTreeDataStore<T> clone()
    {
        /*
        try
        {
        return (KdTreeDataStore<T>) super.clone();
        } catch (CloneNotSupportedException ex)
        {
        throw new RuntimeException(ex);
        }*/

        List<T> items = this.asList();
        KdTreeDataStore<T> result = new KdTreeDataStore<T>(this.getDim());
        result.addAll(items);
        return result;
    }

    public int getDim()
    {
        return dim;
    }

    public T getFirst()
    {
       return rst.values().iterator().next();
    }

    public T getNearest(NDimPoint v)
    {
        Iterator<Entry<NDimPoint, T>> iup = rst.iterator(v, null);
        if (iup.hasNext())
        {
            return iup.next().getValue();
        }
        Iterator<Entry<NDimPoint, T>> lu = rst.iterator(null,v);
        if (lu.hasNext())
        {
            return lu.next().getValue();
        }
        Iterator<Entry<NDimPoint, T>> all = rst.iterator(null, null);
        if (all.hasNext())
        {
            return all.next().getValue();
        }
        return  null;

    }

    public Collection<T> getWithinWindow(NDimPoint window, NDimPoint v)
    {
        final int dim = window.getDimensions();
        SimpleNDimPoint lower = new SimpleNDimPoint(new Float[dim]);
        SimpleNDimPoint upper = new SimpleNDimPoint(new Float[dim]);

        for (int i = 0; i < dim; i++)
        {
            lower.setCoord(i,v.getCoord(i)-window.getCoord(i));
            upper.setCoord(i,v.getCoord(i)+window.getCoord(i));
        }

        Iterator<Entry<NDimPoint, T>> iterator = rst.iterator(lower, upper);

        ArrayList<T> result = new ArrayList<T>();

        while (iterator.hasNext())
        {
            result.add(iterator.next().getValue());
        }

        return result;

 
    }

    public boolean isEmpty()
    {
       return rst.isEmpty();
    }

    public Iterator<T> iterator()
    {
        return  rst.values().iterator();
    }

    public void remove(NDimPoint v)
    {
        rst.remove(v);
    }

    public Collection<T> removeWithinWindow(NDimPoint window, NDimPoint v)
    {
        Collection<T> withinWindow = getWithinWindow(window, v);
        for (NDimPoint nDimPoint : withinWindow)
        {
            remove(nDimPoint);
        }

        return withinWindow;
    }

    public void setOptimalWindow(Float... window)
    {
        
    }

    public Float[] getOptimalWindow()
    {
        return  null;
    }

}
