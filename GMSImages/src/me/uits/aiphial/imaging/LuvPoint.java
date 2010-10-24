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

import ru.nickl.meanShift.direct.LUV;
import me.uits.aiphial.general.dataStore.NDimPoint;

/**
 * LUV-color with x,y-coordinates
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class LuvPoint implements NDimPoint
{

    private LUV c = new LUV();
    private int x;
    private int y;
    private Float[] coords;

    public LuvPoint(int x, int y, LUV c)
    {
        this.c = c;
        this.x = x;
        this.y = y;

        coords = new Float[]
                {
                    new Float(this.x), new Float(this.y), new Float(this.c.l), new Float(this.c.u), new Float(this.c.v)
                };


    }

    public LuvPoint()
    {
        coords = new Float[getDimensions()];
    }

    public LuvPoint(Float... array)
    {
        this();
        for (int i = 0; i < array.length; i++)
        {
            setCoord(i, array[i]);
        }

    }

    public LuvPoint(NDimPoint window)
    {
        this();
        for (int i = 0; i < window.getDimensions(); i++)
        {
            setCoord(i, window.getCoord(i));
        }

    }

    public Float getCoord(int i)
    {
        return coords[i];

    }

    public void setCoord(int i, Float v)
    {
        coords[i] = v;
        switch (i)
        {
            case 0:
                setX(v.intValue());
                break;
            case 1:
                setY(v.intValue());
                break;
            case 2:
                c.l = v;
                break;
            case 3:
                c.u = v;
                break;
            case 4:
                c.v = v;
                break;
            default:
                throw new IndexOutOfBoundsException("no such coord:" + i);
        }

    }

    public int getDimensions()
    {
        return 5;
    }

    public float getWeight()
    {
        return 1;
    }

    /**
     * @return the c
     */
    public LUV getLUV()
    {
        return c;
    }

    public void selLUV(LUV c)
    {
        this.c = c;
        coords[2] =new Float(c.l);
        coords[3] =new Float(c.u);
        coords[4] =new Float(c.v);
    }

    /**
     * @return the x
     */
    public int getX()
    {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x)
    {
        this.x = x;
        this.coords[0] = new Float(x);
    }

    /**
     * @return the y
     */
    public int getY()
    {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y)
    {
        this.y = y;
        this.coords[1] = new Float(y);
    }

    @Override
    public String toString()
    {
        return "x:" + x + " y:" + y + " luv:" + this.c;
    }
}
