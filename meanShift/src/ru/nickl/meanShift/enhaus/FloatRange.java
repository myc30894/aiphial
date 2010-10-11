/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.enhaus;

/**
 *
 * @author nickl
 */
public class FloatRange extends NumberRange<Float> {



    public FloatRange(float begin,float end,float step) {
        super(begin, end, step);
    }




    @Override
    protected Float sum(Float a, Float b) {
        return a+b;
    }

}
