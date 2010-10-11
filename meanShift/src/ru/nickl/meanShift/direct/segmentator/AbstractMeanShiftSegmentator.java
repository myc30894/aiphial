/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import java.awt.image.BufferedImage;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor;

/**
 * Абстрактный MeanShiftSegmentator, является каркасом для алгоритмов meanShift-сегментации
 * Обеспечивает кеширование MeanShift-фильтра. Автоматический сброс кеша не будет осущствлен,
 * если параметры фильтра будут изменены у самого фильтра, а не через этот объект
 * @author nickl
 */
public abstract class AbstractMeanShiftSegmentator extends MeanShiftFilterImageProcessor<MeanShiftFilter> implements MeanShiftSegmentator
{

    public AbstractMeanShiftSegmentator()
    {
    }

    public AbstractMeanShiftSegmentator(MeanShiftFilter filter)
    {
        super(filter);
    }

    /**
     * {@inheritDoc }
     * 
     * При этом выполнение фильтра будет произведено только в случае если были
     * изменены исходное изображение или параметры фильтра
     */
    @Override
    public void process()
    {
        if (mustRebuildSources)
        {
            super.process();
            mustRebuildSources = false;
            rebuildSources();
        }
    }

    /**
     * Метод, вызываемый при перприменении фильтра.
     * Если какето операции должны быть выполнены только в случае изменении
     * исходного изображения и/или параметров meanShift фильтра, то их имеет
     * смысл производить в этом методе, а не в методе {@link #process() };
     */
    protected void rebuildSources()
    {
    }
    private boolean mustRebuildSources = true;

    private void reset()
    {
        mustRebuildSources = true;
    }

    /**
     * {@inheritDoc };
     */
    @Override
    public void setFilter(MeanShiftFilter filter)
    {
        reset();
        super.setFilter(filter);
    }

    /**
     * {@inheritDoc };
     */
    @Override
    public void setSourceImage(BufferedImage sourceImage)
    {
        reset();
        super.setSourceImage(sourceImage);
    }

    /**
     * {@inheritDoc };
     */
    @Override
    public void setSquareRange(short squareRange)
    {
        reset();
        super.setSquareRange(squareRange);
    }

    /**
     * {@inheritDoc };
     */
    @Override
    public void setColorRange(float colorRange)
    {
        reset();
        super.setColorRange(colorRange);
    }
}
