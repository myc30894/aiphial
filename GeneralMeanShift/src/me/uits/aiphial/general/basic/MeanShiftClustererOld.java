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
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class MeanShiftClustererOld<T extends NDimPoint> implements IMeanShiftClusterer<T>
{

    protected  DataStore<? extends T> dataStore;
    protected  float minDistance = 3.0F;
    protected  DataStore<Bof<T>> resultStore;
    protected DataStoreFactory dataStoreFactory = DefaultDataStoreFactory.get();

    protected List<Cluster<T>> result;
    protected SimpleNDimPoint window;


    
    public void setDataStore(DataStore<? extends T> dataStore)
    {
        this.dataStore = dataStore;
    }

    public void doClustering()
    {

        //resultStore = new MultiDimMapDataStore(dataStore.getDim());
        resultStore = dataStoreFactory.createDataStore(dataStore.getDim());


        //resultStore.setWindow(window);



        for (T nDimPoint : dataStore)
        {
            NDimPoint calkBofA = calkBofA(nDimPoint);
                        
            Bof<T> bof = resultStore.addOrGet(new Bof<T>(calkBofA));
            bof.points.add(nDimPoint);

        }

        performClusters();

    }


    protected  void performClusters()
    {             

        result = new ArrayList<Cluster<T>>();

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
        }

    }


    public List<Cluster<T>> getClusters()
    {
        return Collections.unmodifiableList(result);

    }


    protected  NDimPoint calkBofA(NDimPoint nDimPoint)
    {

        int i = 100;


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
        Collection<? extends  NDimPoint> withinWindow = dataStore.getWithinWindow(this.window,nDimPoint);

        return Utls.getAvragePoint(withinWindow);
    }
    

    /**
     * @return the minDistance
     */
    public float getMinDistance()
    {
        return minDistance;
    }

    /**
     * @param minDistance the minDistance to set
     */
    public void setMinDistance(float minDistance)
    {
        this.minDistance = minDistance;
    }

    

    public void setWindow(Float... window) {
        this.window = new SimpleNDimPoint(window);
    }

    /**
     * @return the dataStoreFactory
     */
    public DataStoreFactory getDataStoreFactory()
    {
        return dataStoreFactory;
    }

    /**
     * @param dataStoreFactory the dataStoreFactory to set
     */
    public void setDataStoreFactory(DataStoreFactory dataStoreFactory)
    {
        this.dataStoreFactory = dataStoreFactory;
    }
}
