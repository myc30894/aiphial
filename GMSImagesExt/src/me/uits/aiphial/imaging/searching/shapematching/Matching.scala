/*
 *
 * This file is part of Aiphial.
 *
 * Copyright (c) 2010 Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package me.uits.aiphial.imaging.searching.shapematching

import me.uits.aiphial.imaging.LuvPoint
import collection.mutable.ArrayBuffer

import scala.collection.JavaConversions.asIterable
/**
 * Created by IntelliJ IDEA.
 * User: Nickl
 * Date: 12.12.2009
 * Time: 16:46:41
 * To change this template use File | Settings | File Templates.
 */

object Matching {


  def cyclicMatch(t: ShapeContext, asc: ShapeContext): (Double, List[(LuvPoint, LuvPoint)]) = {

    import scala.collection.mutable.HashMap
    require(t.pointContexts.size == asc.pointContexts.size, "incompatible sizes")
    require(t.RBinCount == asc.RBinCount, "incompatible .RBinCount")
    require(t.TetaBinCount == asc.TetaBinCount, "incompatible .RBinCount")
    //require(this.maxDistance==asc.maxDistance, "incompatible maxDistance")

    val map: HashMap[Double, List[(LuvPoint, LuvPoint)]] = new HashMap

    for (stp <- t.pointContexts) {
      var sum = 0D
      val it = t.pointContexts.iterator(stp)
      val ait = asc.pointContexts.iterator

      val localmap = new ArrayBuffer[(LuvPoint, LuvPoint)](asc.pointContexts.size)

      while (it.hasNext)
        {
          val a = it.next
          val b = ait.next
          localmap.append((a.point, b.point))
          sum += a euclideanReduce b;
        }

      map.put(sum, localmap.toList)
    }

    val minkey = map.keySet.min
    (minkey, map.get(minkey).get)
  }

  def hungarianMatch(a: ShapeContext, b: ShapeContext): (Double, List[(LuvPoint, LuvPoint)]) = {


    val arr1:Array[PointContext] = a.pointContexts.toArray[PointContext](Array.ofDim[PointContext](a.pointContexts.size))
    val arr2:Array[PointContext] = b.pointContexts.toArray[PointContext](Array.ofDim[PointContext](b.pointContexts.size))

    val costMatrix = Array.ofDim[Float](arr1.length, arr2.length)

    for ((ai, i) <- arr1.zipWithIndex) {
      for ((bj, j) <- arr2.zipWithIndex) {
         costMatrix(i)(j) = (ai euclideanReduce bj).toFloat
      }
    }

    import assignment._

    val ap = new AssignmentProblem(costMatrix).solve(new HungarianAlgorithm)

//   for(line <- ap){
//     println(line.mkString("[",",","]"))
//   }
//


   val ab = new ArrayBuffer[(LuvPoint, LuvPoint)](arr1.size)
   var sum = 0f
   for(line <- ap){

     val e1 = line(0)
     val e2 = line(1)

     ab+=((arr1(e1).point,arr2(e2).point))
     sum+=costMatrix(e1)(e2)
   }

    //todo complete it

    return (sum,ab.toList)
  }





}