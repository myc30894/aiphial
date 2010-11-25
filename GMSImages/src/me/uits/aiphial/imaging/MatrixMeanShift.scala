/*
 *
 * This file is part of Aiphial.
 *
 * Copyright (c) 2010 Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.uits.aiphial.imaging

import java.io.PrintWriter
import ru.nickl.meanShift.direct.LUV
import ru.nickl.meanShift.direct.PointUtils
import scala.annotation.tailrec

object MatrixMeanShift {  

  val mindistance = 0.01f

  val logger = new PrintWriter("mms.log");

  def meanshift(image: Matrix[LUV],sr:Int,cr:Float) = {


    @tailrec
    def shiftOnePoint(x:Int,y:Int,c:LUV):LUV = {

      //println("--shifting "+x+" "+y+" "+c)

      val m = image.getWithinWindow((x,y),2*sr,2*sr).asOneLineWithIndex
      .filter( v => PointUtils.Dim(c, v._3) < cr*cr )

      if(m.length ==0)
        c
      else
      {
        // TODO: convert to funtional style after unifiing color presentation
        var xsum = 0f;
        var ysum = 0f;
        var csum = new LUV(0f,0f,0f)

        for ((xp,yp,cp) <- m)
        {
          xsum +=xp;
          ysum +=yp;
          csum.incr(cp)
        }

        val ml = m.size

        val rx = (xsum/ml).round;
        val ry = (ysum/ml).round
        val rc = csum.div(ml)

        val cx = rx - x;
        val cy = ry - y;
        val cc = PointUtils.Dim(rc, c)


        if(cx*cx+cy*cy+cc*cc < mindistance)
          c
        else
        {
          shiftOnePoint(rx,ry,rc)
        }

      }

    }
      
        
    def shiftOnePoint0(x:Int,y:Int,c:LUV):LUV = {
    
      import ru.nickl.meanShift.direct.Point


      def calkMh(old:Point):Point =
      {
        val nearest = image.getWithinWindow((old.y,old.x),2*sr,2*sr).asOneLineWithIndex
        .filter( ep => PointUtils.Dim(old.c, ep._3) < cr*cr ).map(v => new Point(v._2.shortValue,v._1.shortValue,v._3))

        import scala.collection.JavaConversions.asScalaIterable

        //val nearest0 = nearestFinder.getNearest(old);


//        logger.println("ss ("+nearest.size+") ="+(nearest.sortBy(v => v.y*1000+v.x)))
//        logger.println("nf ("+nearest0.size+") ="+nearest0.toSeq.sortBy(v => v.y*1000+v.x))
//        logger.println



        val mean = new Point(0, 0, new LUV(0, 0, 0));

        for (point <- nearest)
        {
          mean.incrBy(point.minus(old));
  }

        if (nearest.size != 0)
        {
          mean.divide(nearest.size);
}

        mean;
      }


      val nulllUV = new LUV(0.0F, 0.0F, 0.0F);

      val curPossition = new Point(y.shortValue, x.shortValue, c.clone());

      logger.println("was="+curPossition)

      var Mh:Point = null;
      var n = 1000;
      do
      {
        Mh = calkMh(curPossition);
        curPossition.incrBy(Mh);
        n = n -1;
      } while (PointUtils.Dim(Mh.c, nulllUV) > 0.01f && n > 0);

      logger.println("now="+curPossition)

      curPossition.c
    }



    image.mapWithIndex(shiftOnePoint)


  }

}
