/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.IMeanShift;

/**
 * {@link LUVFilter LUV-фильтр} реализующий MeanShift фильтр,
 * предоставляет методы для управления параметрами алгоритма через интерфейс
 * {@link IMeanShift}
 * @author nickl
 */
public interface MeanShiftFilter extends LUVFilter, IMeanShift
{    

    ProgressListener getProgressListener();

    void setProgressListener(ProgressListener progressListener);
   
}
