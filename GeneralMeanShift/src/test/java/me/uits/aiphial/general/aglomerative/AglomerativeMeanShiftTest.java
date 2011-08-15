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

import me.uits.aiphial.general.aglomerative.IterationListener;
import me.uits.aiphial.general.basic.*;
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift;
import me.uits.aiphial.general.basic.SimpleBandwidthSelector;
import me.uits.aiphial.general.basic.MeanShiftClusterer;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.IMeanShiftClusterer;
import java.util.Collection;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.MultiDimMapDataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.datagenerator.DataGenerator;
import static org.junit.Assert.*;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */

public class AglomerativeMeanShiftTest extends AbstractMeanShiftClustererTest
{

    public AglomerativeMeanShiftTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    
    private  void testGenerared()
    {
        final int dim = 3;
        final int clusterscount = 3;
        final int deviation = 100;
        final int pointsCount = 2000;

        DataGenerator dg = new DataGenerator(dim);

        dg.setDeviation(deviation);
        dg.setMaxValue(300);


        List<NDimPoint> generated = dg.generate(clusterscount, pointsCount);

        DataStore<NDimPoint> ds = new MultiDimMapDataStore<NDimPoint>(dim);
        ds.addAll(generated);

        /*
        Float[] bandwidth = new Float[dim];

        final double pow = Math.pow(Math.pow(dg.getMaxValue(), dim) / pointsCount, 1.0F / dim);

        for (int i = 0; i < bandwidth.length; i++)
        {
        bandwidth[i] =(float)pow; //(float) deviation * 3;
        }
         */


         Float[] bandwidth = new SimpleBandwidthSelector().getBandwidth(ds);

        Float[] bandwidth0 = new Float[]
        {
            5F, 5F, 5F
        };//

        ds.setOptimalWindow(bandwidth0);

        System.out.println();

        AglomerativeMeanShift<NDimPoint> instance = new AglomerativeMeanShift<NDimPoint> (new MeanShiftClusterer());
        instance.setDataStore(ds);



        final long startime = System.currentTimeMillis();

        instance.addIterationListener(new IterationListener<NDimPoint>()
        {

            public void IterationDone(Collection<? extends Cluster<NDimPoint>> clusters)
            {
                System.out.println(System.currentTimeMillis()-startime+" cc=" + clusters.size());
            }
        });



        instance.doClustering();

        System.out.println("clusters=" + instance.getClusters().size());

        assertTrue(instance.getClusters().size() == clusterscount);

    }

    @Override
    protected <T extends NDimPoint> IMeanShiftClusterer<T> createInstance()
    {
        return new AglomerativeMeanShift<T>(new MeanShiftClusterer());
    }

//    @Override
//    protected IMeanShiftClusterer createInstance() {
//       return new AglomerativeMeanShift(new MeanShiftClusterer());
//    }
}