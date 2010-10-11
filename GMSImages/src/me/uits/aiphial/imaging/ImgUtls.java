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

package me.uits.aiphial.imaging;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LUVConverter;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.general.dataStore.NDimPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class ImgUtls
{

    public static LUV[][] ImageToLuvDArray(BufferedImage orig) {
        return new LUVConverter().toLUVDArray(orig);
    }
    
    public static Collection<Region> asRegions(Collection<Cluster<LuvPoint>> clusters){

        ArrayList<Region> result = new ArrayList<Region>(clusters.size());

        for (Cluster<LuvPoint> cluster : clusters) {
            result.add(new Region(cluster));
        }

        return result;
    }

    public static Rectangle getBoundingRect(Collection<LuvPoint> cluster)
    {
        int xmin;
        int ymin;
        int xmax;
        int ymax;
        Iterator<LuvPoint> iterator = cluster.iterator();
        final LuvPoint firstPoint = iterator.next();
        xmax = xmin = firstPoint.getX();
        ymax = ymin = firstPoint.getY();
        while (iterator.hasNext())
        {
            LuvPoint luvPoint = iterator.next();
            if (luvPoint.getX() < xmin)
            {
                xmin = luvPoint.getX();
            }
            if (luvPoint.getX() > xmax)
            {
                xmax = luvPoint.getX();
            }
            if (luvPoint.getY() < ymin)
            {
                ymin = luvPoint.getY();
            }
            if (luvPoint.getY() > ymax)
            {
                ymax = luvPoint.getY();
            }
        }

        return new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    public static BufferedImage getBoundingImage(BufferedImage originalImg, Region cluster)
    {
        Rectangle boundingRect = getBoundingRect(cluster);

        if (boundingRect.width <= 0 || boundingRect.height <= 0)
        {
            return null;
        }

        return originalImg.getSubimage(boundingRect.x, boundingRect.y, boundingRect.width, boundingRect.height);

    }

    public static BufferedImage getClusterImage(BufferedImage originalImg, Region cluster)
    {
        Rectangle boundingRect = getBoundingRect(cluster);

        if (boundingRect.width <= 0 || boundingRect.height <= 0)
        {
            return null;
        }

        BufferedImage result = new BufferedImage(boundingRect.width + 1, boundingRect.height + 1, BufferedImage.TYPE_INT_ARGB);

        for (LuvPoint luvPoint : cluster)
        {

            result.setRGB(luvPoint.getX() - boundingRect.x, luvPoint.getY() - boundingRect.y,
                    originalImg.getRGB(luvPoint.getX(), luvPoint.getY()));

        }

        return result;

    }

    public static Collection<LuvPoint> luvDArraytoLuvPoints(LUV[][] darray)
    {
        int height = darray.length;
        int width = darray[0].length;

        List<LuvPoint> result = new ArrayList<LuvPoint>(height * width / 2);

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {

                LUV p = darray[y][x];
                if (p != null)
                {
                    result.add(new LuvPoint(x, y, p));
                }


            }

        }

        return result;
    }

    public static Region asRegion(Collection<LuvPoint> points)
    {
        if(points instanceof Region) return (Region) points;

        int sumx = 0;
        int sumy = 0;
        double suml = 0;
        double sumu = 0;
        double sumv = 0;

        for (LuvPoint p : points)
        {

            sumx += p.getX();
            sumy += p.getY();
            LUV luv = p.getLUV();
            suml += luv.l;
            sumv += luv.v;
            sumu += luv.u;
        }
        int length = points.size();

        return new Region(
                new LuvPoint(sumx / length, sumy / length,
                new LUV(suml / length, sumu / length, sumv / length)),
                points);

    }

    static BufferedImage paintRegions(int width, int height, Collection<Region> clusters)
    {

        LUV[][] array = new LUV[height][width];

        for (Region cluster : clusters)
        {
            NDimPoint cl = cluster.getBasinOfAttraction();
            LUV cp = new LUV(cl.getCoord(2), cl.getCoord(3), cl.getCoord(4));

            for (LuvPoint lp : cluster)
            {
                array[lp.getY()][lp.getX()] = cp;
            }

        }

        return new LUVConverter().LUVArrayToBufferedImage(array);

//    array = [[None] * width for i in range(height)]
//
//    for cluster in clusters:
//        cl = cluster.getBasinOfAttraction()
//        cp = LUV(cl.getCoord(2),cl.getCoord(3),cl.getCoord(4))
//        for lp in cluster:
//            array[lp.y][lp.x] = cp
//
//
//    resultImage = LUVConverter().LUVArrayToBufferedImage(array)
    }

    public static Collection<LuvPoint> readImageAsLUVCollection(final String filename) throws IOException
    {
        return ImgUtls.luvDArraytoLuvPoints(readImageAsLuvArray(filename));
    }

    public static Collection<LuvPoint> imageAsLUVPointCollection(final BufferedImage orig) throws IOException
    {
        return ImgUtls.luvDArraytoLuvPoints(ImageToLuvDArray(orig));
    }

    public static LUV[][] readImageAsLuvArray(final String filename) throws IOException
    {
        BufferedImage orig = ImageIO.read(new File(filename));
        return  ImageToLuvDArray(orig);
    }

}
