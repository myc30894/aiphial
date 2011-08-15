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


package me.uits.aiphial.general.basic;

import java.util.ArrayList;
import java.util.Collection;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;

/**
 * Cluster, a collection of points that were united after clusterization because of similarity
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class Cluster<T extends NDimPoint> extends ArrayList<T>{

    public Cluster()
    {
    }

    

    public Cluster(NDimPoint basinOfAttraction)
    {
        this.basinOfAttraction = basinOfAttraction;
    }

    public Cluster(NDimPoint basinOfAttraction, Collection<T> collection)
    {
        super(collection);
        this.basinOfAttraction = basinOfAttraction;
    }
    
    public Cluster(Collection<T> collection)
    {
        super(collection);
        this.basinOfAttraction = Utls.getAvragePoint(collection);
    }

    private NDimPoint basinOfAttraction;

    /**
     * returns the characteristic point of this cluster (center of cluster)
     * if was not set, calculates average point
     * @return
     */
    public NDimPoint getBasinOfAttraction()
    {
        if (basinOfAttraction == null)
        {
            basinOfAttraction = Utls.getAvragePoint(this);
        }
        return basinOfAttraction;
    }

    /**
     * sets the characteristic point of this cluster (center of cluster)
     * @param basinOfAttraction
     * @deprecated avoid changing Bof after creating because it is used to calculate hashCode
     */
    @Deprecated
    public void setBasinOfAttraction(NDimPoint basinOfAttraction)
    {
        this.basinOfAttraction = basinOfAttraction;
    }


    @Override
    public int hashCode()
    {
        // I know this is a hack, but list hashes are too slow
        return this.getBasinOfAttraction().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {        
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Cluster<T> other = (Cluster<T>) obj;
        this.getBasinOfAttraction();
        if (this.basinOfAttraction != other.basinOfAttraction && (this.basinOfAttraction == null || !this.basinOfAttraction.equals(other.basinOfAttraction)))
        {
            return false;
        }
        return true;
    }



}
