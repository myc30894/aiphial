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

import java.io.PrintWriter
import javax.imageio.ImageIO

import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift
import me.uits.aiphial.general.aglomerative.IterationListener
import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory
import me.uits.aiphial.general.dataStore.KdTreeDataStore
import me.uits.aiphial.general.dataStore.KdTreeDataStoreFactory
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.ImgUtls._
import me.uits.aiphial.imaging.LuvDataStore
import me.uits.aiphial.imaging.LuvPoint

import me.uits.aiphial.imaging.MatrixMS
import me.uits.aiphial.imaging.Region
import me.uits.aiphial.imaging.searching.HistogramClusterComparer
import scala.collection.mutable.ArrayBuffer

import me.uits.aiphial.imaging.searching.shapematching.ShapeContext
import me.uits.aiphial.imaging.searching.shapematching.ShapeContextClusterComparer
import scala.collection.JavaConversions.asScalaIterable
import scala.math._
import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Color, BasicStroke, Polygon}

import me.uits.aiphial.imaging.Tools._
/**
 * Created by IntelliJ IDEA.
 * User: Nickl
 * Date: 11.12.2009
 * Time: 22:26:20
 * To change this template use File | Settings | File Templates.
 */

import me.uits.aiphial.imaging.ImgUtls._

object Search{


  val nnom = 80

  /**
   * creates shape context for region represented by image with transparate non-region points
   */
  def ScFromImage(filename: BufferedImage) = {
    val cluster1 = asRegion(luvDArraytoLuvPoints(ImageToLuvDArray(filename)))
    val orderedBoundary = cluster1.getContour

    //new ShapeContext(orderedBoundary, maxDistance, nnom)
    ShapeContext(orderedBoundary, nnom)
  }







  def main(args: Array[String]): Unit = {

    

    DefaultDataStoreFactory.setPrototype(new KdTreeDataStoreFactory())

    new File("match").mkdir;

    val startTime = System.currentTimeMillis

    //val srcimg = ImageIO.read(new File("../../images/smallgisto.jpg"))
    val srcimg = ImageIO.read(new File("../../images/horse.png"))
    //val srcimg = ImageIO.read(new File("/home/nickl/biotecnical/Диссертация/work/data/язык/1243495680.jpg"))
    //val imgtosearch = ImageIO.read(new File("../../images/bluehren.png"))
    val imgtosearch = ImageIO.read(new File("../../images/horseclust.png"))

    val h = srcimg.getHeight();
    val w = srcimg.getWidth();

    val msc = new AglomerativeClustererStack[LuvPoint]();




    val srcmt =matrixFromImage(srcimg);
    val ifilter = new MatrixMS(srcmt){
      setColorRange(7)
      setSquareRange(2)
    }



    

    msc.setInitialClusterer(ifilter);

    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(3)

    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(0.2f)
      setWindowMultiplierStep(0.1f)
    }

    msc.addExtendingClustererToQueue(amsc)

    //  val msc = amsc;

    // val datastore = new KdTreeDataStore[LuvPoint](5)
    // imageAsLUVPointCollection(srcimg).foreach(datastore.add(_))

    // val datastore = new LuvDataStore(ImageToLuvDArray(srcimg))

    //msc.setDataStore(datastore)
  

    msc.addIterationListener({
        var t = System.currentTimeMillis()
        var i = 0
        (a: CCLP) => {
          println(a.size + " " + (System.currentTimeMillis() - t))
          ImageIO.write(paintClusters(w,h,a), "png", new File("out" + i + ".png"))
          t = System.currentTimeMillis()
          i = i + 1
        }
      })





     val vals = ArrayBuffer[Double]()
     amsc.addIterationListener({
       val cc = new HistogramClusterComparer()
       cc.setPattern(imgtosearch)
       val sc = new ShapeContextClusterComparer()
       sc.setPattern(imgtosearch)


       var i2 = 0

       (a: CCLP) => {
         for (cluster <- a) {
           val v = cc.compareCluster(cluster)
           //vals.append(v)
           if(0 <= v && v < 190){
           val sv = sc.compareCluster(cluster)
           vals.append(sv)
           if (sv < 2000)
             {
               val img = ImgUtls.getClusterImage(srcimg, cluster)
               if (img != null)
                 {
                   ImageIO.write(img, "png", new File("match/match_" + i2 + "_" + v +"_"+sv + ".png"))
                 }
             }
           }
           i2 = i2 + 1
         }
       }
     })




    println("inittime: " + (System.currentTimeMillis - startTime))

    msc.doClustering()

    println(vals.sortWith(_>_).mkString("[",",","]"))

  }
 


}