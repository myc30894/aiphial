/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.enhaus;

import java.util.Iterator;

/**
 * Итератор значения поля, содержит имя поля и генератор значений,
 * которые это поле будет принимать
 * @author nickl
 */
public class FieldIterator<T> implements  Iterable<T> {


    private String fieldname;

    private Iterable<T> iterable;

    /**
     * Конструктор
     * @param fieldname ипя изменяемого поля
     * @param iterable генератор значений, которые поле будет принимать
     */
    public FieldIterator(String fieldname, Iterable<T> iterable)
    {
        this.fieldname = fieldname;
        this.iterable = iterable;
    }

    /**
     * Возвращвет имя поля, обслуживаемого данным итератором
     * @return
     */
    public String getFieldname()
    {
        return fieldname;
    }

    /**
     * Возвращает собственно итератор значений,
     * которые будут примваиваться полю
     * @return
     */
    public Iterator<T> iterator()
    {
        return iterable.iterator();
    }

}
