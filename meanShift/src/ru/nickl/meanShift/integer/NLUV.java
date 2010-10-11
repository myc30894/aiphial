/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.integer;

/**
 *
 * @author nickl
 */
public class NLUV {
    
      public NLUV()
    {
    }

    public NLUV(short l, short u, short v)
    {
        this.l = l;
        this.u = u;
        this.v = v;
    }

    @Override
    public  NLUV clone() 
    {
        return  new NLUV(l, u, v);
    }   
    
    
    short l;
    short u;
    short v;

    @Override
    public String toString()
    {
        return super.toString()+"{l="+l+" u="+u+" v="+v+"}";
    }

    public NLUV minus(NLUV a)
    {
        return  new NLUV((short)(l-a.l),(short)(u-a.u),(short)( v-a.v));
    }

}
