/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct;

/**
 * Утилитарный класс, осуществляющий операции над точками
 * @author nickl
 */
public class PointUtils
{

    /**
     * Вычисляет сумму квадратов разностей координат цветов в пространстве LUV
     * @param a LUV-цвет
     * @param b LUV-цвет
     * @return сумму квадратов разностей цветов
     */
    public static double Dim(LUV a, LUV b)
    {
        return p2(a.l - b.l) + p2(a.u - b.u) + p2(a.v - b.v);
    }

    /**
     * Вычисляет сумму квадратов разностей координат точек и складывает ее
     * с суммой квадратов координат цветов в пространстве LUV.
     *
     * <i>Адеватность подобной оценки расстояния довольно спорна,
     * потому что сумма квадратов разности цветов и координат не равносильны</i>
     * @param a
     * @param b
     * @return
     */
    public static double Dim(Point a, Point b)
    {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + Dim(a.c, b.c);
    }

    private static double p2(double a)
    {
        return a * a;
    }
}
