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

package me.uits.aiphial.general.basic;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;

/**
 * class of utility static methods
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class Utls {

    /**
     * calculates euclidian distance between points
     */
    public   static   float distance(NDimPoint shiftedPoint, NDimPoint nDimPoint)
    {

        float squaresSum = 0;

        for (int i = 0; i < shiftedPoint.getDimensions(); i++)
        {
            float q = shiftedPoint.getCoord(i) - nDimPoint.getCoord(i);
            squaresSum += q * q;
        }

        return (float) Math.sqrt(squaresSum);

    }


    static private class MinMax
    {

        Float min;
        Float max;
    }

    /**
     * calculates the dimension of the hyper-parallelepiped bounding the given points
     * @param collection of given points
     * @return dimension of the bounding hyper-parallelepiped
     */
    public static  Float[] getSpaceSize(Collection<? extends NDimPoint> l)
    {
        final NDimPoint firstpoint = l.iterator().next();
        int dim = firstpoint.getDimensions();
        MinMax[] mms = new MinMax[dim];
        for (int i = 0; i < dim; i++)
        {
            mms[i] = new MinMax();
            mms[i].max = mms[i].min = firstpoint.getCoord(i);
        }
        for (NDimPoint point : l)
        {
            for (int i = 0; i < dim; i++)
            {
                if (point.getCoord(i) > mms[i].max)
                {
                    mms[i].max = point.getCoord(i);
                }
                if (point.getCoord(i) < mms[i].min)
                {
                    mms[i].min = point.getCoord(i);
                }
            }
        }
        Float[] spaceDims = new Float[dim];
        for (int i = 0; i < dim; i++)
        {
            spaceDims[i] = mms[i].max - mms[i].min;
        }
        return spaceDims;
    }

    /**
     * @return center of mass of weighted points
     */
    public static NDimPoint getAvragePoint(Collection<? extends NDimPoint> withinWindow)
    {
        Float[] averageData = new Float[withinWindow.iterator().next().getDimensions()];
        for (int i = 0; i < averageData.length; i++)
        {
            averageData[i] = 0f;
        }
        float sumWeight = 0f;
        for (NDimPoint nDimPoint1 : withinWindow)
        {
            final float weight = nDimPoint1.getWeight();
            for (int i = 0; i < averageData.length; i++)
            {
                averageData[i] += nDimPoint1.getCoord(i) * weight;
            }
            sumWeight += weight;
        }
        for (int i = 0; i < averageData.length; i++)
        {
            averageData[i] = averageData[i] / sumWeight;
        }
        return new SimpleNDimPoint(averageData);
    }

}
