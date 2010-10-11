/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.enhaus;

/**
 *
 * @author nickl
 */
public class IntRange extends NumberRange<Integer> {

    public IntRange(int begin,int end,int step) {
        super(begin, end, step);
    }



    @Override
    protected Integer sum(Integer a, Integer b) {
        return a+b;
    }

}
