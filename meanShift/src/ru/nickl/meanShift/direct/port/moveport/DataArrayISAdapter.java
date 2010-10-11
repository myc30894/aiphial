/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.port.moveport;

import ru.nickl.meanShift.direct.filter.BaseMeanShiftFilter;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;

/**
 *
 * @author nickl
 */
public abstract class DataArrayISAdapter extends BaseMeanShiftFilter {

    public DataArrayISAdapter() {
    }
    protected int N = 3;
    protected double h[];
    protected int L;
    protected  double[] data;
    protected double msRawData[];

    protected  int height;
    protected  int width;

    private  void convert(LuvData LUVArray) {
        height = LUVArray.getHeight();
        width = LUVArray.getWidth();


        L = height * width;
        h = new double[2];
        data = new double[height * width * N];
        int k = 0;
        for (int y = 0; y < LUVArray.getHeight(); y++) {
            for (int x = 0; x < LUVArray.getWidth(); x++) {
                data[k++] = LUVArray.getLUV(x, y).l;
                data[k++] = LUVArray.getLUV(x, y).u;
                data[k++] = LUVArray.getLUV(x, y).v;
            }
        }
        msRawData = new double[L * N];
    }

    private  LuvData deconvert() {
       LuvData resultLUVArray = new LuvData(width, height);
        for (int i = 0; i < msRawData.length; i += 3) {
            resultLUVArray.setLUV((i / 3) % width, (i / 3) / width, new LUV(msRawData[i], msRawData[i + 1], msRawData[i + 2]));
        }
        return resultLUVArray;
    }

    public LuvData filter(LuvData rawData)
    {
        convert(rawData);
        doFilter();
        return deconvert();
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    


    protected abstract void doFilter();
}
