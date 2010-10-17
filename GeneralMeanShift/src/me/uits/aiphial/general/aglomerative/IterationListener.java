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

import java.util.Collection;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.dataStore.NDimPoint;

/**
 * Imterface for listeners of multistep clusterers
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public interface IterationListener<T extends NDimPoint>
{

    public void IterationDone(Collection<? extends Cluster<T>> clusters);
}
