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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;
import static me.uits.aiphial.general.basic.Utls.distance;
/**
 * A characteristic point of the cluster, a center of cluster
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class Bof<T extends NDimPoint> extends SimpleNDimPoint
{

    //TODO remove it, cluster already contains it
    public List<T> points = new ArrayList<T>();
/**
 * @param calkBofA  - a characteristic point of the cluster, a center of cluster
 */
    public Bof(NDimPoint calkBofA)
    {
        super(calkBofA);
    }
/**
 *
 * @param calkBofA - a characteristic point of the cluster, a center of cluster
 * @param points - other points of the cluster
 */
    public Bof(NDimPoint calkBofA, List<T> points)
    {
        super(calkBofA);
        this.points = points;
    }

    Float wF = null;

    @Override
    public float getWeight()
    {
        //return points.size();

        if(wF==null)
        {
           wF = calkWeight();
        }
        return wF;
    }

    private float calkWeight()
    {


        Float[] spaceDims = Utls.getSpaceSize(points);

        BigDecimal V = BigDecimal.ONE;
        for (int i = 0; i < spaceDims.length; i++)
        {
            V = V.multiply(new BigDecimal(spaceDims[i]));
        }



        BigDecimal sumWeight = BigDecimal.ONE;
        for (NDimPoint nDimPoint : points)
        {
            sumWeight = sumWeight.add(new BigDecimal(nDimPoint.getWeight()));
        }

        BigDecimal pow = sumWeight.pow(spaceDims.length);

        BigDecimal result = pow.divide(V.add(BigDecimal.ONE), MathContext.DECIMAL64);


        float r = result.floatValue();

        if (Float.isInfinite(r) || Float.isNaN(r))
        {
            throw new RuntimeException("value "+r+" is incorrect");
        }


        return r;
    }



}
