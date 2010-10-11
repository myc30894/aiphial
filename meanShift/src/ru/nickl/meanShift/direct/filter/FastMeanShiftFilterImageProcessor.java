/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

/**
 *
 * @author nickl
 */
public class FastMeanShiftFilterImageProcessor<MeanShiftFilterType extends FastMSFilter>
        extends MeanShiftFilterImageProcessor<MeanShiftFilterType>
{

    public FastMeanShiftFilterImageProcessor()
    {
    }

    public FastMeanShiftFilterImageProcessor(MeanShiftFilterType filter)
    {
        super(filter);
    }

    public float getDiffColor()
    {
        return getFilter().getDiffColor();
    }

    public void setDiffColor(float a)
    {
        getFilter().setDiffColor(a);
    }

    public int getDiffSquare()
    {
        return getFilter().getDiffSquare();
    }

    public void setDiffSquare(int a)
    {
        getFilter().setDiffSquare(a);
    }


}
