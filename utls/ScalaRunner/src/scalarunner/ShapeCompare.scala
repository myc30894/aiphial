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

package scalarunner



import me.uits.aiphial.imaging.ClustersMap

import me.uits.aiphial.imaging.ImgUtls._
import me.uits.aiphial.imaging.LuvPoint

import me.uits.aiphial.imaging.searching.shapematching.ShapeContext
import me.uits.aiphial.imaging.searching.shapematching.Matching._
import scala.collection.JavaConversions.asIterable
import scala.math._
import java.awt.{Graphics2D, Color, BasicStroke, Polygon}


object ShapeCompare {

  def main(args: Array[String]): Unit = sMatchTest((a,b)=>cyclicMatch(a,b))

  def sMatchTest(sMatch:(ShapeContext,ShapeContext )=> (Double, List[(LuvPoint, LuvPoint)])): Unit = {

    //val maxDistance = 100;
    val nnom = 80;
    val treshold = pow(0.41*nnom,2);
    val path = "../../images/contuors/";

    implicit def ScFromFile(filename: String) = {
      val cluster1 = asRegion(readImageAsLUVCollection(path + filename + ".png"))
      val orderedBoundary = ClustersMap.fromOneCluster(cluster1).getOrderedBoundary(cluster1)

      //new ShapeContext(orderedBoundary, maxDistance, nnom)
      ShapeContext(orderedBoundary, nnom)
    }

    def checkS(figa: String, fig2: String, isTrue: Boolean) = {
      val r = sMatch(figa,fig2)._1
      println("similar(" + figa + "," + fig2 + ") =" + r + " assert=" + ((r < treshold) == isTrue))

    }

    //    println("5_orig:")
    //    println("5_orig".pointContexts.mkString("\n"))
    //    println("elipse1:")
    //    println("elipse1".pointContexts.mkString("\n"))

    {
      val (m, list) = sMatch("5_orig", "5_rotsmall")
      val points = list

      println("m=" + m )

      java.awt.EventQueue.invokeLater(new Runnable {
          def run {
            val p = new Painter(50, 50, (g: Graphics2D, sx, sy) => {
                val polyg1 = new Polygon()
                val polyg2 = new Polygon()

                g.setStroke(new BasicStroke(0.2f))
                g.setColor(Color.LIGHT_GRAY)
                for ((lp, rp) <- points) {
                  val x1 = (sx * lp.getX()).toInt
                  val y1 = (sy * lp.getY()).toInt
                  val x2 = (sx * rp.getX()).toInt
                  val y2 = (sy * rp.getY()).toInt

                  polyg1.addPoint(x1, y1)
                  polyg2.addPoint(x2, y2)

                  val rad = 2
                  g.setColor(Color.RED)
                  g.drawOval(x1-rad/2,y1-rad/2,rad,rad)
                  g.setColor(Color.BLUE)
                  g.drawOval(x2-rad/2,y2-rad/2,rad,rad)
                  g.setColor(Color.BLACK)
                  g.drawLine(x1, y1, x2, y2)
                }

                //g.setColor(Color.ORANGE)
                //g.drawPolygon(polyg1)
                //g.setColor(Color.MAGENTA)
                //g.drawPolygon(polyg2)


              })
            p.setVisible(true)
          }
        })
    }

//
//    checkS("cicrle1", "elipse1", true)
//    checkS("S", "S2", true)
//    checkS("5_orig", "S2", true)
//    checkS("S2", "S2", true)
//    checkS("5_orig", "S2", true)
//    checkS("5_orig", "5_big", true)
//    checkS("5_orig", "5_rot", true)
//    checkS("5_orig", "5_1", true)
//
//    println()
//
//    checkS("5_orig", "4_1", false)
//    checkS("4_1", "S", false)
//    checkS("S", "elipse1", false)

  }


  def thresDeterm(sMatch:(ShapeContext,ShapeContext )=> (Double, List[(LuvPoint, LuvPoint)])): Unit = {

    //val maxDistance = 100;

    val path = "../../images/contuors/";
    for (nnom <- 10 to 100)
    {

      

      val treshold = 0.07*nnom;
      implicit def ScFromFile(filename: String) = {
        val cluster1 = asRegion(readImageAsLUVCollection(path + filename + ".png"))
        val orderedBoundary = ClustersMap.fromOneCluster(cluster1).getOrderedBoundary(cluster1)

        //new ShapeContext(orderedBoundary, maxDistance, nnom)
        ShapeContext(orderedBoundary, nnom)
      }

      def checkS(figa: String, fig2: String, isTrue: Boolean) = {
        val r = sMatch(figa,fig2)._1
        //println("similar(" + figa + "," + fig2 + ") =" + r + " assert=" + ((r < treshold) == isTrue))
        sqrt(r)
      }
      
      var sptimes = List[Long]()
      val c = 3
      var acc = 0d
      for (i <- 1 to c)
      {
        val starttime = System.currentTimeMillis
        val tl = List(checkS("cicrle1", "elipse1", true),
                      checkS("S", "S2", true),
                      checkS("5_orig", "S2", true),
                      checkS("S2", "S2", true),
                      checkS("5_orig", "S2", true),
                      checkS("5_orig", "5_big", true),
                      //checkS("5_orig", "5_rot", true),
                      checkS("5_orig", "5_1", true))

        val wl = List(checkS("5_orig", "4_1", false),
                      checkS("4_1", "S", false),
                      checkS("S", "elipse1", false))

        val tla = tl.sum/tl.size
        val wla = wl.sum/wl.size
        acc = acc +((tla+(wla - tla)/2)/nnom)

//            val tla = tl.max
//            val wla = wl.min
//            acc = acc +((tla+(wla - tla)/2)/nnom)
        val spenttime = System.currentTimeMillis - starttime

        sptimes = sptimes.::(spenttime)

      }

      val spenttime = (sptimes.sum - sptimes.max)/(c-1)
      
      println(nnom + " " + acc / c+" "+ spenttime)

    }

  }

  def speedTest(sMatch:(ShapeContext,ShapeContext )=> (Double, List[(LuvPoint, LuvPoint)])): Unit = {

    val maxDistance = 600;

    val path = "../../images/contuors/";
    for (nnom <- 10 to 300 by 10)
    {



      val treshold = 0.07*nnom;
      implicit def ScFromFile(filename: String) = {
        val cluster1 = asRegion(readImageAsLUVCollection(path + filename + ".png"))
        val orderedBoundary = ClustersMap.fromOneCluster(cluster1).getOrderedBoundary(cluster1)

        //new ShapeContext(orderedBoundary, maxDistance, nnom)
        ShapeContext(orderedBoundary, nnom, maxDistance )
      }

      def checkS(figa: String, fig2: String, isTrue: Boolean) = {
        val r = sMatch(figa,fig2)._1
        //println("similar(" + figa + "," + fig2 + ") =" + r + " assert=" + ((r < treshold) == isTrue))
        sqrt(r)
      }

      var sptimes = List[Long]()
      val c = 5
      for (i <- 1 to c)
      {
        val starttime = System.currentTimeMillis
        checkS("wa", "W", true)

        val spenttime = System.currentTimeMillis - starttime

        sptimes = sptimes.::(spenttime)

      }

      val spenttime = (sptimes.sum - sptimes.max)/(c-1)

      println(nnom + " "+ spenttime)

    }

  }


}
