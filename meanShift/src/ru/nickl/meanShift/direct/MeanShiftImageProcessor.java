/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct;

/**
 * Обработчик изображения связанный с алгоритмом MeanShift.
 * Расширяет базовый интерфейс обработчика свойсвами алгорима MeanShift
 * @author nickl
 */
public interface MeanShiftImageProcessor extends LuvImageProcessor, IMeanShift
{


    //ProgressListener getProgressListener();
}
