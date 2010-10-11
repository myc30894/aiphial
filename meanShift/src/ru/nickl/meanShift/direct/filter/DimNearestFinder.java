/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.filter;

import ru.nickl.meanShift.direct.*;

/**
 *
 * @author nickl
 */
public class DimNearestFinder extends SpartialNearestFinder{

    private double colorRangeP2;

     public DimNearestFinder(LuvData LUVArray, short squareRange,double colorRange) {
        super(LUVArray);        
        super.setSquareRange(squareRange);        
        this.colorRangeP2 = colorRange*colorRange;

        super.setAdditionalCriteria(new Criteria() {

            public boolean isNear(Point a, Point b)
            {
                return PointUtils.Dim(a.c, b.c) <= colorRangeP2;
            }
        });

        
    }


}
