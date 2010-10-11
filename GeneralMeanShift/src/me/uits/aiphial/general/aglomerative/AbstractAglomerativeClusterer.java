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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.uits.aiphial.general.basic.Bof;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.Clusterer;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.DataStoreFactory;
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory;
import me.uits.aiphial.general.dataStore.NDimPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public abstract class AbstractAglomerativeClusterer<T extends NDimPoint> implements Clusterer<T>, MultistepClusterer<T> {
    protected List<IterationListener<T>> iterationListeners = new ArrayList<IterationListener<T>>();

    protected  DataStoreFactory dataStoreFactory = DefaultDataStoreFactory.get();
    public AbstractAglomerativeClusterer()
    {
    }

    protected void fireIterationDone(List<Cluster<T>> clusters)
    {
        for (IterationListener iterationListener : iterationListeners)
        {
            iterationListener.IterationDone(clusters);
        }
    }

    protected <T1 extends NDimPoint> ArrayList<Cluster<T>> getClustersOfInitialPoints(Collection<Cluster<Bof<T1>>> clusters)
    {
        ArrayList<Cluster<T>> result0 = new ArrayList<Cluster<T>>(clusters.size());
        for (Cluster cluster : clusters)
        {
            Bof filledBof = getFilledBof(cluster);
            result0.add(new Cluster(filledBof, filledBof.points));
        }
        return result0;
    }

    public DataStoreFactory getDataStoreFactory()
    {
        return dataStoreFactory;
    }

    protected <BT extends NDimPoint> Bof<BT> getFilledBof(Cluster<BT> cluster)
    {
        if (cluster.get(0) instanceof Bof)
        {
            ArrayList<BT> al = new ArrayList<BT>();
            for (NDimPoint nDimPoint : cluster)
            {
                al.addAll(((Bof) nDimPoint).points);
            }
            return new Bof<BT>(cluster.getBasinOfAttraction(), al);
        } else
        {
            return new Bof<BT>(cluster.getBasinOfAttraction(), cluster);
        }
    }

    public void setDataStoreFactory(DataStoreFactory dataStoreFactory)
    {
        this.dataStoreFactory = dataStoreFactory;
    }

    public void addIterationListener(IterationListener<T> iterationListener)
    {
        this.iterationListeners.add(iterationListener);
    }

    public void removeIterationListener(IterationListener<T> iterationListener)
    {
        this.iterationListeners.remove(iterationListener);
    }

}
