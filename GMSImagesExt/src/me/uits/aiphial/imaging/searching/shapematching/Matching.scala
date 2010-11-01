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

/**
 * matches two shape contexts with shapes cyclic method.
 * @return tuple contains distance value and list of matched points pairs
 */
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

}