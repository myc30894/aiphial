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
import java.util.Collections;
import java.util.List;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.DataStoreFactory;
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;
import static me.uits.aiphial.general.basic.Utls.distance;

/**
 * Base (naive) implementation of mean-shift clusterer
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class MeanShiftClusterer<T extends NDimPoint> implements IMeanShiftClusterer<T>
{

    protected  DataStore<? extends T> dataStore;
    protected  float minDistance = 3.0F;
    protected  DataStore<Bof<T>> resultStore;
    protected DataStoreFactory dataStoreFactory = DefaultDataStoreFactory.get();

    protected List<Cluster<T>> result;
    protected SimpleNDimPoint window;


    Float ws;
    
    public void setDataStore(DataStore<? extends T> dataStore)
    {
        this.dataStore = dataStore;
    }

    public void doClustering()
    {

        //resultStore = new MultiDimMapDataStore(dataStore.getDim());
        resultStore = dataStoreFactory.createDataStore(dataStore.getDim());


        //resultStore.setWindow(window);


        int pc = 0;
        // TODO: it is a very big overhead to use asList to compute elem count
        float count = !this.progressListeners.isEmpty()?dataStore.asList().size():Float.NaN;
        
        for (T nDimPoint : dataStore)
        {
            NDimPoint calkBofA = calkBofA(nDimPoint);
                      
            Bof<T> bof = resultStore.addOrGet(new Bof<T>(calkBofA));
            bof.points.add(nDimPoint);

            fireProgressListeners(++pc/count * 0.95f);

        }

        performClusters();

        fireProgressListeners(1f);

    }


    protected  void performClusters()
    {             

        result = new ArrayList<Cluster<T>>();

        //TODO: it a very big overhead
        int maxiterations = resultStore.asList().size();

        int i = 0;
        while (!resultStore.isEmpty())
        {

            Bof bof = resultStore.getFirst();
            
            Cluster<T> newCluster = new Cluster<T>();

            Collection<Bof<T>> withinWindow = resultStore.getWithinWindow(this.window,bof);
            
            for (Bof<T> bof1 : withinWindow)
            {               
                   newCluster.addAll(bof1.points);
                   resultStore.remove(bof1);
            }

            if(!newCluster.isEmpty())
            {
                newCluster.setBasinOfAttraction(bof);
                result.add(newCluster);
            }

            if(i++>maxiterations)
                throw new RuntimeException("cannot drain result store, "+resultStore.asList().size()+" elements left");

        }

    }


    public List<Cluster<T>> getClusters()
    {
        return Collections.unmodifiableList(result);

    }


    protected  NDimPoint calkBofA(NDimPoint nDimPoint)
    {

        int i = 1000;


        NDimPoint shiftedPoint = nDimPoint;
        do
        {
            
           
            nDimPoint = shiftedPoint;
            shiftedPoint = calkOneShift(nDimPoint);            

        } while (distance(shiftedPoint, nDimPoint) > getMinDistance() && i-- > 0);

        if(i<=0) System.err.println("end of iterations limit");

        return shiftedPoint;
    }

    protected  NDimPoint calkOneShift(NDimPoint nDimPoint)
    {

        Collection<? extends  NDimPoint> withinWindow = dataStore.getWithinWindow(this.window, nDimPoint);

        Float[] averageData = new Float[nDimPoint.getDimensions()];

        for (int i = 0; i < averageData.length; i++)
        {
            averageData[i] = 0f;
        }


        Float kSum = 0f;

        for (NDimPoint nDimPoint1 : withinWindow)
        {
            final float weight = nDimPoint1.getWeight();
            Float k =ws - distance(nDimPoint, nDimPoint1);
            for (int i = 0; i < averageData.length; i++)
            {
                averageData[i] += k*nDimPoint1.getCoord(i)*weight;
            }
            kSum+=k*weight;
        }
        for (int i = 0; i < averageData.length; i++)
        {
            averageData[i] = averageData[i] / kSum;
            if(averageData[i].isNaN()||averageData[i].isInfinite())
                throw new RuntimeException(averageData[i]+" in clustering result");

        }


        return new SimpleNDimPoint(averageData);
    }
    

    /**
     * @return the minimum distance between points in order to be considered identical
     */
    public float getMinDistance()
    {
        return minDistance;
    }

    /**
     * @param minDistance the minimum distance between points in order to be considered identical
     */
    public void setMinDistance(float minDistance)
    {
        this.minDistance = minDistance;
    }

    



    public void setWindow(Float... window) {
        this.window = new SimpleNDimPoint(window);
        this.ws = distance(SimpleNDimPoint.getZeroPoint(this.window.getDimensions()), this.window);
    }

    /**
     * @return the dataStore Factory that is used to produce data storages for resulting data
     */
    public DataStoreFactory getDataStoreFactory()
    {
        return dataStoreFactory;
    }

    /**
     * @param dataStoreFactory - the dataStore Factory
     * that would be used to produce data storages for resulting data
     */
    public void setDataStoreFactory(DataStoreFactory dataStoreFactory)
    {
        this.dataStoreFactory = dataStoreFactory;
    }


    private List<ProgressListener> progressListeners = new ArrayList<ProgressListener>(1);


    protected void fireProgressListeners(float v){
        for (ProgressListener pl : progressListeners)
        {
            pl.onStepDone(v);
        }
    }

    public void addProgressListener(ProgressListener pl){
        progressListeners.add(pl);
    }

    public void removeProgressListener(ProgressListener pl){
        progressListeners.remove(pl);
    }


}
