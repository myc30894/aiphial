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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class ListDataStore<T extends NDimPoint> implements  DataStore<T> {

    private int dim;

    private SimpleNDimPoint window;

    SortedSet<T> f;

    ListDataStore(int dimention)
    {
        this.dim = dimention;
    }

    public void add(T v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addAll(Collection<T> all)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public T addOrGet(T v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<T> asList()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ListDataStore<T> clone()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }   

    public T getFirst()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public T getNearest(NDimPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Float[] getOptimalWindow()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<T> getWithinWindow(NDimPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<T> getWithinWindow(NDimPoint window, NDimPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEmpty()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Iterator<T> iterator()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void remove(NDimPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<T> removeWithinWindow(NDimPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<T> removeWithinWindow(NDimPoint window, NDimPoint v)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setOptimalWindow(Float... window)
    {
        this.window = new SimpleNDimPoint(window);
    }

    public int getDim()
    {
        return dim;
    }

    public void optimize()
    {
        
    }

}
