/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.MeanShiftImageProcessor;

/**
 * Обработчик изображения, обрабатывающий изображение с использованием одного
 * {@link MeanShiftFilter MeanShift-фильтра}.
 * <br/>
 * По сути является адаптером интерфейса {@link MeanShiftFilter} к интерфейсу {@link MeanShiftImageProcessor}.
 * @author nickl
 * @param <MeanShiftFilterType> тип фильтра, используемого при обработке
 */
public class MeanShiftFilterImageProcessor<MeanShiftFilterType extends MeanShiftFilter>
        extends LuvFilterImageProcessor<MeanShiftFilterType>
        implements MeanShiftImageProcessor
{

    ProgressListener progressListener = null;

    /**
     * Конструктор по умолчанию.
     * Для работы объекта необходимо будет вручную {@link #setFilter(ru.nickl.meanShift.direct.filter.MeanShiftFilter) установить} фильтр
     */
    public MeanShiftFilterImageProcessor()
    {
    }

    /**
     *
     * @param filter фильтр, который будет использоваться для обработки
     */
    public MeanShiftFilterImageProcessor(MeanShiftFilterType filter)
    {
        super(filter);


    }

    public float getColorRange()
    {
        return getFilter().getColorRange();
    }

    public ProgressListener getProgressListener()
    {
        return progressListener;
    }

    public int getSquareRange()
    {
        return getFilter().getSquareRange();
    }

    public void setColorRange(float colorRange)
    {
        getFilter().setColorRange(colorRange);
    }

    public void setProgressListener(ProgressListener progressListener)
    {
        this.getFilter().setProgressListener(progressListener);
    }

    public void setSquareRange(short squareRange)
    {
        getFilter().setSquareRange(squareRange);
    }
}
