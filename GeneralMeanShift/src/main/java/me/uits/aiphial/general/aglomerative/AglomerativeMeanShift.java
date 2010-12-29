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
 * Agglomerative mean-shift clusterer
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
@SuppressWarnings("unchecked")
public class AglomerativeMeanShift<T extends NDimPoint> extends AbstractAglomerativeClusterer<T> implements IMeanShiftClusterer<T>
{

    private BandwidthSelector bandwidthSelector = new SimpleBandwidthSelector();
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
        if (window == null)
        {
            window = genBandwidth(initialDataStore);
        }

        innerAlgorithm.setWindow(window);
        innerAlgorithm.doClustering();

        List clusters = innerAlgorithm.getClusters();

        fireIterationDone(getClustersOfInitialPoints(clusters));

        for (int i = 0; i < maxIterations; i++)
        {
            List<Cluster<Bof<T>>> clusterOfBasins = aglomerate(clusters);
            fireIterationDone(getClustersOfInitialPoints(clusterOfBasins));

            if (autostopping && (clusterOfBasins.size() == clusters.size()))
            {
                stop = true;
            }
            if (clusterOfBasins.size() <= 1 || stop)
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
        Float[] bandwidth = getBandwidthSelector().getBandwidth(curDataStore);

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

    /**
     * sets an inner mean-shift algorithm to perform clusterization on each step
     * @param innerAlgorithm - an inner mean-shift algorithm to perform clusterization on each step
     */
    public void setInnerAlgorithm(IMeanShiftClusterer<NDimPoint> innerAlgorithm)
    {
        this.innerAlgorithm = innerAlgorithm;
    }

    /**
     * returns an inner mean-shift algorithm to perform clusterization on each step
     * @return inner mean-shift algorithm to perform clusterization on each step
     */
    public IMeanShiftClusterer getInnerAlgorithm()
    {
        return innerAlgorithm;
    }

    public void setWindow(Float... window)
    {
        this.window = window;
    }

    /**
     * @return whether the algorithm stops automatically
     * if no new clusters were allocated on this step
     */
    public boolean isAutostopping()
    {
        return autostopping;
    }

    /**
     * specifies whether the algorithm stops automatically
     * if no new clusters were allocated on this step
     * @param autostopping
     */
    public void setAutostopping(boolean autostopping)
    {
        this.autostopping = autostopping;
    }

    /**
     * sets a window coefficient. a value which multiplies
     * the size of the window(bandwidth) after the automatic detection
     * lower values produces more clusters on a step
     * @param windowMultiplier
     */
    public void setWindowMultiplier(float windowMultiplier)
    {
        this.windowMultiplier = windowMultiplier;
    }

    /**
     * sets the limit of agglomerative steps number
     * @param maxIterations the limit of agglomerative steps number
     */
    public void setMaxIterations(int maxIterations)
    {
        this.maxIterations = maxIterations;
    }

    /**
     * @return a window coefficient. a value which multiplies
     * the size of the window(bandwidth) after the automatic detection
     * lower values produces more clusters on a step
     */
    public float getWindowMultiplier()
    {
        return windowMultiplier;
    }

    /**
     * @return the limit of agglomerative steps number
     */
    public int getMaxIterations()
    {
        return maxIterations;
    }

    /**
     * safely terminates the clusterization process performed by this object
     * normally could be called from iteration listeners
     */
    public void stop()
    {
        stop = true;
    }

    /**
     * @return the bandwidthSelector
     */
    public BandwidthSelector getBandwidthSelector()
    {
        return bandwidthSelector;
    }

    /**
     * @param bandwidthSelector the bandwidthSelector to set
     */
    public void setBandwidthSelector(BandwidthSelector bandwidthSelector)
    {
        this.bandwidthSelector = bandwidthSelector;
    }
    // </editor-fold>
}
