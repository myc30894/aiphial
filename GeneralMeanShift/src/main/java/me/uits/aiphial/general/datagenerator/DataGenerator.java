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

package me.uits.aiphial.general.datagenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import me.uits.aiphial.general.dataStore.NDimPoint;
import me.uits.aiphial.general.dataStore.SimpleNDimPoint;

/**
 * N-dim points data generator based on multiple Gaussian distribution.
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class DataGenerator {
    private int deviation = 40;
    
    private int dim = 5;
    private int maxValue = 100;
    
    private Random r = new Random();
    
    public  List<NDimPoint> generate(int centers, int totalPoints)
    {
        
        List<NDimPoint> result = new ArrayList<NDimPoint>(totalPoints);
        
        int pointsPerCenter = totalPoints/centers;
        
        for (int i = 0; i < centers; i++)
        {            
            NDimPoint center = genCenter();
            result.add(center);

            for (int j = 0; j < pointsPerCenter; j++)
            {

                result.add(genPoint(center));

            }
        }

        return result;
    }

    private NDimPoint genPoint(NDimPoint center)
    {

        Float[] data = new Float[dim];

        for (int i = 0; i < dim; i++)
        {
            data[i] =(float)(center.getCoord(i)+r.nextGaussian()*deviation);
        }

        return new SimpleNDimPoint(data);
    }


    private NDimPoint genCenter()
    {
        Float[] data = new Float[dim];

        for (int i = 0; i < data.length; i++)
        {
            data[i] =(float)(r.nextDouble()*maxValue);
        }

        return new SimpleNDimPoint(data);
    }

    public DataGenerator()
    {
    }

    public DataGenerator(int dim)
    {
        this.dim = dim;
    }

    public int getDim()
    {
        return dim;
    }

    public void setDim(int dim)
    {
        this.dim = dim;
    }

    public int getDeviation()
    {
        return deviation;
    }

    public void setDeviation(int deviation)
    {
        this.deviation = deviation;
    }

    public void setMaxValue(int maxValue)
    {
        this.maxValue = maxValue;
    }

    public int getMaxValue()
    {
        return maxValue;
    }

    
    

}
