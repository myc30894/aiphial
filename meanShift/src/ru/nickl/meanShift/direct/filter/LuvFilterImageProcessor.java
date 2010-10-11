/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.LuvImageProcessor;

/**
 * Обработчик изображения, обрабатывающий изображение с использованием одного
 * {@link LUVFilter LUV-фильтра}.
 * <br/>
 * По сути является адаптером интерфейса {@link LUVFilter} к интерфейсу {@link LuvImageProcessor}.
 * @author nickl
 * @param <LUVFilterType> тип фильтра, используемого при обработке
 */
public class LuvFilterImageProcessor<LUVFilterType extends LUVFilter> extends BaseLuvImageProcessor
{
    private LUVFilterType filter;

    /**
     * Конструктор по умолчанию.
     * Для работы объекта необходимо будет вручную {@link #setFilter(ru.nickl.meanShift.direct.filter.MeanShiftFilter) установить} фильтр
     */
    public LuvFilterImageProcessor()
    {
    }

    /**
     *
     * @param filter фильтр, который будет использоваться для обработки
     */
    public LuvFilterImageProcessor(LUVFilterType filter)
    {
        this.filter = filter;
    }

    /**
     * Возвращает фильтр, используемый при обработке
     * @return фильтр, используемый при обработке
     */
    public LUVFilterType getFilter()
    {
        return filter;
    }

    /**
     * {@inheritDoc }
     */
    public void process()
    {
        resultLUVArray = filter.filter(LUVArray);
    }

    /**
     * Устанавливает фильтр, используемый при обработке
     * @param filter фильтр, используемый при обработке
     */
    public void setFilter(LUVFilterType filter)
    {
        this.filter = filter;
    }
}
