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

import java.lang.Float;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import static me.uits.aiphial.general.basic.Utls.getSpaceSize;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class SimpleBandwidthSelector
{


    public Float[] getBandwidth(DataStore<? extends NDimPoint> ds)
    {
        List<? extends NDimPoint> pointsList = ds.asList();
        int dim = pointsList.get(0).getDimensions();

        Float[] spaceDims = getSpaceSize(pointsList);


        Float[] dimK = new Float[dim];

        for (int i = 0; i < dim; i++)
        {
            dimK[i] = spaceDims[i] / spaceDims[0];
        }


        Float V = 1F;
        for (int i = 0; i < spaceDims.length; i++)
        {
            V *= spaceDims[i];
        }

        Float abcde = 1F;

        for (int i = 0; i < dim; i++)
        {
            abcde *= dimK[i];
        }

        Float xside = (float) Math.pow(V / (abcde * pointsList.size()), 1F / dim);


        Float[] result = new Float[dim];

        for (int i = 0; i < dim; i++)
        {
            result[i] = xside * dimK[i];
        }


        return result;

    }

    
    /*
    public Float[] getBandwidth(DataStore<? extends NDimPoint> ds)
    {
    List<? extends NDimPoint> points = ds.asList();

    Float[][] arrays= new Float[points.get(0).size()][points.size()];

    int j=0;
    for (NDimPoint nDimPoint : points)
    {

    for (int i = 0; i < nDimPoint.size(); i++)
    {
    arrays[i][j] = nDimPoint.get(i);
    }

    j++;
    }


    Float[] result = new Float[points.get(0).size()];

    for (int i = 0; i < result.length; i++)
    {
    result[i] = estimate(arrays[i])*points.get(0).size()*40;
    }

    return result;
    }

    private Float estimate(Float[] line)
    {

    Arrays.sort(line);

    Float sum = 0f;

    for (int i = 0; i < line.length-1; i++)
    {
    sum+=line[i+1]-line[i];
    }

    return (sum/line.length);

    }
     */
}
