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

import me.uits.aiphial.general.dataStore.DataStoreFactory;
import me.uits.aiphial.general.basic.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.basic.Bof;
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
@SuppressWarnings("unchecked")
public class AglomerativeMeanShift<T extends NDimPoint> extends AbstractAglomerativeClusterer<T> implements IMeanShiftClusterer<T>
{

    private SimpleBandwidthSelector bandwidthSelector = new SimpleBandwidthSelector();
    private DataStore<? extends T> initialDataStore;
    private int maxIterations = 20;
    private List<Cluster<T>> result;
    private IMeanShiftClusterer innerAlgorithm;
    private float windowMultiplier = 0.4F;
    private Float[] window;
    

    private boolean stop;
    private boolean autostopping = true;

    
    public void doClustering()
    {
        stop = false;
        innerAlgorithm.setDataStore(initialDataStore);
        if(window==null)
            window = genBandwidth(initialDataStore);

        innerAlgorithm.setWindow(window);
        innerAlgorithm.doClustering();

        List clusters = innerAlgorithm.getClusters();

        fireIterationDone(getClustersOfInitialPoints(clusters));

        for (int i = 0; i < maxIterations; i++)
        {
            List<Cluster<Bof<T>>> clusterOfBasins = aglomerate(clusters);
            fireIterationDone(getClustersOfInitialPoints(clusterOfBasins));

            if (autostopping && (clusterOfBasins.size()==clusters.size()))
            {
                stop = true;
            }
            if (clusterOfBasins.size()<=1 || stop)
            {
                break;
            }

            clusters = clusterOfBasins;
        }

        result = getClustersOfInitialPoints(clusters);
    }


    private <BT extends NDimPoint> List<Cluster<Bof<BT>>> aglomerate(List<Cluster<BT>> clusters)
    {
        //DataStore<Bof> curDataStore = new MultiDimMapDataStore<Bof>(initialDataStore.getDim());
        DataStore<Bof<BT>> curDataStore = dataStoreFactory.createDataStore(initialDataStore.getDim());
        for (Cluster<BT> cluster : clusters)
        {
            curDataStore.add(getFilledBof(cluster));
        }
        //curDataStore.setWindow(genBandwidth(curDataStore));
        innerAlgorithm.setDataStore(curDataStore);
        innerAlgorithm.setWindow(genBandwidth(curDataStore));
        innerAlgorithm.doClustering();
        return innerAlgorithm.getClusters();
    }

    private <BT extends NDimPoint> Float[] genBandwidth(DataStore<BT> curDataStore)
    {
        Float[] bandwidth = bandwidthSelector.getBandwidth(curDataStore);

        Float[] res = new Float[bandwidth.length];

        for (int i = 0; i < res.length; i++)
        {
            res[i] = bandwidth[i] * windowMultiplier;
        }

        return res;
    }

    public AglomerativeMeanShift(IMeanShiftClusterer<NDimPoint> innerAlgorithm)
    {
        this.innerAlgorithm = innerAlgorithm;
    }

    // <editor-fold defaultstate="collapsed" desc="gettersandsetters">
    public List<Cluster<T>> getClusters()
    {
        return result;
    }

    public void setDataStore(DataStore<? extends T> dataStore)
    {
        this.initialDataStore = dataStore;
    }

    public void setInnerAlgorithm(IMeanShiftClusterer<NDimPoint> innerAlgorithm)
    {
        this.innerAlgorithm = innerAlgorithm;
    }

    public IMeanShiftClusterer getInnerAlgorithm()
    {
        return innerAlgorithm;
    }

    public void setWindow(Float... window)
    {
        this.window = window;
    }

    public boolean isAutostopping()
    {
        return autostopping;
    }

    public void setAutostopping(boolean autostopping)
    {
        this.autostopping = autostopping;
    }

    public void setWindowMultiplier(float windowMultiplier)
    {
        this.windowMultiplier = windowMultiplier;
    }

    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    public float getWindowMultiplier()
    {
        return windowMultiplier;
    }

    public int getMaxIterations()
    {
        return maxIterations;
    }

    public void stop()
    {
        stop = true;
    }
    // </editor-fold>
}
