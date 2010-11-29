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
import me.uits.aiphial.general.basic.BandwidthSelector
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.basic.SimpleBandwidthSelector
import me.uits.aiphial.general.dataStore.DataStore
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging._
import me.uits.aiphial.imaging.texture.Filtres
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


    println("cleaning...")

    val cf = new File(".")
    val todel = cf.listFiles(new java.io.FilenameFilter()
                 {
        override def accept(f:File,s:String)=  s.endsWith(".bmp") || s.endsWith(".js")
      })
  
    //println("removing:"+todel.map(_.getName).mkString(","))

    todel.foreach(_.delete)

    println("reading data...")

    val drawtextones = false;

    val gaborMatrix = Filtres.gaborMatrix
    //val gaborMatrix = Gabor.readfromjs("./textones/sand100_cc_6.js")

//    val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../../images/DSCN4909s400.bmp")).map(_.l)
    
    //   val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../..//images/sand100.png")).map(_.l)
    val imagemtx = Tools.matrixFromImage("../../images/twotex.png").map(_.l)
    
    println("finished")
    println("applying filters...")

    val appliedGabors = for(gabor <- gaborMatrix) yield imagemtx.wndDotProduct(gabor)

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

    def paintTextone(point: NDimPoint)={

      val pointasarray = Array.tabulate(point.getDimensions)(point.getCoord(_).floatValue)

      val n = math.sqrt(pointasarray.map(e=>e*e).sum)     

      val textoneMatrix = (for( (f,v) <- (gaborMatrix.toSeq zip pointasarray))
        yield f.map(v * _)).reduceLeft(_.join(_)(_+_)).map(_/n)

      textoneMatrix    
    }


    val dataStore = DefaultDataStoreFactory.get().createDataStore[TextonePoint](appliedGabors.length)
    for((x,y,v) <- gaborsInOne)
    {
      dataStore.add(new TextonePoint(v,x,y))
    }

    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(3)
    msc0.addProgressListener(new LinearPersentlogger())

    val amsc = new AglomerativeMeanShift[TextonePoint](msc0){
      setAutostopping(false)
      setMaxIterations(5000)
      setWindowMultiplier(1f)
      setBandwidthSelector(new BandwidthSelector()  {
              override def getBandwidth(a:DataStore[_ <:NDimPoint]):Array[java.lang.Float] = Array.fill(a.getDim)(4f);
            }

      )
      addIterationListener({var v = 2f; (a: Any) => {this.setWindowMultiplier(v); v += 1f}})
    }

    amsc.setDataStore(dataStore)

    amsc.addIterationListener({var s = 0;
                               (clusters:CC[TextonePoint])=>{
          println("step "+s+" finished, cluster count="+clusters.size)

          val ra = Array.ofDim[LUV](f.height,f.width)

          if(drawtextones && clusters.size<50)
          {

            val ab = new ArrayBuffer[Matrix[Double]](clusters.size)

            for((c,i) <- clusters zipWithIndex)
            {

              val tm = paintTextone(c.getBasinOfAttraction())

              ab.append(tm)

              val tmp = tm.map( v=> new LUV(50+50*v,0.,0.))

              ImageIO.write(matrixToImage(tmp),"bmp",new File("textone"+s+"_"+i+"_cc_"+clusters.size+".bmp"));
            }

            import java.io.FileOutputStream
            val js = new FileOutputStream("textone"+s+"_cc_"+clusters.size+".js")
            js.write(ab.map(_.toJSON).mkString("[", ",", "]").getBytes)
            js.close

          }

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
