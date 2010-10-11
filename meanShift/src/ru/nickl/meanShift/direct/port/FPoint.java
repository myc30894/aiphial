/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.port;

import ru.nickl.meanShift.direct.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickl
 */
public class FPoint implements Cloneable{

    int idxd = -1;
   
    public FPoint(double x, double y, LUV c)
    {
        this.x = x;
        this.y = y;
        this.c = c; 
    }

    public FPoint()
    {
        setNuls();
    }   
        
    double x;
    double y;
    LUV c;
    
    public void incrBy(FPoint a)
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
    
     void divide(double a)
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
        //return "{x="+x+" y="+y+" "+c+"}";
        return x+"\t"+y+"\t"+c.l+"\t"+c.u+"\t"+c.v;
    }

   

    FPoint minus(FPoint old)
    {
        return new FPoint((x-old.x), (y-old.y), c.minus(old.c));
    }
    
    FPoint decrBy(FPoint old)
    {
       x-=old.x;
       y-=old.y;
                c.l-=old.c.l;
        c.u-=old.c.u;
        c.v-=old.c.v;
        
        return this;
    }

    void set(double x, double y, double l, double u, double v)
    {
        this.x=x;
        this.y=y;
        this.c.l=l;
        this.c.u=u;
        this.c.v=v;
    }

     @Override
    public  FPoint clone()
    {
        try
        {
            FPoint r = (FPoint) super.clone();
            r.c=c.clone();
            return r;
        } catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    
    
    
}
