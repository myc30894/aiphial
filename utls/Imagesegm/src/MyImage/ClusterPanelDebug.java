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
 * ClusterPanel.java
 *
 * Created on 21.08.2009, 13:27:02
 */
package MyImage;

import MyImage.utls.ClustersMap;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.imaging.LuvPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class ClusterPanelDebug extends PicturePanel
{

    public static ClusterPanelDebug clusterPanel;
    int x;
    int y;
    Thread datathread;

    public ClusterPanelDebug()
    {
        clusterPanel = this;

        initComponents();

        final ClusterPanelDebug panel = this;

        this.addMouseListener(new MouseAdapter()
        {

            @Override
            public void mouseReleased(final MouseEvent e)
            {
                if (e.getButton() == MouseEvent.BUTTON1)
                {
                    Runnable runnable = new Runnable()
                    {

                        public void run()
                        {
                            BufferedImage i = getImage();

                            x = e.getX() * i.getWidth() / getWidth();
                            y = e.getY() * i.getHeight() / getHeight();

//                            Collection<LuvPoint> orderedBoundary = clustersMap.getOrderedBoundary(clustersMap.getAt(x, y));
//
//                            ClusterPanelDebug.this.drawCoutour(orderedBoundary, 0);

                            Collection<Collection<LuvPoint>> orderedBoundary = clustersMap.getOrderedBoundaries(clustersMap.getAt(x, y));

                            ClusterPanelDebug.this.drawCoutours(orderedBoundary, 0);

                        }
                    };

                    if (datathread != null && datathread.isAlive())
                    {
                        datathread.stop();
                        System.err.println("Thread was stopped");
                    }

                    datathread = new Thread(runnable, "BoundaryPainer");
                    //bp.setDaemon(true);
                    datathread.start();

                } else
                {
                    ClusterPanelDebug.this.ncont();
                }

                panel.repaint();

            }
        });
    }

    private void drawpoint(Graphics g, LuvPoint point)
    {
        int w = getWidth() / getImage().getWidth();
        int h = getHeight() / getImage().getHeight();

        if (w < 2)
        {
            w = 2;
        }
        if (h < 2)
        {
            h = 2;
        }
        g.fillOval(corectW(point.getX()), corectH(point.getY()), w, h);
    }

    private synchronized void ncont()
    {
        this.notify();
    }
    private List<Cluster<LuvPoint>> clusters;
    private ClustersMap clustersMap;

    private int corectW(float w)
    {
        return (int) (w * getWidth() / getImage().getWidth());
    }

    private int corectH(float h)
    {
        return (int) (h * getHeight() / getImage().getHeight());
    }
    private Collection<Collection<LuvPoint>> countours;

    public synchronized void drawCoutour(Collection<LuvPoint> countour, int time)
    {
        ArrayList<Collection<LuvPoint>> al = new ArrayList<Collection<LuvPoint>>(1);
        al.add(countour);
        drawCoutours(al, time);
    }

    public synchronized void drawCoutours(Collection<Collection<LuvPoint>> countour, int time)
    {
        this.countours = countour;

        SwingUtilities.invokeLater(new Runnable()
        {

            public void run()
            {
                ClusterPanelDebug.this.repaint();
            }
        });


        try
        {
            Thread.sleep(time);
            //this.wait();
        } catch (InterruptedException ex)
        {
            throw new RuntimeException(ex);
        }

    }

    public void paintClusters(Graphics g)
    {

        Cluster<LuvPoint> cluster = clustersMap.getAt(x, y);


        Color oldc = g.getColor();
        g.setColor(Color.CYAN);
        for (LuvPoint point : cluster)
        {
            drawpoint(g, point);
        }




        if (countours != null)
        {
            //for multithreading
            Collection<Collection<LuvPoint>> countourscopy = new ArrayList<Collection<LuvPoint>>(countours);
            for (Collection<LuvPoint> countour : countourscopy)
            {
                Polygon polygon = new Polygon();
                LuvPoint last = null;

                g.setColor(Color.LIGHT_GRAY);

                //for multithreading
                countour = new ArrayList<LuvPoint>(countour);

                for (LuvPoint point : countour)
                {

                    drawpoint(g, point);

                    polygon.addPoint(corectW(point.getX()), corectH(point.getY()));
                    last = point;
                }


                g.setColor(Color.ORANGE);

                drawpoint(g, last);
                //g.fillOval(corectW(last.getX()), corectH(last.getY()), getWidth() / getImage().getWidth(), getHeight() / getImage().getHeight());

                g.setColor(oldc);
                g.drawPolygon(polygon);
            }
        }

        g.setColor(oldc);

        // polygon.invalidate();

        //g.drawPolygon(polygon);

    }

    @Override
    public void paint(Graphics g)
    {
        //super.paint(g);
        //Graphics2D g2d = (Graphics2D)g;
        Color old = g.getColor();
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(old);

        paintClusters(g);
        // Рисуем подкомпоненты.
        super.paintChildren(g);
        // Рисуем рамку
        super.paintBorder(g);

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @return the clusters
     */
    public List<Cluster<LuvPoint>> getClusters()
    {
        return clusters;
    }

    /**
     * @param clusters the clusters to set
     */
    public void setClusters(final List<Cluster<LuvPoint>> clusters)
    {


        clustersMap = new ClustersMap(getImage().getWidth(), getImage().getHeight());
        ClusterPanelDebug.this.clusters = clusters;
        clustersMap.buildMap(clusters);

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
