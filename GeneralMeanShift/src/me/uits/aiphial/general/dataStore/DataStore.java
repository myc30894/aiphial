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
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public interface DataStore<T extends NDimPoint> extends Cloneable, Iterable<T> {

    void add(T v);

    void addAll(Collection<T> all);

    T addOrGet(T v);

    List<T> asList();
    
    public DataStore<T> clone();

    int getDim();

    T getFirst();

    T getNearest(NDimPoint v);

    

    //Collection<T> getWithinWindow(NDimPoint v);

    Collection<T> getWithinWindow(NDimPoint window, NDimPoint v);

    boolean isEmpty();

    Iterator<T> iterator();

    void remove(NDimPoint v);

    //Collection<T> removeWithinWindow(NDimPoint v);

    Collection<T> removeWithinWindow(NDimPoint window, NDimPoint v);

    void setOptimalWindow(Float... window);
    Float[] getOptimalWindow();

    void optimize();

}
