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
import me.uits.aiphial.general.basic.Utls
import me.uits.aiphial.imaging.LUV.dist2
import scala.annotation.tailrec
import scala.collection.immutable.HashSet
import scala.collection.mutable.ArrayBuffer

import scala.collection.JavaConversions.asScalaIterable
import scala.collection.JavaConversions.asJavaCollection;

object MatrixMeanShift {

  val mindistance = 0.01f

  //val logger = new PrintWriter("mms.log");


  private[this] def shiftOnePointFunction(image: Matrix[LUV],sr:Int,cr:Float, trace:((Int,Int,LUV),(Int,Int,LUV))=>Unit = (_,_)=>{}):(Int,Int,LUV)=>LUV = {

    val sr2 = 2*sr;
    val powcr = cr*cr

    @tailrec
    def shiftOnePoint(x:Int,y:Int,c:LUV, deep:Int = 1000):LUV = {

      //println("--shifting "+x+" "+y+" "+c)

      val m = image.getWithinWindow((x,y),sr2,sr2).asOneLineWithIndex
      .filter( v => dist2(c, v._3) < powcr )

      if(m.length ==0)
      {trace((x,y,c),(x,y,c)); c}
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
          csum = csum + cp
        }

        val ml = m.size

        val rx = (xsum/ml).intValue
        val ry = (ysum/ml).intValue
        val rc = csum.div(ml)

        val cc = dist2(rc, c)

        if( deep > 0 && cc > mindistance)
        {

          trace((x,y,c),(rx,ry,rc))
          shiftOnePoint(rx,ry,rc, deep-1)
        }
        else
        {
          trace((x,y,c),(x,y,c)); c
        }

      }

    }

    (x:Int,y:Int,c:LUV) => shiftOnePoint(x,y,c)
  }

  def meanshift(image: Matrix[LUV],sr:Int,cr:Float) = {

    val f = shiftOnePointFunction(image, sr, cr)

    image.mapWithIndex(f)

  }

  def fastmeanshift(image: Matrix[LUV],sr:Int,cr:Float) = {

    val alreadyShifted = Array.ofDim[LUV](image.height, image.width)

    val asr = 5// sr
    val acr = 10 // cr*cr /2

    def multishift(x:Int,y:Int,c:LUV):LUV = {
      alreadyShifted(x)(y) match {
        case null => {      

            val aggregated = new ArrayBuffer[Tuple3[Int,Int,LUV]]

            def aggregator(a:Tuple3[Int,Int,LUV],b:Tuple3[Int,Int,LUV]){
              val nearest = image.getWithinWindow((a._1,a._2),asr,asr).asOneLineWithIndex
              .filter( v => dist2(a._3, v._3) < acr )

              aggregated.appendAll(nearest)

            }

            val f = shiftOnePointFunction(image, sr, cr, aggregator)
            val r = f(x,y,c)

            for(e <- aggregated){
              alreadyShifted(e._1)(e._2) = r
            }

            r
          }
        case v:LUV => v

      }
    }

   

    image.mapWithIndex(multishift)

  }


  def regionGrowing(image:Matrix[LUV], distance:Float, minregsize:Int):Seq[Region] = {
  
    val (regions,regmap) = regionGrowingOnly(image,distance)


    val regmatrix:Matrix[Region] = Matrix(regmap)

    val frecarray = Array.fill[Int](regmatrix.height, regmatrix.width)(0)

    val (rsultregions0,regtoconsume) = regions.partition(_.size > minregsize)

    val rsultregions  = HashSet()++rsultregions0

    val unremoved = ArrayBuffer[Region]()
   
    for (region <- regtoconsume )
    {

      val allregions = region.map(p=>
        regmatrix.getWithinWindow((p.getX,p.getY),3,3).asOneLine.filter(rsultregions.contains(_))
      ).flatten


      if(allregions.isEmpty)
      {
        unremoved.append(region)
      }
      else
      {
        val (nearest,_) = allregions.groupBy(v => v).map{ case(k,v)=> (k, v.size) }
        .reduceLeft( (e1,e2)=> if(e1._2>e2._2)e1 else e2)

        nearest.addAll(region)
      }

    }

    (rsultregions++unremoved).toSeq

  }

  private[this] def regionGrowingOnly(image:Matrix[LUV], distance:Float) = {

    

    val regions = new ArrayBuffer[Region]()

    val Regmap = Array.ofDim[Region](image.height, image.width)

    @tailrec
    def growregion(region:Region, point:LuvPoint,queue:scala.collection.mutable.Queue[LuvPoint]){

      val nearest = image.getWithinWindow((point.getX, point.getY), 3,3)

      for ((x,y,c) <- nearest)
      {
        Regmap(x)(y) match{
          case null if(dist2(c, point.getLUV)<distance)=> {
              val p = new LuvPoint(x,y,c)
              region.add(p)
              queue+=(p)
              Regmap(x)(y) =  region
            }
          case _ =>
        }
      }

      if(!queue.isEmpty)
        growregion(region,queue.dequeue(),queue)
       
      
    }

    for (p <- image){
      Regmap(p._1)(p._2) match {
        case null => {
            val nr = new Region()
            regions.append(nr)
            growregion(nr,new LuvPoint(p._1,p._2,p._3),scala.collection.mutable.Queue())
          }
        case _ => 
      }
    }



    //regions.foreach(r=> r.setBasinOfAttraction(Utls.getAvragePoint(r)))

    (regions,Regmap)
  }


}


import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.general.basic.Clusterer
import me.uits.aiphial.general.dataStore.DataStore

abstract class MatrixMeansShiftSegmentatorAdapter(m:Matrix[LUV]) extends Clusterer[LuvPoint]
{

  var sr:Short = 20

  var cr = 7f

  var range = 1f

  var minsize = 0

  protected val msfunction: (Matrix[LUV],Int,Float) => Matrix[LUV]

  protected val aggregator: (Matrix[LUV],Float,Int) => Seq[_ <: Cluster[LuvPoint]] = MatrixMeanShift.regionGrowing _

  private[this] var result:java.util.List[_ <: Cluster[LuvPoint]] = new java.util.ArrayList[Cluster[LuvPoint]](0)

  override def doClustering() = {

    import scala.collection.JavaConversions.asJavaList

    val r0 = msfunction(m,sr,cr)


    //println(r0)

    result = aggregator(r0, range,minsize)
    
  }

  override def getClusters():java.util.List[_ <: Cluster[LuvPoint]] = result;
  
  override def setDataStore(dataStore:DataStore[_ <: LuvPoint]) = {
    //setting datastore is not implemented and do nothing, because segmentator has its own data
  }
  
  def setColorRange(a:Float) = cr = a
  def setSquareRange(a:Short) = sr = a
  def setMinRegionSize(minreg:Int) = minsize = minreg

}

class FastMatrixMS(m:Matrix[LUV]) extends MatrixMeansShiftSegmentatorAdapter(m)
{
  protected val msfunction = MatrixMeanShift.fastmeanshift _
}

class MatrixMS(m:Matrix[LUV]) extends MatrixMeansShiftSegmentatorAdapter(m)
{
  protected val msfunction = MatrixMeanShift.meanshift _
}

