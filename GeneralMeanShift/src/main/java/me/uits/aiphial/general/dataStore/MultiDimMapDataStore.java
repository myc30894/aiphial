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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Data storage which is based on TreeMaps within TreeMaps
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
@SuppressWarnings("unchecked")
public class MultiDimMapDataStore<T extends NDimPoint> implements  DataStore<T>
{

    private int dim;
    private TreeMap<Float, Object> tm;
    //private SimpleNDimPoint window;

    public MultiDimMapDataStore(int dim)
    {
        this.dim = dim;
    }

    public T addOrGet(T v)
    {
        TreeMap<Float, Object> ref = getDepestNode(v);

        if (!ref.isEmpty())
        {
            return (T) ref.firstEntry().getValue();
        }

        ref.put(0f, v);
        return v;
    }

    public void add(T v)
    {
        TreeMap<Float, Object> ref = getDepestNode(v);

        try
        {
            Float lastKey = ref.lastKey();
            ref.put(lastKey + 1, v);
        } catch (NoSuchElementException e)
        {
            ref.put(0f, v);
        }

    }

    public void remove(NDimPoint v)
    {
        check(v);

        if (tm == null)
        {
            return;
        }

        TreeMap<Float, Object> ref = tm;

        Deque<TreeMap<Float, Object>> visitedNodes = new ArrayDeque<TreeMap<Float, Object>>(dim);


        for (int i = 0; i < v.getDimensions(); i++)
        {
            visitedNodes.push(ref);
            if (!ref.containsKey(v.getCoord(i)))
            {
                return;
            }
            ref = (TreeMap<Float, Object>) ref.get(v.getCoord(i));
        }

        ref.pollFirstEntry();

        for (int i = v.getDimensions() - 1; i >= 0; i--)
        {
            TreeMap<Float, Object> node = visitedNodes.pop();

            node.remove(v.getCoord(i));

            if (node.size() >= 1)
            {
                break;
            }
        }

    }

    public T getFirst()
    {
        if (tm == null)
        {
            return null;
        }

        TreeMap<Float, Object> ref = tm;


        for (int i = 0; i < dim; i++)
        {
            if (ref.isEmpty())
            {
                return null;
            }
            ref = (TreeMap<Float, Object>) ref.firstEntry().getValue();

        }

        return (T) ref.firstEntry().getValue();
    }

    public boolean isEmpty()
    {
        return (tm == null) || tm.isEmpty();
    }

    public void addAll(Collection<T> all)
    {
        for (T nDimPoint : all)
        {
            add(nDimPoint);
        }
    }

    public T getNearest(NDimPoint v)
    {
        check(v);

        TreeMap<Float, Object> ref = tm;

        for (int i = 0; i < v.getDimensions(); i++)
        {
            Entry<Float, Object> nearestEntry = getNearestLocal(ref, v.getCoord(i));

            ref = (TreeMap<Float, Object>) nearestEntry.getValue();
        }

        return (T) ref.firstEntry().getValue();

    }

    private Entry<Float, Object> getNearestLocal(TreeMap<Float, Object> ref, Float v)
    {

        Entry<Float, Object> floorEntry = ref.floorEntry(v);
        Entry<Float, Object> higherEntry = ref.higherEntry(v);

        if (floorEntry == null)
        {
            return higherEntry;
        }
        if (higherEntry == null)
        {
            return floorEntry;
        }

        if (v - floorEntry.getKey() > higherEntry.getKey() - v)
        {
            return higherEntry;
        } else
        {
            return floorEntry;
        }
    }

//    public Collection<T> getWithinWindow(NDimPoint v)
//    {
//        throw new UnsupportedOperationException("temprory unsupported");
//        //return getWithinWindow(window, v, false);
//    }

    public Collection<T> getWithinWindow(NDimPoint wind, NDimPoint v)
    {
        return getWithinWindow(wind, v, false);
    }


//    public Collection<T> removeWithinWindow(NDimPoint v)
//    {
//        throw new UnsupportedOperationException("temprory unsupported");
//        //return getWithinWindow(window, v, true);
//    }

    public Collection<T> removeWithinWindow(NDimPoint window, NDimPoint v)
    {
        return getWithinWindow(window, v, true);
    }

    private Collection<T> getWithinWindow(NDimPoint window, NDimPoint v, boolean delete)
    {
        TreeMap.SimpleEntry<Float, Object> entry = new SimpleEntry<Float, Object>(null, tm);

        return getWithinDimention(entry, asFloatList(v), asFloatList(window), delete);
    }

