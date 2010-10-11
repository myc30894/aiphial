/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.segmentator;

import java.util.Collection;
import ru.nickl.meanShift.direct.Point;

/**
 * Объединение точек изображения. Объекты данного типа содержат точки
 * исходного изображения, объединенные по какому-то признаку.
 * @author nickl
 */
public interface Region {

    /**
     * Возвращет коллекцию точек, лежащих на границе данной области
     * @return коллекцию точек, лежащих на границе данной области
     */
    Collection<Point> getCountour();

    /**
     * Возвращает коллекцию точек, содержащихся в данной области
     * @return коллекцию точек, содержащихся в данной области
     */
    Collection<Point> getPoints();

}
