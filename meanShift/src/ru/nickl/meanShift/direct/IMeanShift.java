/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct;

/**
 * Интерфейс всего, что связанно с Mean-shift в обработки изображений.
 * Предоставляет методы для контроля параметров этого алгоритма
 * @author nickl
 */
public interface IMeanShift {

    /**
     * возвращает радиус окна в цветовой области
     * @param colorRange радиус окна в цветовой области
     */
    float getColorRange();

    /**
     * Возвращет радиус окна в пространственной области
     * @return радиус окна в пространственной области
     */
    int getSquareRange();

    /**
     * устанавливает радиус окна в цветовой области
     * @param colorRange радиус окна в цветовой области
     */
    void setColorRange(float colorRange);

    /**
     * устанавливает радиус окна в пространственной области
     * @param squareRange радиус окна в пространственной области
     */
    void setSquareRange(short squareRange);

}
