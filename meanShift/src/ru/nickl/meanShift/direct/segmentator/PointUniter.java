/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import ru.nickl.meanShift.direct.*;
import java.util.Collection;

/**
 * Интерфейс объектов, обеспечивающих объединение LUV-данных в области
 * И предоставляющих информацию о полученных областых
 * @author nickl
 */
public interface PointUniter
{

    /**
     * Выволняет объединение данных в области.
     */
    public void formRegions();

    /**
     * Возвращет регион, к которому была отнесена занданная точка
     * @param x координато точки по оси x
     * @param y коодината точки по оси y
     * @return область к которой была отнесена данная точка.
     */
    public Region getPointRegion(int x, int y);

    /**
     * Возвращет регион, к которому была отнесена занданная точка
     * @param p - точка    
     * @return область к которой была отнесена данная точка.
     */
    public Region getPointRegion(Point p);

    /**
     * Возвращает набор всех выделенных регионов
     * @return коллеуцию из созданных регионов
     */
    public Collection<Region> getRegions();

    /**
     * Устанавливает массив исходных данных, исз которых при вызове метода
     * {@link #findRegions() } будут созданы регионы
     * @param data массив исходных данных
     */
    public void setData(LuvData data);

    /**
     * Возвращает массив исходных данных
     * @param data массив исходных данных
     */
    public LuvData getData();
}
