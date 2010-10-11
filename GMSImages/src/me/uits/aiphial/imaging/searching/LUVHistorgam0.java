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

import ru.nickl.meanShift.direct.LUV;
import me.uits.aiphial.general.basic.Cluster;
import me.uits.aiphial.imaging.LuvPoint;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class LUVHistorgam0
{

    private static final int KPerc = 100;
    public static final int L = 0;
    public static final int U = 1;
    public static final int V = 2;
    private static final int COORDCOUNT = 3;
    private static double[] maxes = new double[]
    {
        100, 100, 100
    };
    private static double[] mines = new double[]
    {
        0, -100, -100
    };
    private static final int SEGMENTSCOUNT = 100;

    public static LUVHistorgam0 build(Iterable<LuvPoint> cluster)
    {

        LUVHistorgam0 h = new LUVHistorgam0(COORDCOUNT, SEGMENTSCOUNT);

        for (LuvPoint luvPoint : cluster)
        {
            final LUV lUV = luvPoint.getLUV();
            h.addElem(L, lUV.l);
            h.addElem(U, lUV.u);
            h.addElem(V, lUV.v);
        }

        h.normalize();
        return h;
    }
    private final int[][] segments;
    private final int[] count;

    private LUVHistorgam0(int coordCount, int SEGMENTSCOUNT)
    {
        segments = new int[coordCount][SEGMENTSCOUNT];
        count = new int[coordCount];

    }

    private int getSegment(int item, double v)
    {
        v = v - mines[item];
        return (int) (v / (maxes[item] - mines[item]));
    }

    private void addElem(int item, double v)
    {
        segments[item][getSegment(item, v)]++;
        count[item]++;
    }

    private void normalize()
    {
        for (int item = 0; item < segments.length; item++)
        {
            for (int segm = 0; segm < segments[item].length; segm++)
            {
                segments[item][segm] = (KPerc * segments[item][segm]) / count[item];
            }
        }
    }

    public double distcompare(LUVHistorgam0 a)
    {
        if (this.segments.length != a.segments.length)
        {
            throw new IllegalArgumentException("historgrams have different dimentions");
        }

        LUVHistorgam0 diffh = new LUVHistorgam0(COORDCOUNT, SEGMENTSCOUNT);

        for (int item = 0; item < this.segments.length; item++)
        {
            for (int segm = 0; segm < segments[item].length; segm++)
            {
                diffh.segments[item][segm] = Math.abs(this.segments[item][segm] - a.segments[item][segm]);
            }
        }

        double diff = 0.0;


        for (int segm = 0; segm < diffh.segments[0].length; segm++)
        {
            double cdiff = 0;

            for (int item = 0; item < diffh.segments.length; item++)
            {
                final int d = diffh.segments[item][segm];
                cdiff+=  d*d;
            }

            diff+=Math.sqrt(cdiff);

        }

        return diff;

    }
}
