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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import me.uits.aiphial.general.basic.Bof;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.Clusterer;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.DataStoreFactory;
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory;
import me.uits.aiphial.general.dataStore.NDimPoint;

/**
 * Class to build agglomerative clusterers from other clusterers by connecting them into sequence.
 * This means that results of clustering with first clusterer would be an the input data to second clusterers
 * end so on.
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class AglomerativeClustererStack<T extends NDimPoint> extends AbstractAglomerativeClusterer<T> implements Clusterer<T>
{

    private Clusterer<T> initialClusterer;
    private DataStore<? extends T> initialDataStore;
    private LinkedList<Clusterer<NDimPoint>> clusterers = new LinkedList<Clusterer<NDimPoint>>();
    private List<Cluster<T>> result;

    public void doClustering()
    {

        initialClusterer.setDataStore(initialDataStore);
        initialClusterer.doClustering();
        List<Cluster<T>> clusters = (List<Cluster<T>>) initialClusterer.getClusters();
        final int dim = clusters.iterator().next().iterator().next().getDimensions();

        DataStore<Bof> curDataStore = dataStoreFactory.createDataStore(dim);
        for (Cluster<T> cluster : clusters)
        {
            curDataStore.add(getFilledBof(cluster));
        }


        ListIterator<Clusterer<NDimPoint>> clusteresIterator = clusterers.listIterator();

        fireIterationDone(clusters);
        List clusters1 = clusters;

        while (clusteresIterator.hasNext())
        {
            Clusterer curClusterer = clusteresIterator.next();

            curClusterer.setDataStore(curDataStore);
            curClusterer.doClustering();
            clusters1 = curClusterer.getClusters();

            fireIterationDone(getClustersOfInitialPoints(clusters1));
            curDataStore = dataStoreFactory.createDataStore(dim);

            for (Object cluster : clusters1)
            {
                curDataStore.add(getFilledBof((Cluster) cluster));
            }

        }

        result = (List<Cluster<T>>) getClustersOfInitialPoints(clusters1);


    }

    public List<Cluster<T>> getClusters()
    {
        return result;
    }

    public void setDataStore(DataStore<? extends T> dataStore)
    {
        initialDataStore = dataStore;
    }

    // <editor-fold defaultstate="collapsed" desc="gettersandsetters">
    /**
     * set the first clusterer in seq
     * @param initialClusterer - the first clusterer in seq
     */
    public void setInitialClusterer(Clusterer<T> initialClusterer)
    {
        if (this.initialClusterer != null)
        {
            clusterers.addFirst((Clusterer<NDimPoint>) this.initialClusterer);
        }

        this.initialClusterer = initialClusterer;
    }
/**
 *
 * @return the first clusterer in seq
 */
    public Clusterer<T> getInitialClusterer()
    {
        return initialClusterer;
    }

    /**
     * add a clusterer to seq
     * @param clusterer
     */
    public void addClustererToQueue(Clusterer<NDimPoint> clusterer)
    {
        if (initialClusterer == null)
        {
            setInitialClusterer((Clusterer<T>) clusterer);
        } else
        {
            this.clusterers.add(clusterer);
        }
    }


    IterationListener extendinglistener = new IterationListener(){

        public void IterationDone(Collection clusters)
        {
            AglomerativeClustererStack.this.fireIterationDone(getClustersOfInitialPoints(clusters));
        }

    };

    /**
     * add a multistep(agglomerative) clusterer to seq
     * @param clusterer
     */
    public void addExtendingClustererToQueue(MultistepClusterer<T> clusterer)
    {

        clusterer.addIterationListener(extendinglistener);

        if (initialClusterer == null)
        {
            setInitialClusterer((Clusterer<T>) clusterer);
        } else
        {
            this.clusterers.add((Clusterer<NDimPoint>) clusterer);
        }
    }

    // </editor-fold>
}
