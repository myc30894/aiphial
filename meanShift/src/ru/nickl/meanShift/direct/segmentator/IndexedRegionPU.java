/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.segmentator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.Point;
import ru.nickl.meanShift.direct.filter.NearestFinder;
import ru.nickl.meanShift.direct.filter.SpartialNearestFinder;

/**
 * Реализация PointUniter на основе простого алгоритма разростания регионов, путем присвоения регионам индексов
 * Но при помощи
 * {@link #setNearestFinder(ru.nickl.meanShift.direct.filter.NearestFinder) установки}
 * других стратегий определения ближних элементов,
 * возможно изменить стратегию кластеризации
 * @author nickl
 */
public class IndexedRegionPU implements PointUniter
{

    protected static final int UNINDEXED_LABEL = -1;
    private int index;
    protected List<IndexedRegion> regions;
    private int[][] segmentIndexMatrix;
    protected LuvData data;
    protected NearestFinder nearestFinder;
    private double equalityRange = 1;

    /**
     * Реинициализовать объект для повторгого использования
     * Этот метод автоматически вызовется при вызове {@link #setData(ru.nickl.meanShift.direct.LuvData) }
     */
    private void reinit()
    {
        regions = new ArrayList<IndexedRegion>(100);
        index = UNINDEXED_LABEL;
        initIndexMatrixWithUnindexedLabel();


        if (nearestFinder == null)
        {
            setDefaultNearestFinder();
        }
        nearestFinder.setData(data);
    }

    /**
     * Переиндексирование всех регионов и их точек. Этот метод необходимо вызвать если
     * {@link #regions массив регионов} както изменялся извне
     */
    protected void rebuildIndexMatrix()
    {

        initIndexMatrixWithUnindexedLabel();


        for (int i = 0; i < regions.size(); i++)
        {
            IndexedRegion stdRegionImpl = regions.get(i);
            stdRegionImpl.index=i;
            for (Point p : stdRegionImpl.getPoints())
            {
                segmentIndexMatrix[p.y][p.x] = i;
            }
        }
    }

    private void setDefaultNearestFinder()
    {
        SpartialNearestFinder spartialNearestFinder = new SpartialNearestFinder();
        spartialNearestFinder.setSquareRange((short) 1);
        spartialNearestFinder.setAdditionalCriteria(new SpartialNearestFinder.Criteria()
        {

            public boolean isNear(Point a, Point b)
            {

                LUV d = a.c.minus(b.c);
                if (Math.abs(d.l) < equalityRange && Math.abs(d.u) < equalityRange && Math.abs(d.v) < equalityRange)
                {
                    return true;
                } else
                {
                    return false;
                }
            }
        });

        setNearestFinder(spartialNearestFinder);
    }

    public void formRegions()
    {

        reinit();

        for (int y = 0; y < data.getHeight(); y++)
        {
            for (int x = 0; x < data.getWidth(); x++)
            {
                if (segmentIndexMatrix[y][x] == UNINDEXED_LABEL)
                {
                    index++;
                    IndexedRegion curRegion = new IndexedRegion(this, index);
                    regions.add(curRegion);
                    growRegion(new Point((short) x, (short) y, data.getLUV(x, y)), curRegion);
                    if (curRegion.getPoints().isEmpty())
                    {
                        regions.remove(index);
                        index--;
                    } 
                }
            }
        }
    }

    /**
     * Метод производящий расростание региона, вызывается из {@link #formRegions() }
     * Может быть переопределен в наследниках
     * @param first первая точка этого региона, предполагается, что с это точки будет начинаться новый регион
     * @param region новосозданный регион, в него должны добавляться все точки.
     * @param index индекс этого региона в таблице индексов, при помещении в этот регион точки,
     * внути метода этот индекс будет присвоен позиции в segmentIndexMatrix, соответсвующей этой точке.
     */
    protected void growRegion(Point first, IndexedRegion region)
    {
        Deque<Point> procList = new LinkedList<Point>();
        procList.addLast(first);

        while (!procList.isEmpty())
        {
            Point point = procList.pop();

            for (Point nearestPoint : getNearestFinder().getNearest(point))
            {
                if (segmentIndexMatrix[nearestPoint.y][nearestPoint.x] == UNINDEXED_LABEL)
                {

                    region.add(nearestPoint);
                    procList.addFirst(nearestPoint);
                }
            }
        }
    }

