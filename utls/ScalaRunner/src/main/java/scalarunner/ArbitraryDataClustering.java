package scalarunner;

import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.MeanShiftClusterer;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.MultiDimMapDataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;
import me.uits.aiphial.general.datagenerator.DataGenerator;


import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

public class ArbitraryDataClustering {

    public static void main(String[] args) throws java.io.IOException {

        //Field size
        final int sWidth = 200;
        final int sHeight = 300;

        //Multidimensional datastore with dimension to be 2
        final DataStore<NDimPoint> ds = new MultiDimMapDataStore<NDimPoint>(2);

        //Generating some gaussian-distributed points with dimension to be 2
        List<NDimPoint> generated = genSomePoints();

        //adding generated points to datastore
        ds.addAll(generated);

        //setup clusterer
        final MeanShiftClusterer<NDimPoint> instance = new MeanShiftClusterer<NDimPoint>();
        instance.setMinDistance(1f);
        instance.setWindow(15f, 15f);

        //setting data to clusterer
        instance.setDataStore(ds);

        //start clustering
        instance.doClustering();

        //getting results
        final List<? extends Cluster<NDimPoint>> clusters = instance.getClusters();

        System.out.println("clusters count = " + clusters.size());

        //paint results on form
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ClustersPainter(sWidth, sHeight, clusters).setVisible(true);
            }
       });

    }

    private static List<NDimPoint> genSomePoints() {
        DataGenerator dg = new DataGenerator(2);
        dg.setDeviation(10);
        dg.setMaxValue(100);
        dg.setMapCenter(new SimpleNDimPoint(50f, 50f));
        return dg.generate(4, 50);
    }

}


