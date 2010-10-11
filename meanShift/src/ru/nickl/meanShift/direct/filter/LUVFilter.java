/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;

/**
 * Фильтр использующий LUV-представление изображения.
 * Объекты данного типа представляют алгоритм обработки
 * @author nickl
 */
public interface LUVFilter {

    /**
     * Применение фильтра к данным изображения
     * @param rawData LUV-предствление исходного изображения
     * @return LUV-предствление обработанного изображения
     */
    public LuvData filter(LuvData rawData);

}
