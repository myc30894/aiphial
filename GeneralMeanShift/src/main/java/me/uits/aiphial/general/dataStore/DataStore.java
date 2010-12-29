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

/**
 * Interface for all data storages. This objects store a set of
 * n-dimension points and allow to do nearest neighbor search 
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public interface DataStore<T extends NDimPoint> extends Cloneable, Iterable<T> {

    /**
     * add point to storage
     * @param v
     */
    void add(T v);

    /**
     * add collection of points to storage
     * @param all
     */
    void addAll(Collection<T> all);

    /**
     * if storage contains this point returns containing point
     * otherwise adds point to storage and returns added point
     * @param v
     * @return
     */
    T addOrGet(T v);

    /**
     * @return list that contains all points from storage
     */
    List<T> asList();
    
    /**
     * @return deep clone of this storage
     */
    public DataStore<T> clone();

    /**
     * @return the dimension of points in this storage
     */
    int getDim();

    /**
     * @return an arbitrary (but not random) point from this storage
     */
    T getFirst();

    /**
     * @return a point from this storage that is closest to the given point
     */
    T getNearest(NDimPoint v);
   
    

    /**
     * returns points from this storage that are bounded with
     * hyper-parallelepiped window with center <i>v</i> and size <i>window</i>
     * @param window - size of the window
     * @param v - center of the window
     * @return points from this storage within window
     */
    Collection<T> getWithinWindow(NDimPoint window, NDimPoint v);

    /**
     * @return true if this storage contains an least one point,
     * or false otherwise
     */
    boolean isEmpty();

    /**
     * @return an iterator over points in this storage
     */
    Iterator<T> iterator();

    /**
     * remove point from this storage. Does nothing if storage doesn't contain any
     * @param v
     */
    void remove(NDimPoint v);

  
    /**
     * removes all points from this storage that are bounded with
     * hyper-parallelepiped window with center <i>v</i> and size <i>window</i>
     * @param window - size of the window
     * @param v - center of the window
     * @return points from this storage within window
     */
    Collection<T> removeWithinWindow(NDimPoint window, NDimPoint v);

    /**
     * sets the optimal window size for window-queries
     * @param window
     */
    void setOptimalWindow(Float... window);

    /**
     *
     * @return the optimal window size for window-queries
     */
    Float[] getOptimalWindow();

    /**
     * optimize data storage structure after adding elements.
     * For example it could me tree-balancing
     */
    void optimize();

}
