package scalarunner;

import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift;
import me.uits.aiphial.general.aglomerative.IterationListener;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.MeanShiftClusterer;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.MultiDimMapDataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;
import me.uits.aiphial.general.datagenerator.DataGenerator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ArbitraryDataClusteringAgglo {

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
        final MeanShiftClusterer<NDimPoint> meanShiftClusterer = new MeanShiftClusterer<NDimPoint>();
        meanShiftClusterer.setMinDistance(1f);
        meanShiftClusterer.setWindow(10f, 10f);

        //creating agglomerative clusterer
        AglomerativeMeanShift<NDimPoint> instance = new AglomerativeMeanShift<NDimPoint> (meanShiftClusterer);
        instance.setMaxIterations(1000);
        instance.setWindowMultiplier(0.2f);
        // an iteration listener that would increment window multiplier on each step
        // to provide additional agglomeretivity :)
        instance.setWindowMultiplierStep(0.1f);

        //setting data to clusterer
        instance.setDataStore(ds);

        //painting each step result to file
        final AtomicInteger i = new AtomicInteger(0);
        instance.addIterationListener(new IterationListener<NDimPoint>()
        {
            public void IterationDone(final List<? extends Cluster<NDimPoint>> clusters)
            {
                String filename = "clusters" + (i.getAndIncrement()) + ".png";
                System.out.println("clusters count = " + clusters.size()+" writing to "+filename);
                paintClusters(clusters, filename, sWidth, sHeight);
            }
        });

        //start clustering
        instance.doClustering();

    }

    private static List<NDimPoint> genSomePoints() {
        DataGenerator dg = new DataGenerator(2);
        dg.setDeviation(10);
        dg.setMaxValue(100);
        dg.setMapCenter(new SimpleNDimPoint(50f, 50f));
        return dg.generate(4, 50);
    }

    private static void paintClusters(List<? extends Cluster<NDimPoint>> clusters, String filename, int sWidth, int sHeight) {
        BufferedImage img = new BufferedImage(sWidth, sHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = img.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
        ClustersPainter.drawClusters(clusters, graphics);
        graphics.dispose();
        try {
            ImageIO.write(img, "png", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }


}
