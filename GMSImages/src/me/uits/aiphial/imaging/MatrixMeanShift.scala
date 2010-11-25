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

    val sr2 = 2*sr;
    val powcr = cr*cr

    @tailrec
    def shiftOnePoint(x:Int,y:Int,c:LUV):LUV = {

      //println("--shifting "+x+" "+y+" "+c)

      val m = image.getWithinWindow((x,y),sr2,sr2).asOneLineWithIndex
      .filter( v => PointUtils.Dim(c, v._3) < powcr )

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

        val rx = (xsum/ml).intValue
        val ry = (ysum/ml).intValue
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
  
    image.mapWithIndex(shiftOnePoint)

  }

}
