/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct;

/**
 * Класс, объекты которого храянят пиксели изображения в формате LUV
 * @author nickl
 */
public class LuvData {

    private LUV[][] dataArray;
    private int height;
    private int width;

    protected  LuvData()
    {
    }

    public LuvData(LUV[][] dataArray)
    {
        this.dataArray = dataArray;
        height = dataArray.length;
        width = dataArray[0].length;
    }

    public LuvData(int w, int h)
    {
        height = h;
        width = w;
        dataArray = new LUV[height][width];
    }

    public LUV getLUV(int x, int y)
    {
        return dataArray[y][x];
    }

    public void setLUV(int x,int y, LUV luv)
    {
        dataArray[y][x] = luv;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    /**
     * Возвращает изображение в виде массива LUV-цветов, координата y- строки,
     * кооридната x - столбцы.
     *
     * <i>не стоити активно пользоваться этим методом так
     * как нет гарантии что он будет поддерживаться в дальнейшем</i>
     * @return изображение в виде массива LUV-цветов,
     */
    public LUV[][] getLUVArray()
    {
        return dataArray;
    }
    

}
