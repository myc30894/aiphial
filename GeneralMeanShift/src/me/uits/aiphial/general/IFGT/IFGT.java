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

package me.uits.aiphial.general.IFGT;

import java.util.Collection;
import me.uits.aiphial.general.dataStore.NDimPoint;

// TODO complete or remove
/**
 *
 * @deprecated depends on missing native implementation
 * must be reimplemented or removed
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
@Deprecated
public class IFGT {
    
    private double eps;
    
    private int clusterLimit;

    public void setSourcePoints(Collection<? extends NDimPoint> sourcePoints)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Float[] doTransform(Collection<? extends NDimPoint> targetPoints)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the eps
     */
    public double getEps() {
        return eps;
    }

    /**
     * @param eps the eps to set
     */
    public void setEps(double eps) {
        this.eps = eps;
    }

    /**
     * @return the clusterLimit
     */
    public int getClusterLimit() {
        return clusterLimit;
    }

    /**
     * @param clusterLimit the clusterLimit to set
     */
    public void setClusterLimit(int clusterLimit) {
        this.clusterLimit = clusterLimit;
    }

}
