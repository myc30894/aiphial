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

import com.savarese.spatial.Point;

/**
 * Multidimension point. A unit that could be clustered
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public interface NDimPoint extends Point<Float> {

    /**
     * return value at i-th dimension
     */
    Float getCoord(int i);

    /**
     * set value at i-th dimension
     */
    void setCoord(int i, Float v);

    /**
     * returns a number of dimensions for this point
     * @return a number of dimensions for this point
     */
    int getDimensions();

    /**
     * returns a weight of this point (usally returns 1). This is needed for some algorithms
     * @return
     */
    float getWeight();

}
