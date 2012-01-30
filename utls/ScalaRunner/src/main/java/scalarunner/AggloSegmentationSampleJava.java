/*
 *
 * This file is part of Aiphial.
 *
 * Copyright (c) 2012 Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
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
package scalarunner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.imageio.ImageIO;
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift;
import me.uits.aiphial.general.aglomerative.IterationListener;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.MeanShiftClusterer;
import me.uits.aiphial.general.dataStore.KdTreeDataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.imaging.ImgUtls;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.imaging.Tools;

/**
 *
 * @author nickl
 */
public class AggloSegmentationSampleJava
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws java.io.IOException
    {
        // read a buffered image from file
        final BufferedImage srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"));
        // then create a mean-shift clusterer which would be used
        // at each step of agglomareative clustering
        MeanShiftClusterer<NDimPoint> msc0 = new MeanShiftClusterer<NDimPoint>();

        msc0.setMinDistance(3);
        // create an agglomerative clusterer which is based on created mean-shift clusterer
        AglomerativeMeanShift<LuvPoint> amsc = new AglomerativeMeanShift<LuvPoint>(msc0);

        amsc.setAutostopping(false);
        amsc.setMaxIterations(1000);
        amsc.setWindowMultiplier(0.2f);
        // an iteration listener that would increment window multiplier on each step
        // to provide additional agglomeretivity :)
        amsc.setWindowMultiplierStep(0.1f);
        
        // create a datastore and add all points from image to it
        KdTreeDataStore<LuvPoint> datastore = new KdTreeDataStore<LuvPoint>(5);

        for (LuvPoint p : ImgUtls.imageAsLUVPointCollection(srcimg))
        {
            datastore.add(p);
        }

        // set created datasrote as data source for agglomerative clusterer
        amsc.setDataStore(datastore);


        // add an iteration listener that would write to image file
        // results of each step of the agglomerative clustering
        amsc.addIterationListener(new IterationListener<LuvPoint>()
        {            
            int s = 0;
            
            public void IterationDone(Collection<? extends Cluster<LuvPoint>> a)
            {
                try
                {
                    ImageIO.write(
                            Tools.paintClusters(srcimg.getWidth(), srcimg.getHeight(), a, false),
                            "bmp",
                            new File("../out_" + s + ".bmp"));
                    s = s + 1;
                } catch (IOException iOException)
                {
                    throw new RuntimeException(iOException);
                }
            }
        });

        // start the clustering process
        amsc.doClustering();

    }
}
