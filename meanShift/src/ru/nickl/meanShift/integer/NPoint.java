/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.integer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickl
 */
public class NPoint implements Cloneable {

   
    public NPoint(short x, short y, NLUV c)
    {
        this.x = x;
        this.y = y;
        this.c = c; 
    }

    public NPoint()
    {        
    }   
        
    short x;
    short y;
    NLUV c;
    
    public void incrBy(NPoint a)
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

    @Override
    public String toString()
    {
        return "{x="+x+" y="+y+" "+c+"}";
    }

    NPoint minus(NPoint old)
    {
        return new NPoint((short)(x-old.x), (short)(y-old.y), c.minus(old.c));
    }
    
    NPoint decrBy(NPoint old)
    {
       x-=old.x;
       y-=old.y;
                c.l-=old.c.l;
        c.u-=old.c.u;
        c.v-=old.c.v;
        
        return this;
    }

    void set(short x, short y, short l, short u, short v)
    {
        this.x=x;
        this.y=y;
        this.c.l=l;
        this.c.u=u;
        this.c.v=v;
    }

    @Override
    public NPoint clone() 
    {
        
        //return new NPoint(x, y, c.clone());
        
        
        try
        {
            return (NPoint) super.clone();
        } catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException("cantclone");
        }
        
        
    }
    
    
    
    
}
