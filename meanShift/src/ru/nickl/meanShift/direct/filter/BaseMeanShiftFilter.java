/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.filter;

/**
 *
 * @author nickl
 */
public abstract class BaseMeanShiftFilter implements MeanShiftFilter {
    //protected LUV[][] LUVArray;
    protected double colorRange;
    protected double colorRangeP2;
    //protected int height;
    ProgressListener progressListener = null;
    //protected LUV[][] resultLUVArray;
    protected short squareRange;
   // protected int width;

    public BaseMeanShiftFilter()
    {
    }

    public float getColorRange()
    {
        return (float) colorRange;
    }

  

    public ProgressListener getProgressListener()
    {
        return progressListener;
    }    

    public int getSquareRange()
    {
        return squareRange;
    }

    

    public void setColorRange(float colorRange)
    {
        this.colorRange = colorRange;
        this.colorRangeP2 = colorRange * colorRange;
    }

    public void setProgressListener(ProgressListener progressListener)
    {
        this.progressListener = progressListener;
    }
    

    public void setSquareRange(short squareRange)
    {
        this.squareRange = squareRange;
    }

    protected void fireProgressListener(int proc) {
        if (progressListener != null) {
            progressListener.proc(proc);
        }
    }

}
