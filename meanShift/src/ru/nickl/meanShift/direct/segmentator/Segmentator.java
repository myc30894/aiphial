/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.segmentator;

import java.util.Collection;
import ru.nickl.meanShift.direct.LuvImageProcessor;

/**
 * Сегментатор, алгоритм, позволяющмй выделять области на изображении
 * @author nickl
 */
public interface Segmentator extends LuvImageProcessor {

    /**
     * Возвращает коллекцию выделенных регионов в процессе сегментации.
     * @return регионы, выделенные в процесе сегментации
     */
    Collection<Region> getRegions();

}
