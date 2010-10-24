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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ru.nickl.meanshift.kmeans

import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.general.basic.Clusterer
import me.uits.aiphial.general.basic.Utls
import me.uits.aiphial.general.dataStore.DataStore
import me.uits.aiphial.general.dataStore.NDimPoint

import scala.collection.JavaConversions.asIterable
import scala.util.Random

@deprecated("not implemented")
class Kmeans(val k:Int = 3) extends Clusterer[NDimPoint] {


  private[this] var ds:DataStore[_<:NDimPoint] = null

  private[this] var clusters = Array.ofDim[Cluster[NDimPoint]](k)


  def doClustering()={

    val data =  ds.asList
    val size = data.size

    for(i <- clusters.indices)
    {
      clusters(i) = new Cluster(data.get(Random.nextInt(size)))
    }


    for(elem <- data){

      val dstns = (for(cluster <- clusters) yield (cluster, Utls.distance(elem, cluster.getBasinOfAttraction)))


      val (nearest, dst) = dstns min Ordering.fromLessThan[(_,Float)](_._2 < _._2)
     
      nearest.add(elem)


    }


    throw new UnsupportedOperationException("Not implemented yet")

  }

  def setDataStore(ds: DataStore[_<:NDimPoint])= this.ds=(ds)


  def getClusters():java.util.List[Cluster[NDimPoint]] = {

    throw new UnsupportedOperationException("Not implemented yet")
  }


}