    public Region getPointRegion(Point p)
    {
        return getPointRegion(p.x, p.y);
    }

    public Region getPointRegion(int x, int y)
    {
        int label = segmentIndexMatrix[y][x];
        if (label == UNINDEXED_LABEL)
        {
            return null;
        }
        return regions.get(label);
    }

    /**
     * Реаизация интерфейса {@link Region} использумая в {@link IndexedRegionPU}
     * @author nickl
     */
    protected  class IndexedRegion implements Region
    {

        private IndexedRegionPU rm;
        private Collection<Point> points = new ArrayList<Point>();
        private int width;
        private int height;
        private int index;

        protected IndexedRegion(IndexedRegionPU rm, int index)
        {
            this.rm = rm;
            LuvData data = rm.getData();
            height = data.getHeight();
            width = data.getWidth();
            this.index = index;
        }

        protected void add(Point p)
        {
            rm.segmentIndexMatrix[p.y][p.x] = index;
            points.add(p);
        }

        protected void remove(Point p)
        {
            rm.segmentIndexMatrix[p.y][p.x] = IndexedRegionPU.UNINDEXED_LABEL;
            points.remove(p);
        }

        /**
         * Возвращает коллекцию точек, содержащихся в данной области
         * @return коллекцию точек, содержащихся в данной области
         */
        public Collection<Point> getPoints()
        {
            return Collections.unmodifiableCollection(points);
        }

        /**
         * Возвращет коллекцию точек, лежащих на границе данной области
         * @return коллекцию точек, лежащих на границе данной области
         */
        public Collection<Point> getCountour()
        {
            Collection<Point> result = new ArrayList<Point>();

            for (Point point : points)
            {
                if (isOnEdge(point))
                {
                    result.add(point);
                }
            }

            return Collections.unmodifiableCollection(result);
        }

        /**
         * проверяет находится ли данная точка на границе региона
         * @param p точка
         * @return true - если находится и false - если не находится
         */
        private boolean isOnEdge(Point p)
        {
            for (short y0 = (short) (p.y - 1); y0 <= p.y + 1; y0++)
            {

                if (y0 >= 0 && y0 < height)
                {

                    for (short x0 = (short) (p.x - 1); x0 <= p.x + 1; x0++)
                    {
                        if (x0 >= 0 && x0 < width)
                        {

                            if (rm.getPointRegion(x0, y0) != this)
                            {
                                return true;
                            }

                        }
                    }
                }
            }

            return false;
        }

        public int getIndex()
        {
            return index;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="get/set methods">
    public Collection<Region> getRegions()
    {
        return Collections.<Region>unmodifiableCollection(regions);
    }

    private  void initIndexMatrixWithUnindexedLabel()
    {
        segmentIndexMatrix = new int[data.getHeight()][data.getWidth()];
        for (int i = 0; i < segmentIndexMatrix.length; i++)
        {
            for (int j = 0; j < segmentIndexMatrix[i].length; j++)
            {
                segmentIndexMatrix[i][j] = UNINDEXED_LABEL;
            }
        }
    }

    public void setData(LuvData data)
    {
        this.data = data;
        reinit();
    }

    public LuvData getData()
    {
        return data;
    }

    /**
     * Возвращает установленный {@link NearestFinder}, или
     * null если используется установленный по умочанию
     * @return установленный {@link NearestFinder}, или
     * null если используется установленный по умочанию
     */
    public NearestFinder getNearestFinder()
    {
        return nearestFinder;
    }

    /**
     * Устанавливает отличный от установленного поумолчанию {@link NearestFinder}
     * @param nearestFinder
     */
    public void setNearestFinder(NearestFinder nearestFinder)
    {
        this.nearestFinder = nearestFinder;
    }

    /**
     * Возвращает установленный критерий еквивалентности точек
     * @return
     */
    public double getEqualityRange()
    {
        return equalityRange;
    }

    /**
     * Устанавливает критерий еквивалентности точек,
     * чем болше значение тем критерий менее строг
     * @param C критерий еквивалентности точек
     */
    public void setEqualityRange(double C)
    {
        this.equalityRange = C;
    }
    //</editor-fold>
}
