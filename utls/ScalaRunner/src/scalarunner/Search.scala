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

package scalarunner

import java.io.File

import java.io.PrintWriter
import javax.imageio.ImageIO

import ru.nickl.meanShift.direct.LUVConverter
import ru.nickl.meanShift.direct.filter.SimpleMSFilter
import ru.nickl.meanShift.direct.segmentator.RegionGrowingSegmentator
import ru.nickl.meanshift.direct.cuda.NativeCudaMSFilter
import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift
import me.uits.aiphial.general.aglomerative.IterationListener
import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory
import me.uits.aiphial.general.dataStore.KdTreeDataStore
import me.uits.aiphial.general.dataStore.KdTreeDataStoreFactory
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging.ClustersMap
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.ImgUtls._
import me.uits.aiphial.imaging.LuvDataStore
import me.uits.aiphial.imaging.LuvPoint

import me.uits.aiphial.imaging.Region
import me.uits.aiphial.imaging.SegmentatorAdapter
import me.uits.aiphial.imaging.searching.HistogramClusterComparer
import scala.collection.mutable.ArrayBuffer

import me.uits.aiphial.imaging.searching.shapematching.ShapeContext
import me.uits.aiphial.imaging.searching.shapematching.ShapeContextClusterComparer
import scala.collection.JavaConversions.asIterable
import scala.math._
import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Color, BasicStroke, Polygon}

import Tools._
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

  def ScFromImage(filename: BufferedImage) = {
    val cluster1 = asRegion(luvDArraytoLuvPoints(ImageToLuvDArray(filename)))
    val orderedBoundary = ClustersMap.fromOneCluster(cluster1).getOrderedBoundary(cluster1)

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

//    val growingSegmentator = new RegionGrowingSegmentator(){
//      setEqualityRange(10)
//      setSourceImage(srcimg)
//    }
//     msc.setInitialClusterer(new SegmentatorAdapter(growingSegmentator))


    //val ifilter = new NativeCudaMSFilter{
    val ifilter = new SimpleMSFilter{
      setColorRange(7)
      setSquareRange(2)
    }

    val is  = new ru.nickl.meanShift.direct.segmentator.SimpleSegmentator(ifilter){
      setMinRegionSize(0)
    }

//
//     val is = new ru.nickl.meanShift.direct.segmentator.SobelMeanShiftSegmentator(ifilter){
//       setGradTreshold(5)
//       setEqualityRange(1)
//       setColorRange(7)
//       setSquareRange(2)
//       setMinRegionSize(0)
//     }
//


    is.setSourceImage(srcimg)

    msc.setInitialClusterer(new SegmentatorAdapter(is));

    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(3)

    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(0.2f)
      addIterationListener({var v = 0.2f; (a: CC) => {this.setWindowMultiplier(v); v += 0.1f}})
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
        (a: CC) => {
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

       (a: CC) => {
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








  // <editor-fold defaultstate="collapsed" desc="old">

  def main0(args: Array[String]): Unit = {

    type CC = java.util.Collection[_<:Cluster[LuvPoint]]

    implicit def ClusterToRegion(cluster: Cluster[LuvPoint]) = new Region(cluster)

    implicit def lambdaToItearationListener(funk: CC => Unit): IterationListener[LuvPoint] = {
      new IterationListener[LuvPoint]() {
        def IterationDone(a: CC) {
          funk(a)
        }
      }
    }

    val startTime = System.currentTimeMillis

    val srcimg = ImageIO.read(new File("../../images/smallgisto.jpg"))
    val imgtosearch = ImageIO.read(new File("../../images/bluehren.png"))

    val h = srcimg.getHeight();
    val w = srcimg.getWidth();


    val growingSegmentator = new RegionGrowingSegmentator();
    growingSegmentator.setEqualityRange(10)
    growingSegmentator.setSourceImage(srcimg);



    val msc = new AglomerativeClustererStack[LuvPoint]();
    msc.setInitialClusterer(new SegmentatorAdapter(growingSegmentator));


    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(3)

    val amsc = new AglomerativeMeanShift[LuvPoint](msc0)

    amsc.setAutostopping(false)
    amsc.setMaxIterations(1000)
    amsc.setWindowMultiplier(0.5f)

    msc.addExtendingClustererToQueue(amsc)

    amsc.addIterationListener({var v = 0.5f; (a: CC) => {amsc.setWindowMultiplier(v); v += 0.1f}})

    amsc.addIterationListener({
        var t = System.currentTimeMillis()
        var i = 0
        (a: CC) => {
          println(a.size + " " + (System.currentTimeMillis() - t))
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

        (a: CC) => {
          for (cluster <- a) {
            val v = cc.compareCluster(cluster)
            val sv = sc.compareCluster(cluster)
            vals.append(sv)
            if (0 <= v && v < 190 && sv < 2000)
            {
              val img = ImgUtls.getClusterImage(srcimg, cluster)
              if (img != null)
              {
                ImageIO.write(img, "png", new File("match/match_" + i2 + "_" + v +"_"+sv + ".png"))
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

// </editor-fold>


}