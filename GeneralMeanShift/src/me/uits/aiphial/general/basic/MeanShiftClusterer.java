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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import me.uits.aiphial.general.dataStore.DataStore;
import me.uits.aiphial.general.dataStore.DataStoreFactory;
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;
import static me.uits.aiphial.general.basic.Utls.distance;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class MeanShiftClusterer<T extends NDimPoint> extends MeanShiftClustererOld<T> implements IMeanShiftClusterer<T>
{


    Float ws;

    

    @Override
        protected  NDimPoint calkOneShift(NDimPoint nDimPoint)
    {

        Collection<? extends  NDimPoint> withinWindow = dataStore.getWithinWindow(this.window, nDimPoint);

        Float[] averageData = new Float[nDimPoint.getDimensions()];

        for (int i = 0; i < averageData.length; i++)
        {
            averageData[i] = 0f;
        }


        Float kSum = 0f;

        for (NDimPoint nDimPoint1 : withinWindow)
        {
            final float weight = nDimPoint1.getWeight();
            Float k =ws - distance(nDimPoint, nDimPoint1);
            for (int i = 0; i < averageData.length; i++)
            {
                averageData[i] += k*nDimPoint1.getCoord(i)*weight;
            }
            kSum+=k*weight;
        }
        for (int i = 0; i < averageData.length; i++)
        {
            averageData[i] = averageData[i] / kSum;
        }


        return new SimpleNDimPoint(averageData);
    }

    

    public void setWindow(Float... window) {
        super.setWindow(window);
        this.ws = distance(SimpleNDimPoint.getZeroPoint(this.window.getDimensions()), this.window);
    }
    
}
