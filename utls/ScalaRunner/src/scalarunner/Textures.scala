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

import java.io.File
import javax.imageio.ImageIO
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.basic.SimpleBandwidthSelector
import me.uits.aiphial.general.dataStore.DataStore
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging._
import me.uits.aiphial.imaging.texture.Gabor
import ru.nickl.meanShift.direct.LUV
import scala.collection.JavaConversions.asScalaIterable
import scala.collection.mutable.ArrayBuffer
import me.uits.aiphial.imaging.Tools._

import scala.math.ScalaNumericConversions



object Textures {

  implicit def ftojf(v:java.lang.Float):Float = v.floatValue
  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {


    println("reading data...")

//    val gaborMatrix =  Gabor.gaborFiltres9.map(Matrix(_))
//      val gaborMatrix =  Gabor.gaborFiltres15.map(Matrix(_))
    val gaborMatrix = Gabor.gaborMatrix

//    val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../../images/DSCN4909s400.bmp")).map(_.l)
    
    //   val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../..//images/sand100.png")).map(_.l)
    val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../..//images/twotex.png")).map(_.l)

    println("finished")
    println("applying filters...")

    val appliedGabors = for(gabor <- gaborMatrix) yield imagemtx.convolve(gabor)

    val f = appliedGabors.head

    val gaborsInOne = appliedGabors.foldLeft(
      Matrix(
        Array.fill(f.height,f.width)(new ArrayBuffer[Double]())
      )
    )((o,p)=>o.join(p)(_+=_))

    println("finished")
    println("initializating clusterer...")

    class TextonePoint(val data:scala.collection.mutable.Seq[Double], val x:Int, val y:Int ) extends NDimPoint{
      
      require(!data.exists(v => v.isNaN || v.isInfinity), "cannot contain nans or inf")

      override def getCoord(i:Int) = data(i).floatValue
      override def setCoord(i:Int,v:java.lang.Float):Unit  = data(i) = v.doubleValue    
      override def getDimensions = data.length      
      override def getWeight = 1f
      override def toString():String = data.mkString("[", ",", "]")
    }


    val dataStore = DefaultDataStoreFactory.get().createDataStore[TextonePoint](appliedGabors.length)
    for((x,y,v) <- gaborsInOne)
    {
      dataStore.add(new TextonePoint(v,x,y))
    }

    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(3)
    msc0.addProgressListener(new Persentlogger())

    val amsc = new AglomerativeMeanShift[TextonePoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(0.2f)
      addIterationListener({var v = 0.2f; (a: Any) => {this.setWindowMultiplier(v); v += 0.1f}})
    }

    amsc.setDataStore(dataStore)

    amsc.addIterationListener({var s = 0;
                               (clusters:CC[TextonePoint])=>{
          println("step "+s+" finished, cluster count="+clusters.size)

          val ra = Array.ofDim[LUV](f.height,f.width)

          val colors = genRandomColors(clusters.size)

          for((cluster,i) <- clusters zipWithIndex; point <- cluster)
          {
            ra(point.x)(point.y)= colors(i)
          }

          ImageIO.write(ImgUtls.LuvArrayToBufferedImage(ra),"bmp",new File("textured_"+s+"_cc_"+clusters.size+".bmp"));
          s=s+1
        }})

    println("finished")
    println("clustering...")

    amsc.doClustering()

    println("finished")

  }

}
