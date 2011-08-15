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

package me.uits.aiphial.imaging.searching;

import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.imaging.Region;

/**
 * base interface for region comparers. each instance of
 * this class keeps a pattern and compares region with it
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public interface RegionComparer {

    /**
     * returns distance between regions. Zero value means that given region is equal to pattern.
     * Lager values means more difference between pattern and given regions
     * @param cluster
     * @return
     */
    double compareCluster(Region cluster);

    /**
     * sets pattern to compare with
     * @param pattern
     */
    void setPattern(Region pattern);

}
