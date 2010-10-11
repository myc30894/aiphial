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
public class Point implements Cloneable{

   
    public Point(short x, short y, LUV c)
    {
        this.x = x;
        this.y = y;
        this.c = c; 
    }

    public Point()
    {
        setNuls();
    }   
        
    public short x;
    public short y;
    public LUV c;
    
    public void incrBy(Point a)
    {
        x+=a.x;
        y+=a.y;
        c.l+=a.c.l;
        c.u+=a.c.u;
        c.v+=a.c.v;
    }

    public void divide(int a)
    {
        x/=a;
        y/=a;
        c.l/=a;
        c.u/=a;
        c.v/=a;
    }

    public void setNuls()
    {
        this.x = 0;
        this.y = 0;
        if(c==null)
        {
        this.c = new LUV();
        }
        this.c.l = 0;
        this.c.u = 0;
        this.c.v = 0;
    }

    @Override
    public String toString()
    {
        return "{x="+x+" y="+y+" "+c+"}";
    }

    public Point minus(Point old)
    {
        return new Point((short)(x-old.x), (short)(y-old.y), c.minus(old.c));
    }
    
    public Point decrBy(Point old)
    {
       x-=old.x;
       y-=old.y;
                c.l-=old.c.l;
        c.u-=old.c.u;
        c.v-=old.c.v;
        
        return this;
    }

    public void set(short x, short y, float l, float u, float v)
    {
        this.x=x;
        this.y=y;
        this.c.l=l;
        this.c.u=u;
        this.c.v=v;
    }

    @Override
    public  Point clone()
    {
        try
        {
            return (Point) super.clone();
        } catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    
    
    
}