    private SortedMap<Float, Object> getSubmapWithinFirstRestiction(TreeMap<Float, Object> value, List<Float> v, List<Float> r)
    {
        if (v == null || r == null)
        {
            return value;
        }

        Float leastKey = v.get(0) - r.get(0);
        Float maxKey = v.get(0) + r.get(0);

        if (value != null)
        {
            return value.subMap(leastKey, maxKey);
        } else
        {
            return new TreeMap<Float, Object>();
        }

    }

    private TreeMap<Float, Object> getDepestNode(T v) throws IllegalArgumentException
    {
        check(v);
        if (tm == null)
        {
            tm = new TreeMap<Float, Object>();
        }
        TreeMap<Float, Object> ref = tm;
        for (int i = 0; i < v.getDimensions(); i++)
        {
            if (!ref.containsKey(v.getCoord(i)))
            {
                TreeMap<Float, Object> ref2 = new TreeMap<Float, Object>();
                ref.put(v.getCoord(i), ref2);
            }
            ref = (TreeMap<Float, Object>) ref.get(v.getCoord(i));
        }
        return ref;
    }

    private List<T> getWithinDimention(Entry<Float, Object> ref, List<Float> v, List<Float> r, boolean delete)
    {
        List<T> result = new ArrayList<T>();

        TreeMap<Float, Object> map = (TreeMap<Float, Object>) ref.getValue();

        SortedMap<Float, Object> subMap = getSubmapWithinFirstRestiction(map, v, r);

        List<Float> toRemove = new ArrayList<Float>();

        for (Entry<Float, Object> entry : subMap.entrySet())
        {
            List<T> withinSubDimention;
            if (r.size() > 1)
            {
                withinSubDimention = getWithinDimention(entry, tail(v), tail(r), delete);

                if (delete)
                {
                    TreeMap<Float, Object> innerTree = (TreeMap<Float, Object>) entry.getValue();

                    if (innerTree.size() == 0)
                    {
                        //map.remove(entry.getKey());
                        toRemove.add(entry.getKey());
                    }
                }

            } else
            {
                
                TreeMap<Float, Object> lastref = (TreeMap<Float, Object>) entry.getValue();
                
                withinSubDimention = new ArrayList<T>(lastref.size());


                for (Object object : lastref.values())
                {
                    withinSubDimention.add((T)object);
                }
                
                if (delete)
                {
                    //if (((TreeMap<Float, Object>) entry.getValue()).size() <= 1)
                    //{
                    //map.remove(entry.getKey());
                    toRemove.add(entry.getKey());
                //}
                }
            }
            result.addAll(withinSubDimention);
        }

        if (delete)
        {
            for (Float key : toRemove)
            {
                map.remove(key);
            }
        }

        return result;
    }

    public Iterator<T> iterator()
    {

        return asList().iterator();
    }

    public List<T> asList()
    {
        TreeMap.SimpleEntry<Float, Object> entry = new SimpleEntry<Float, Object>(null, tm);
        List<T> withinDimention = getWithinDimention(entry, null, asFloatList(new Float[dim]), false);
        return withinDimention;
    }

    public int getDim()
    {
        return dim;
    }

    private void check(NDimPoint v) throws IllegalArgumentException
    {
        if (v.getDimensions() != dim)
        {
            throw new IllegalArgumentException("illegal Point dimention, must be " + dim);
        }
    }

    public void setOptimalWindow(Float... window)
    {
        throw new UnsupportedOperationException("temprory unsupported");
        //this.window = new SimpleNDimPoint(window);
    }

    public Float[] getOptimalWindow()
    {
        throw new UnsupportedOperationException("temprory unsupported");
        //return window.getFloatData();
    }

    private static List<Float> tail(List<Float> list)
    {
        if (list == null)
        {
            return list;
        }
        return list.subList(1, list.size());
    }

    private List<Float> asFloatList(Float[] v)
    {
        return asFloatList(new SimpleNDimPoint(v));
    }

    private List<Float> asFloatList(NDimPoint v)
    {
        List<Float> vl = new ArrayList<Float>(v.getDimensions());

        for (int i = 0; i < v.getDimensions(); i++)
        {
            vl.add(v.getCoord(i));
        }

        return vl;
    }

    @Override
    public MultiDimMapDataStore<T> clone()
    {
        try
        {
            MultiDimMapDataStore<T> clone = (MultiDimMapDataStore<T>) super.clone();

            clone.tm = recurciveClone(tm);

            return clone;
        } catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private TreeMap<Float, Object> recurciveClone(TreeMap<Float, Object> orig)
    {
        TreeMap<Float, Object> result = new TreeMap<Float, Object>();

        for (Entry<Float, Object> entry : orig.entrySet())
        {
            if (entry.getValue() instanceof TreeMap)
            {
                result.put(entry.getKey(), recurciveClone((TreeMap<Float, Object>) entry.getValue()));
            } else
            {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;

    }

    public void optimize()
    {
        
    }
}
