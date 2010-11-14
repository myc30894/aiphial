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

import java.math.MathContext;
import java.lang.Float;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.NDimPoint;
import static me.uits.aiphial.general.basic.Utls.getSpaceSize;

/**
 * Class that automatically selects a bandwidth(window)
 * for mean-shift clustering from data
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class SimpleBandwidthSelector
{


    /**
     * automatically selects a bandwidth(window)
     * for mean-shift clustering from data
     * @param ds - the source data
     * @return n-dimensional point, a bandwidth(window)
     */
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


        BigDecimal V = BigDecimal.ONE;
        for (int i = 0; i < spaceDims.length; i++)
        {
            V = V.multiply(new BigDecimal(spaceDims[i]));
        }

        Float abcde = 1F;

        for (int i = 0; i < dim; i++)
        {
            abcde *= dimK[i];
        }

        Float xside = (float) Math.pow(V.divide(new BigDecimal(abcde * pointsList.size()),MathContext.DECIMAL64).doubleValue(), 1F / dim);


        Float[] result = new Float[dim];

        for (int i = 0; i < dim; i++)
        {
            result[i] = xside * dimK[i];
        }


        return result;

    }

}
