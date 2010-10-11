/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.PointUtils;

/**
 * Фильтр Собела, использующий представление цвета в формате LUV
 * @author nickl
 */
public class SobelFilter implements LUVFilter
{

    private static int[][] xKernel = new int[][]
    {
        {
            -1, 0, 1
        },
        {
            -2, 0, 2
        },
        {
            -1, 0, 1
        }
    };
    private static int[][] yKernel = new int[][]
    {
        {
            1, 2, 1
        },
        {
            0, 0, 0
        },
        {
            -1, -2, -1
        }
    };
    private int height;
    private LuvData rawData;
    private int width;

    /**
     * Возвращает крту градиента для исходного изображения
     * @param rawData исходное изображение
     * @return карта градиента, представленная в формате LUV
     */
    public LuvData filter(LuvData rawData)
    {
        this.rawData = rawData;
        height = rawData.getHeight();
        width = rawData.getWidth();

        LuvData result = new LuvData(width, height);

        for (int y = 1; y < height-1; y++)
        {
            for (int x = 1; x < width-1; x++)
            {
                LUV calkx = calk(x, y, xKernel);
                LUV calky = calk(x, y, yKernel);

                double Dim = PointUtils.Dim(new LUV(), calky);
                double Dim1 = PointUtils.Dim(new LUV(), calkx);

                

                result.setLUV(x, y,  new LUV(Math.sqrt(Dim+Dim1), 0, 0));

            }
        }

        for (int y = 0; y < height; y++)
        {
            result.setLUV(0, y, new LUV());
            result.setLUV(width-1, y, new LUV());
        }

        for (int x = 1; x < width-1; x++)
        {
            result.setLUV(x, 0,new LUV());
            result.setLUV(x, height-1, new LUV());
        }

        this.rawData=null;
        this.height=0;
        this.width=0;
        return result;
    }


    private LUV calk(int x, int y, int[][] m)
    {

        LUV result = new LUV();

        for (int y0 = y - 1; y0 <= y + 1; y0++)
        {
            for (int x0 = x - 1; x0 <= x + 1; x0++)
            {
                LUV minus = rawData.getLUV(x, y).minus(rawData.getLUV(x0, y0)).mult(m[y0 - y + 1][x0 - x + 1]);

                result.incr(minus);

            }
        }

        
        return result;

    }

     private double calkl(int x, int y, int[][] m)
    {

       double result = 0;

        for (int y0 = y - 1; y0 <= y + 1; y0++)
        {
            for (int x0 = x - 1; x0 <= x + 1; x0++)
            {

                double f = (rawData.getLUV(x0, y0).l-rawData.getLUV(x, y).l)*m[y0-y+1][x0-x+1];

                result+=f;

            }
        }

        return result;
    }
   
}
