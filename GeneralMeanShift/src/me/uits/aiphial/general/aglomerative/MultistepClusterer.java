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

package me.uits.aiphial.general.aglomerative;

import me.uits.aiphial.general.basic.Clusterer;
import me.uits.aiphial.general.dataStore.NDimPoint;

/**
 * Interface for clusterers that have several steps and result could be obtained on each step
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public interface MultistepClusterer<T extends NDimPoint> extends Clusterer<T> {

    /**
     * add listener that would be notified when one clustering step is done
     * @param iterationListener
     */
    void addIterationListener(IterationListener<T> iterationListener);

    /**
     * remove listener
     * @param iterationListener
     */
    void removeIterationListener(IterationListener<T> iterationListener);

}
