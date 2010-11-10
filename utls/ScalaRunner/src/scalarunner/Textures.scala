/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scalarunner

import java.io.File
import javax.imageio.ImageIO
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


    //val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../../images/DSCN4909s400.bmp")).map(_.l)
    
    val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../..//images/sand100.png")).map(_.l)
    

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


    val clusterer = new MeanShiftClusterer[TextonePoint]()

    
    clusterer.setDataStore(dataStore)

    clusterer.setMinDistance(1f)

    val bs = new SimpleBandwidthSelector()
    val obw = bs.getBandwidth(dataStore)

    println("original bw="+obw.mkString("[",",","]"))

    val sbw = obw.map(v=>float2Float(v * 0.5f))

    println("chosen bw="+sbw.mkString("[",",","]"))

    clusterer.setWindow(sbw:_*)


    println("finished")
    println("clustering...")

    clusterer.doClustering()


    println("finished")
    println("writing results...")


    val clusters = clusterer.getClusters()
    
    println("number of clusters = "+clusters.size)

    val ra = Array.ofDim[LUV](f.height,f.width)

    //val ra = Array.tabulate[LUV](f.height,f.width)((x,y) => new LUV(imagemtx(x,y),0,0))

    val step = 200./clusters.size

    for((cluster,i) <- clusters zipWithIndex; point <- cluster)
    {
      ra(point.x)(point.y)= new LUV(50,-100+i*step, 100-i*step)
    }

    ImageIO.write(ImgUtls.LuvArrayToBufferedImage(ra),"bmp",new File("textured.bmp"));

    println("finished")

  }

}
