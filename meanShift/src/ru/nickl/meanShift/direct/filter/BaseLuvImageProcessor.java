/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import java.awt.image.BufferedImage;
import ru.nickl.meanShift.direct.LUVConverter;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.LuvImageProcessor;

/**
 * Каркас для реализации {@link LuvImageProcessor}
 * предоставляет средства установки полей
 * {@link #LUVArray} и {@link #resultLUVArray} через открытие
 * и закрытие файла с изображением.
 * @author nickl
 */
public abstract class BaseLuvImageProcessor implements LuvImageProcessor
{

    /**
     * {@link LuvData} исходного изображения
     */
    protected LuvData LUVArray;
    /**
     * Высота обрабатываемого изображения
     */
    protected int height;
    /**
     * {@link LuvData} результирующего изображения
     */
    protected LuvData resultLUVArray;
    /**
     * Ширина обрабатываемого изображения
     */
    protected int width;

    public BaseLuvImageProcessor()
    {
    }

    /**
     * Возвращает высоту обрабатываемого изображения
     * @return высоту обрабатываемого изображения
     */
    public int getHeight()
    {
        return height;
    }

    public LuvData getLUVArray()
    {
        return LUVArray;
    }

    public BufferedImage getProcessedImage()
    {
        LUVConverter lUVConverter = new LUVConverter();
        return lUVConverter.LUVArrayToBufferedImage(resultLUVArray.getLUVArray());
    }

    public LuvData getResultLUVArray()
    {
        return resultLUVArray;
    }

    /**
     * Возвращает ширину обрабатываемого изображения
     * @return ширину обрабатываемого изображения
     */
    public int getWidth()
    {
        return width;
    }

    public void setSourceImage(BufferedImage sourceImage)
    {
        height = sourceImage.getHeight();
        width = sourceImage.getWidth();
        LUVConverter lUVConverter = new LUVConverter();
        LUVArray = new LuvData(lUVConverter.toLUVDArray(sourceImage));
    }
}
