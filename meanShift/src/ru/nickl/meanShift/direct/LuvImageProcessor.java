/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct;

import java.awt.image.BufferedImage;

/**
 * Обработчик изображений, использующий представление изображений в формате LUV.
 * Схема работы обработчика заключается в получении изображения методом
 * {@link #setSourceImage(java.awt.image.BufferedImage)},
 * затем обработки его методом {@link #process()} и получения резулультата
 * методом {@link #getProcessedImage() }.
 * Кроме того для получения LUV - представления данных используются методы
 * {@link #getLUVArray() } и {@link #getResultLUVArray() }.
 * @author nickl
 */
public interface LuvImageProcessor {

    /**
     * Метод получения обработанного изображения
     * @return обработанное изображение
     */
    BufferedImage getProcessedImage();

    /**
     * Возвращает LUV - представление исходного изображения
     * @return LUV - представление исходного изображения
     */
    LuvData getLUVArray();

   /**
    * @return LUV - представление обработанного изображения
    */
    LuvData getResultLUVArray();

    /**
     * Запускает процесс обработки изображения
     */
    void process();

    /**
     * Устанавливает исходное изображение
     * @param sourceImage изображение, которое предстоит обработать
     */
    void setSourceImage(BufferedImage sourceImage);

}
