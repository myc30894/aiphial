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

/*
 * testFrame.java
 *
 * Created on 21.08.2009, 15:11:21
 */
package MyImage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LUVConverter;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.basic.MeanShiftClusterer;
import me.uits.aiphial.general.basic.SimpleBandwidthSelector;
import me.uits.aiphial.imaging.LuvDataStore;
import me.uits.aiphial.imaging.LuvPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class testFrame extends javax.swing.JFrame
{

    /** Creates new form testFrame */
    public testFrame()
    {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        clusterPanel1 = new MyImage.ClusterPanelDebug();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        clusterPanel1.setName("clusterPanel1"); // NOI18N

        javax.swing.GroupLayout clusterPanel1Layout = new javax.swing.GroupLayout(clusterPanel1);
        clusterPanel1.setLayout(clusterPanel1Layout);
        clusterPanel1Layout.setHorizontalGroup(
            clusterPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        clusterPanel1Layout.setVerticalGroup(
            clusterPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clusterPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(clusterPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        try
        {
            final BufferedImage image = ImageIO.read(new File("../../images/DSC00104s400.bmp"));
            //final BufferedImage image = ImageIO.read(new File("../images/DSCN4909s100.bmp"));
            LUV[][] LUVArray = new LUVConverter().toLUVDArray(image);
            LuvDataStore dataStore = new LuvDataStore(LUVArray);
            Float[] bandwidth = new SimpleBandwidthSelector().getBandwidth(dataStore);
            MeanShiftClusterer<LuvPoint> clusterer = new MeanShiftClusterer<LuvPoint>();
            clusterer.setDataStore(dataStore);
            clusterer.setWindow(bandwidth);
            clusterer.doClustering();
            System.out.println("clusteringDone");
            final List<Cluster<LuvPoint>> clusters = clusterer.getClusters();
            java.awt.EventQueue.invokeLater(new Runnable()
            {

                public void run()
                {
                    final testFrame testFrame = new testFrame();
                    testFrame.clusterPanel1.setImage(image);
                    testFrame.clusterPanel1.setClusters(clusters);
                    //testFrame.clusterPanel1.repaint();
                    testFrame.setVisible(true);
                }
            });
        } catch (IOException ex)
        {
            Logger.getLogger(testFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private MyImage.ClusterPanelDebug clusterPanel1;
    // End of variables declaration//GEN-END:variables
}
