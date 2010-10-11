/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.*;
import java.util.Collection;
import java.util.List;
import ru.nickl.meanShift.direct.Point;

/**
 * Интерфейс объектов, находящих в массиве исходных данных точки,
 * ближайшие(по определенному критерию) к заданной
 * @author nickl
 */
public interface NearestFinder {

    /**
     * Возвращвет коллекцию точек, ближайших к заданной
     * @param old исходная точка
     * @return коллекцию точек, ближайших к заданной
     */
    public Collection<Point> getNearest(Point old);

    /**
     * Устанавливает данные в которых будет осуществляться поиск
     * @param data исходные данные
     */
    public void setData(LuvData data);

}
