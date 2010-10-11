/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.*;
import ru.nickl.meanShift.direct.filter.BaseMeanShiftFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.lang.Math.*;
import static ru.nickl.meanShift.direct.PointUtils.*;

/**
 * Быстрый MeanShift фильтр. Основывается на предположении о том,
 * что близкие точки придут в один и тот же центр. Критерий близости задается пареметрами
 * {@link #agrDiffSquare} - близость по расположению и
 * {@link #agrDiffColor} - близость по цвету.
 * Большие значения будут объединять больше точек тем самым увеличивая скорость
 * и уменьшая качество фильтра.
 * Значения, большие значений соответвующих размеров окна не имеют смысла.
 * @author nickl
 */
public class FastMSFilter extends BaseMeanShiftFilter
{

    private float agrDiffColor;
    private int agrDiffSquare;
    private NearestFinder nearestFinder;
    private LUV nulllUV = new LUV(0.0F, 0.0F, 0.0F);
    private LuvData LUVArray = null;
    private LuvData resultLUVData = null;

    /**
     * Конструктор по умолчанию, устанавливает dk = 10; ds = 5;
     * что увеличивает скорость приерно в 3-5 раз и практически не влияет на качество.
     */
    public FastMSFilter()
    {
        agrDiffColor = 10;
        agrDiffSquare = 5;
    }

    /**
     * Конструктор, устанавливающий параметры объединения точек.
     * @param dk - максимальная разница между ближайшими точками в цветовой области
     * @param ds - квадрат, в котором лежит ближайшие точки в пространсвенной области
     */
    public FastMSFilter(float dk, int ds)
    {
        agrDiffColor = dk;
        agrDiffSquare = ds;
    }

    /**
     * Определяет близкие ли эти точки.
     * @param p - точка близость которой нужно определить
     * @param old - основная точка
     * @return true - если точки близки
     */
    private boolean isAggregatable(Point p, Point old)
    {
        return resultLUVData.getLUV(p.x, p.y) == null &&
                abs(p.x - old.x) < getDiffSquare() &&
                abs(p.y - old.y) < getDiffSquare() &&
                Dim(p.c, old.c) < getDiffColor();
    }

    /**
     * {@inheritDoc }
     */
    public LuvData filter(LuvData rawData)
    {

        this.LUVArray = rawData;

        int height = rawData.getHeight();
        int width = rawData.getWidth();

        resultLUVData = new LuvData(width, height);

        nearestFinder = new DimNearestFinder(LUVArray, squareRange, colorRange);


        int pointNumber = 1;
        int totalPoints = height * width;

        for (short y = 0; y < height; y++)
        {
            for (short x = 0; x < width; x++)
            {

                if (resultLUVData.getLUV(x, y) == null)
                {

                    movePoints(x, y);
                }

                fireProgressListener((pointNumber += 100) / totalPoints);

            }
        }

        nearestFinder = null;
        this.LUVArray = null;
        LuvData result = this.resultLUVData;
        this.resultLUVData = null;
        return result;
    }

    /**
     * ВЫчисляет вектор среднего сдвига для указанной точки и
     * добавляет встретивишиеся "похожие" точки в {@link #aggregatedList}
     * @param old - точка
     * @return вектор среднего сдвига для одной итерации
     */
    protected Point calkMh(Point old)
    {
        Collection<Point> nearest = nearestFinder.getNearest(old);

        for (Point p : nearest)
        {

            if (isAggregatable(p, old))
            {
                aggregatedList.add(p);
            }
        }
        //mean.set(0, 0, 0, 0, 0);
        Point mean = new Point((short) 0, (short) 0, new LUV(0, 0, 0));

        for (Point point : nearest)
        {
            mean.incrBy(point.minus(old));
        }

        if (nearest.size() != 0)
        {
            mean.divide(nearest.size());
        }

        return mean;
    }
    /**
     * Список "собранных" точек при вычислении вектора среднего сдвига для основной точки.
     * Все эти точки придут в ту же точку что и основная точка данного сдвига.
     */
    protected List<Point> aggregatedList = new ArrayList<Point>();

    /**
     * Приводит точку с указанными координатами в ее точку притяжения.
     * Так же приводит в эту точку все "похожие" точки, встретившиеся по пути.
     * @param x
     * @param y
     */
    protected void movePoints(short x, short y)
    {
        aggregatedList.clear();

        Point curPossition = new Point(x, y, LUVArray.getLUV(x, y).clone());
        Point Mh;
        int n = 1000;
        do
        {
            Mh = calkMh(curPossition);
            curPossition.incrBy(Mh);
        } while (Dim(Mh.c, nulllUV) > 0.01f && n-- > 0);




        for (Point p : aggregatedList)
        {
            resultLUVData.setLUV(p.x, p.y, curPossition.c);
        }

        resultLUVData.setLUV(x, y, curPossition.c);

    }

    /**
     * @return the agrDiffColor
     */
    public float getDiffColor()
    {
        return agrDiffColor;
    }

    /**
     * @param agrDiffColor the agrDiffColor to set
     */
    public void setDiffColor(float agrDiffColor)
    {
        this.agrDiffColor = agrDiffColor;
    }

    /**
     * @return the agrDiffSquare
     */
    public int getDiffSquare()
    {
        return agrDiffSquare;
    }

    /**
     * @param agrDiffSquare the agrDiffSquare to set
     */
    public void setDiffSquare(int agrDiffSquare)
    {
        this.agrDiffSquare = agrDiffSquare;
    }
}
