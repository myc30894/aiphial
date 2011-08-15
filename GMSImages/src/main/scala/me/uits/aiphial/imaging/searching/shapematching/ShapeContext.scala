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

package me.uits.aiphial.imaging.searching.shapematching

import me.uits.aiphial.general.basic.Utls
import me.uits.aiphial.imaging.boundary.CircleList
import me.uits.aiphial.imaging.boundary.Contour
import me.uits.aiphial.imaging.LuvPoint
import scala.collection.mutable.ArrayBuffer
import scala.math._


import scala.collection.JavaConversions.asScalaIterable
import scala.collection.JavaConversions.asJavaIterable
//import java.lang.{Iterable => JavaItb}
//import java.util.{Iterator => JavaItr}

/**
 * class that representates shape context for given contuor.
 * Shape context is usable to compare contuor shapes
 */
class ShapeContext protected(points: Contour, val maxDistance: Float) {
  import ShapeContext._

  val RBinCount = 5;
  val TetaBinCount = 12;
  val maxTeta = 2 * Pi;

  val pointContexts = new CircleList[PointContext](points map {p => new PContext(p)})
 

  private class PContext(val point: LuvPoint) extends PointContext {
    val array = Array.ofDim[Int](RBinCount, TetaBinCount)

    val iterator = ShapeContext.this.points.iterator.asInstanceOf[ShapeContext.this.points.CircleListIterator]

    //val iterator =  ShapeContext.this.points.iterator

    while(iterator.hasNext)
    {
      val p = iterator.getCurrent
      val (r, teta) = toPolar(point.getX() - p.getX(), point.getY() - p.getY())

      val ta = tangentAngle(iterator)

      var cteta =  teta - ta
      if(cteta<0) cteta+= 2*Pi
      if(cteta>=2*Pi) cteta-= 2*Pi

//              println("maxdistance="+maxDistance)
//              println("r="+r)
//              println("cteta="+cteta)
//              println("x="+(r*RBinCount/maxDistance).toInt)
//              println("y="+((teta/maxTeta)*TetaBinCount).toInt)



        array(
          (log(r + 1) * RBinCount / log(maxDistance + 2)).toInt)(
          ((cteta / maxTeta) * TetaBinCount).toInt) += 1

      iterator.next
    }

    def toPolar(x: Int, y: Int) = {

      val r = sqrt(x * x + y * y)
      val teta = if (r==0) 0D
      else if(x>=0) asin(y/r)
      else -asin(y/r)+Pi

      (r, teta + Pi / 2)}

    def tangentAngle(iterator: ShapeContext.this.points.CircleListIterator):Double={
     val next:LuvPoint = iterator.getNext
     val prev:LuvPoint = iterator.getPrev

      toPolar(next.getX-prev.getX,next.getY-prev.getY)._2
  }


    override def reduceBins(apoint: PointContext, op: (Int, Int) => Double): Double = {

      var sum = 0D;
      for (i <- 0 until RBinCount; j <- 0 until TetaBinCount) {
        val r = op(this.array(i)(j), apoint.array(i)(j))
        sum += r
      }
      sum;
    }


    override def toString() = {
      val builder = new StringBuilder();
      for (str <- array)
        {
          for (s <- str)
            {
              if (s != 0) builder.append(s.formatted("%3d")) else builder.append("   ")
              builder.append("  |")
            }
          builder.append("\n")

        }
      builder.toString
    }

  }


  //def cyclicMatch(asc: ShapeContext) = ShapeContext.HungarianMatch(this, asc)
//  def cyclicMatch(asc: ShapeContext) = ShapeContext.euclideanMinMatch(this, asc)
//  def hungarianMatch(asc: ShapeContext) = ShapeContext.HungarianMatch(this, asc)

}


trait PointContext {
  def array: Array[Array[Int]]

  /**
   * general funtion to caluclare reduce funtion for
   * shape contexts histogram beans
   */
  def reduceBins(apoint: PointContext, op: (Int, Int) => Double): Double

  /**
   * calculates euclidian distance between
   * shape contexts histogram beans
   */
  def euclideanReduce(b1: PointContext) = math.sqrt(this.reduceBins(b1, {(a: Int, b: Int) => (a - b) * (a - b)}))

  /**
   * calculates hi square distance between
   * shape contexts histogram beans
   */
  def hiSqTestReduce(b1: PointContext) = 0.5*(this.reduceBins(b1, {
    (a: Int, b: Int) =>
      if(a!=0 && b!=0)
        ((a - b) * (a - b)).toDouble/(a+b)
      else 0
  }))

  /**
   * returns the point for which this context is calculated
   */
  def point: LuvPoint
}




object ShapeContext {
  /**
   * creates Shape Context for given contuor with given historgam bins number
   * max distance will be calculated automaticly
   */
  def apply(points: Contour, num: Int):ShapeContext = {
    val norm = ShapeContext.normalizeTo(points, num)
    new ShapeContext(norm, ShapeContext.getMaxDistance(norm))
  }

  /**
   * creates Shape Context for given contuor with given historgam bins number and
   * precalculated max distance for better performance
   */
   def apply(points: Contour, num: Int, maxDistance: Float):ShapeContext = {
    new ShapeContext (ShapeContext.normalizeTo(points, num), maxDistance)
  }
  
  private def normalizeTo(col: Contour, num: Int): Contour = {
    //require(col.size >= num, "unnormalizable")
    val d = col.size.toFloat / num;

    //    println("start---------")
    //    println("col=",col.size)
    //    println("num=",num)
    //    println("d=",d)

    val result = Array.ofDim[LuvPoint](num)

    val iterator = col.iterator
    var i = 0;
    var toget = 0f
    while (iterator.hasNext)
      {
        val v = iterator.next
        if (i >= toget)
          {
            val fl: Float = (toget * num) / col.size
            //
            //            print("i=",i)
            //            println(" fl=",fl)

            result(fl.round) = v
            toget = toget + d
          }
        i = i + 1
      }

    //    println("finish---------")

    assert(result.forall(_!=null), "unnormalizable")

    new Contour(result.toIterable)
  }

  def getMaxDistance(col: Iterable[LuvPoint]) = {for (i <- col; j <- col) yield Utls.distance(i, j)}.max
}

