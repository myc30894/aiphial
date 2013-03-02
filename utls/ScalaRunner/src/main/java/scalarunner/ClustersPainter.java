package scalarunner;

import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.dataStore.NDimPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class ClustersPainter extends JFrame {

    private int sWidth;
    private int sHeight;

    private List<? extends Cluster<? extends NDimPoint>> clusters;

    ClustersPainter(int sWidth, int sHeight, List<? extends Cluster<NDimPoint>> clusters) throws HeadlessException {

        super("Painter");
        this.clusters = clusters;
        this.sWidth = sWidth;
        this.sHeight = sHeight;

        int fs = 8;

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(dim.width / fs, dim.height / fs, dim.width * (fs - 2) / fs, dim.height * (fs - 2) / fs);

        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);

        Graphics2D td = (Graphics2D) g2.create(10, 30, getWidth() - 10, getHeight() - 10);

        td.scale(getWidth() / sWidth, getHeight() / sHeight);

        drawClusters(clusters, td);

    }

    public static void drawClusters(List<? extends Cluster<? extends NDimPoint>> clusters, Graphics2D td) {

        int rad = 4;

        List<Color> colors = getDistinctColors(clusters.size());

        for (int i = 0; i < clusters.size(); i++) {

            Cluster<? extends NDimPoint> cluster = clusters.get(i);
            Color color = colors.get(i);
            NDimPoint center = cluster.getBasinOfAttraction();
            for (NDimPoint nDimPoint : cluster) {
                td.setColor(color);

                Shape l = new Line2D.Double(
                        nDimPoint.getCoord(0), nDimPoint.getCoord(1),
                        center.getCoord(0), center.getCoord(1)
                );

                Shape l2 = new Ellipse2D.Double(
                        nDimPoint.getCoord(0) - rad / 2, nDimPoint.getCoord(1) - rad / 2,
                        rad, rad
                );
                td.draw(l2);
                td.draw(l);

            }

        }
    }

    private static List<Color> getDistinctColors(int cn) {

        int sccount = (int) Math.ceil(Math.sqrt(cn));

        float step = 1f / sccount;

        ArrayList<Color> colors = new ArrayList<Color>(cn);

        for (int s = 0; s < sccount; s++) {
            for (int h = 0; h < sccount; h++) {
                colors.add(new Color(Color.HSBtoRGB(step * h, 1f, 1f - 0.6f * step * s)));
            }
        }

        return colors;
    }

}
