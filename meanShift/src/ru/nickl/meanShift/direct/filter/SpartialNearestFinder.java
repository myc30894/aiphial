/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.*;
import ru.nickl.meanShift.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.Point;

/**
 * Объекты данного класса находят точки, лежащие в одном квадрате
 * с указанной точкой. Кроме этого можно указать дополнительный критерий отбора
 * @author nickl
 */
public class SpartialNearestFinder implements NearestFinder
{

    private short squareRange = 0;
    private Criteria additionalCriteria;

    public SpartialNearestFinder()
    {
    }

    /**
     *
     * @param LUVArray массив исходных данных
     */
    public SpartialNearestFinder(LuvData LUVArray)
    {
        setData(LUVArray);
    }

    /*
    public StdNearestFinder(LuvData LUVArray, short squareRange) {
    this(LUVArray);
    this.squareRange = squareRange;
    }
     */
    /**
     *
     * @param LUVArray массив исходных данных
     * @param squareRange радиус (на самом деле половина стороны квадрата)
     * в котором нужно будет искать точки
     * @param additionalCriteria дополнительный критерий отбора точек из тех,
     * что уже были найдены в квадрате (может быть null)
     */
    public SpartialNearestFinder(LuvData LUVArray, short squareRange, Criteria additionalCriteria)
    {
        this.setData(LUVArray);
        this.squareRange = squareRange;
        this.additionalCriteria = additionalCriteria;

    }

    /**
     *
     * @param squareRange радиус (на самом деле половина стороны квадрата)
     * в котором нужно будет искать точки
     * @param additionalCriteria дополнительный критерий отбора точек из тех,
     * что уже были найдены в квадрате (может быть null)
     */
    public SpartialNearestFinder(short squareRange, Criteria additionalCriteria)
    {
        this.squareRange = squareRange;
        this.additionalCriteria = additionalCriteria;
    }

    private Point[][] points;
    private short height;
    private short width;

    /**
     * Возвращет точки из квадрата, без учета дополнительного критерия
     * @param x координата x
     * @param y координата y
     * @return точки из квадрата, без учета дополнительного критерия
     */
    public List<Point> getPointsWithinSquare(int x, int y)
    {
        return getPointsWithinSquare((short) x, (short) y);
    }

    /**
     * Возвращет установленный дополнительный критерий или null если этот критерий не был установлен
     * @return
     */
    public Criteria getAdditionalCriteria()
    {
        return additionalCriteria;
    }

    /**
     * Устанавливает дополнительный критерий отбора точек из тех,
     * что уже были найдены в квадрате (может быть null)
     * @param additionalCriteria дополнительный критерий (может быть null)
     */
    public void setAdditionalCriteria(Criteria additionalCriteria)
    {
        this.additionalCriteria = additionalCriteria;
    }

    /**
     * Устанавливает массив исходных данных
     * @param LUVArray массив исходных данных
     */
    public void setData(LuvData LUVArray)
    {

        this.height = (short) LUVArray.getHeight();
        this.width = (short) LUVArray.getWidth();
        points = new Point[height][width];

        for (int y = 0; y < LUVArray.getHeight(); y++)
        {
            for (int x = 0; x < LUVArray.getWidth(); x++)
            {
                points[y][x] = new Point((short) x, (short) y, LUVArray.getLUV(x, y));
            }
        }

    }

    private Point getPoint(short x, short y)
    {
        return points[y][x];
    // return new Point(x0, y0, LUVArray[y0][x0]);
    }

    /**
     * Устанавлиает радиус (на самом деле половину стороны квадрата)
     * в котором нужно будет искать точки
     * @param squareRange радиус (на самом деле половина стороны квадрата)
     * в котором нужно будет искать точки
     */
    public void setSquareRange(short squareRange)
    {
        this.squareRange = squareRange;
    }

    /**
     * Возвращает радиус (на самом деле половину стороны квадрата)
     * в котором нужно будет искать точки
     * @return радиус (на самом деле половина стороны квадрата)
     * в котором нужно будет искать точки
     */
    public short getSquareRange()
    {
        return squareRange;
    }

    /**
     * Возвращет точки из квадрата, без учета дополнительного критерия
     * @param x координата x
     * @param y координата y
     * @return точки из квадрата, без учета дополнительного критерия
     */
    public List<Point> getPointsWithinSquare(short x, short y)
    {
        List<Point> result = new ArrayList<Point>((squareRange + 1) * (squareRange + 1) * 4);


        for (short y0 = (short) (y - squareRange); y0 <= y + squareRange; y0++)
        {

            if (y0 >= 0 && y0 < height)
            {

                for (short x0 = (short) (x - squareRange); x0 <= x + squareRange; x0++)
                {
                    if (x0 >= 0 && x0 < width)
                    {
                        result.add(getPoint(x0, y0));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Возвращет точки из квадрата, удовлетворяющие дополнительному критерию,
     * если тот был установлен.
     * Если дополнительный критерий не был установлен
     * то действает так же как и {@link #getPointsWithinSquare(int, int) }
     * @param x координата x
     * @param y координата y
     * @return точки из квадрата, с учетом дополнительного критерия
     */
    public Collection<Point> getNearest(Point old)
    {

        if(points==null) throw new IllegalStateException("Luvdata was not setted");

        List<Point> pWSS = getPointsWithinSquare(old.x, old.y);

        List<Point> result = new ArrayList<Point>(pWSS.size());

        if (additionalCriteria != null)
        {
            for (Point point : pWSS)
            {
                if (additionalCriteria.isNear(old, point))
                {
                    result.add(point);
                }
            }
        } else
        {
            return pWSS;
        }

        return result;
    }

    /**
     * Дополнительный критерий, определяет лежит ли точка рядом
     * с другой точкой или нет. ВЫборка осуществляется из точек,
     * уже находящихся в квадрате
     */
    public interface Criteria
    {

        /**
         * Проверяет находится ли точка рядом с другой точкой
         * @param a
         * @param b
         * @return
         */
        boolean isNear(Point a, Point b);
    }
}
