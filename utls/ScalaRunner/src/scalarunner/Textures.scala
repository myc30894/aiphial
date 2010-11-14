/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import scalarunner.Tools._

import scala.math.ScalaNumericConversions



object Textures {

  implicit def ftojf(v:java.lang.Float):Float = v.floatValue
  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {


    println("reading data...")

    val gaborMatrix = Gabor.garborFiltres.map(Matrix(_))

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
      override def getCoord(i:Int) = data(i).floatValue      
      override def setCoord(i:Int,v:java.lang.Float):Unit  = data(i) = v.doubleValue    
      override def getDimensions = data.length      
      override def getWeight = 1f      
    }


    val dataStore = DefaultDataStoreFactory.get().createDataStore[TextonePoint](appliedGabors.length)
    for((x,y,v) <- gaborsInOne)
    {
      dataStore.add(new TextonePoint(v,x,y))
    }

    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(3)

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

//          val step = 200./clusters.size
//          for((cluster,i) <- clusters zipWithIndex; point <- cluster)
//          {
//            ra(point.x)(point.y)= new LUV(50,-100+i*step, 100-i*step)
//          }
          ImageIO.write(ImgUtls.LuvArrayToBufferedImage(ra),"bmp",new File("textured_"+s+"_cc_"+clusters.size+".bmp"));
          s=s+1
        }})

    println("finished")
    println("clustering...")

    amsc.doClustering()

    println("finished")

  }

}
