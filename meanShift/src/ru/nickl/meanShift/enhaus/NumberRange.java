/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.enhaus;

import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author nickl
 */
public abstract class NumberRange<T extends Number> implements Iterable<T> {

    
    T begin;
    T end;
    T step;

    public NumberRange(T begin, T end, T step) {
        
        this.begin = begin;
        this.end = end;
        this.step = step;
    }


    public Iterator<T> iterator() {
        return new Iterator<T>() {

            private T cur = begin;

            @SuppressWarnings("unchecked")
            public boolean hasNext() {
                return ((Comparable) cur).compareTo(end)<=0;
            }

            public T next() {
                T result = cur;
                cur = sum(cur, step);

                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException("Not supported");
            }
        };
    }

    protected abstract T sum(T a, T b);

}
