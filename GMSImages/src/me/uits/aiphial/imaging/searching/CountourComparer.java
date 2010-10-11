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

package me.uits.aiphial.imaging.searching;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.imaging.ClustersMap;
import me.uits.aiphial.imaging.ImgUtls;
import me.uits.aiphial.imaging.LuvPoint;
import me.uits.aiphial.imaging.Region;

/**
 * @deprecated does not really work
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
// TODO: decide remove it or comlete it
@Deprecated
public class CountourComparer implements RegionComparer
{

    private double[] pattern;

    private double[] getDescriprors(Region cluster)
    {
        Rectangle r = ImgUtls.getBoundingRect(cluster);
        ClustersMap clustersMap = new ClustersMap(r.x+r.width+1, r.y+r.height+1);
        clustersMap.buildMap(Arrays.asList(cluster));
        Collection<LuvPoint> boundary = clustersMap.get8Boundary(cluster);

        return getCountourDescriprors(boundary);


    }

    private double[] getCountourDescriprors(Collection<LuvPoint> countour)
    {
        int N = 50;

        double[] desriptors = new double[N / 2];
        Collection<LuvPoint> points = countour;
        double cx = 0, cy = 0;

        for (LuvPoint p : points)
        {
            cx += p.getX();
            cy += p.getY();
        }

        cx = cx / points.size();
        cy = cy / points.size();
        double[] unnr = new double[points.size()];

        int k = 0;
        for (LuvPoint luvPoint : points)
        {
            unnr[k++] = Math.sqrt(Math.pow(luvPoint.getX() - cx, 2) + Math.pow(luvPoint.getY() - cy, 2));

        }



        Arrays.sort(unnr);

        float norm = (float) (unnr.length - 1) / (float) (N - 1);

        double[] r = new double[N];

        for (int i = 0; i < N; i++)
        {
            r[i] = unnr[(int) (i * norm + 0.5)];
        }

        double[] C = new double[N / 2];

        for (int n = 0; n < N / 2; n++)
        {
            double An = 0, Bn = 0;

            for (int i = 0; i < N; i++)
            {
                An += r[i] * Math.cos(2 * Math.PI * n * i / N);
                Bn += r[i] * Math.sin(2 * Math.PI * n * i / N);
            }

            An = An / N;
            Bn = Bn / N;

            C[n] = Math.sqrt(An * An + Bn * Bn);
        }

        for (int n = 0; n < N / 2; n++)
        {
            desriptors[n] = C[n] / C[0];
        }



        return desriptors;
    }

    public double compareCluster(Region cluster)
    {

        double sumkv = 0;

        double[] descriprors = getDescriprors(cluster);

        System.out.print("descriprors:[");


        for (int i = 0; i < descriprors.length; i++)
        {
            final double di = pattern[i] - descriprors[i];

            sumkv+=di*di;

            System.out.print(di);
            System.out.print(',');
        }
       

        System.out.println("]");

         System.out.println(sumkv);

        return 50;
    }

    public void setPattern(Region pattern)
    {
        this.pattern = getDescriprors(pattern);

//        System.out.print("pattern:[");
//
//        for (double d : this.pattern)
//        {
//            System.out.print(d);
//            System.out.print(',');
//        }
//
//        System.out.println("]");


    }
}
