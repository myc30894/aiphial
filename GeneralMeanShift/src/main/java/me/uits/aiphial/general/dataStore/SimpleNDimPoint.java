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

package me.uits.aiphial.general.dataStore;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class SimpleNDimPoint implements NDimPoint {

    private Float[] data;

    protected float weight = 1f;

    public static SimpleNDimPoint getZeroPoint(int dim)
    {
        Float[] data = new Float[dim];
        for (int i = 0; i < data.length; i++)
        {
            data[i] = 0f;
        }

        return new SimpleNDimPoint(data);

    }

    public SimpleNDimPoint(Float... data)
    {
        this.data = data;
    }

    public SimpleNDimPoint(SimpleNDimPoint a)
    {
        this.data = a.data;
    }

    public SimpleNDimPoint(NDimPoint a)
    {
        data = new Float[a.getDimensions()];
        for (int i = 0; i < a.getDimensions(); i++)
        {
            data[i] = a.getCoord(i);
        }
    }

    public Float getCoord(int i)
    {
        return data[i];
    }

    public void setCoord(int i, Float v)
    {
        data[i] = v;
    }


    public int getDimensions()
    {
        return data.length;
    }

    @Override
    public String toString()
    {

        StringBuilder sb = new StringBuilder();
        
        for (Float float1 : data)
        {
            sb.append(float1);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length()-1);

        return sb.toString();

    }

    public Float[] getFloatData()
    {
        return data;
    }

    public float getWeight()
    {
        return weight;
    }




}
