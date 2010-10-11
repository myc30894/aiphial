/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickl
 */
public class LUV implements Cloneable {

    public LUV()
    {
    }

    public static LUV zeroLuv()
    {
       return new LUV(0, 0, 0);
    }

    public LUV(double l, double u, double v)
    {
        this.l = l;
        this.u = u;
        this.v = v;
    }

    @Override
    public  LUV clone() 
    {
        try
        {
            return (LUV) super.clone();
        } catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    
    
    public double l;
    public double u;
    public double v;

    @Override
    public String toString()
    {
        return "{l="+l+" u="+u+" v="+v+"}";
    }

    public LUV minus(LUV a)
    {
        return  new LUV(l-a.l, u-a.u, v-a.v);
    }

    public LUV mult(int m)
    {
        return new LUV(l*m, u*m, v*m);
    }

    public LUV div(int m)
    {
        return new LUV(l/m, u/m, v/m);
    }
    
    public LUV plus(LUV a)
    {
        return  new LUV(l+a.l, u+a.u, v+a.v);
    }

    public void incr(LUV a)
    {
        l+=a.l;
        u+=a.u;
        v+=a.v;
    }
    
}
