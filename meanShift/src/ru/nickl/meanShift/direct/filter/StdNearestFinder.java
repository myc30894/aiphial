/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.*;
import ru.nickl.meanShift.*;
import java.util.ArrayList;
import java.util.List;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.Point;

/**
 *
 * @author nickl
 */
public class StdNearestFinder implements NearestFinder {

    private short squareRange = 0;
    private double colorRangeP2;

    public StdNearestFinder(LuvData LUVArray) {
        setData(LUVArray);
    }

    /*
    public StdNearestFinder(LuvData LUVArray, short squareRange) {
        this(LUVArray);
        this.squareRange = squareRange;
    }
*/
    public StdNearestFinder(LuvData LUVArray, short squareRange,double colorRange) {
        this(LUVArray);
        this.squareRange = squareRange;
        this.colorRangeP2 = colorRange*colorRange;
    }


    private LuvData LUVArray;
    private short height;
    private short width;

    public List<Point> getPointsWithinSquare(int x, int y) {
        return getPointsWithinSquare((short)x, (short)y);
    }

    public List<Point> getPointsWithinSquare(short x, short y) {
        if (squareRange == 0) {
            throw new IllegalStateException("squareRange was not set");
        }
        return getPointsWithinSquare(x, y, squareRange);
    }



    public List<Point> getPointsWithinSquare(short x, short y, short squareRange) {
        List<Point> result = new ArrayList<Point>((squareRange+1)*(squareRange+1)*4);


        for (short y0 = (short) (y - squareRange); y0 <= y + squareRange; y0++) {

            if (y0 >= 0 && y0 < height) {

                for (short x0 = (short) (x - squareRange); x0 <= x + squareRange; x0++) {
                    if (x0 >= 0 && x0 < width) {
                        result.add(new Point(x0, y0, LUVArray.getLUV(x0, y0)));
                    }
                }
            }
        }
        return result;
    }
    /**
     * {@inheritDoc }
     */
    public  List<Point> getNearest(Point old) {
        List<Point> pWSS = getPointsWithinSquare(old.x, old.y);

        List<Point> result = new ArrayList<Point>(pWSS.size());

        for (Point point : pWSS) {
            if (PointUtils.Dim(point.c, old.c) <= colorRangeP2) {
                result.add(point);
            }
        }

        return result;
    }

    /**
     * {@inheritDoc }
     */
    public void setData(LuvData LUVArray)
    {
        this.LUVArray = LUVArray;
        this.height = (short) LUVArray.getHeight();
        this.width = (short) LUVArray.getWidth();
    }

   

}
