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
import ru.nickl.meanShift.direct.filter.FastMSFilter
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor
import ru.nickl.meanShift.direct.filter.SimpleMSFilter
import ru.nickl.meanShift.direct.segmentator.RegionGrowingSegmentator
import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift
import me.uits.aiphial.general.aglomerative.IterationListener
import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.dataStore.KdTreeDataStore
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.ImgUtls._
import me.uits.aiphial.imaging.LuvDataStore
import me.uits.aiphial.imaging.LuvPoint

import me.uits.aiphial.imaging.Matrix
import me.uits.aiphial.imaging.Region
import me.uits.aiphial.imaging.SegmentatorAdapter
import me.uits.aiphial.imaging.searching.HistogramClusterComparer
import scala.collection.mutable.ArrayBuffer

import me.uits.aiphial.imaging.searching.shapematching.ShapeContext
import me.uits.aiphial.imaging.searching.shapematching.ShapeContextClusterComparer
import scala.collection.JavaConversions.asScalaIterable
import scala.math._
import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Color, BasicStroke, Polygon}
import scala.runtime.RichDouble


import me.uits.aiphial.imaging.Tools._

object AgloSpeedTest {


  def main(args: Array[String]): Unit = {


    val srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"))
    //val srcimg = ImageIO.read(new File("../../images/Gysto/helmcells400.bmp"))
    //val srcimg = ImageIO.read(new File("../../images/DSC00104s400.bmp"))
    //val srcimg = ImageIO.read(new File("../../images/smallgisto.jpg"))


    val t = 2


    for (k <- 0.2 until 1.1 by 0.1){

      var l = List[Long]()
      for(i <- 1 to t){


        val scaledimg = scale(srcimg, k)

        //ImageIO.write(scaledimg, "jpg",new File("../../images/DSC00104s400_k"+k+".jpg"))

        val msp = createSegmentatorForImage(scaledimg)

        //val msp = createPureAglomerativeSegmentator(scaledimg)

        msp.addIterationListener({var s = 0;
                                  (a:CCLP)=>{
            ImageIO.write(
              paintClusters(scaledimg.getWidth, scaledimg.getHeight, a),
              "bmp",
              new File("../out_"+i+"_"+s+".bmp"))
            s=s+1
          }})

        val start = System.currentTimeMillis
        msp.doClustering()
        val elapsed = System.currentTimeMillis-start
        //println("elapsed= "+elapsed)
        l = elapsed ::l

        //scala.immutable.HashMap
      }

      val avetime = (l.sum -l.max) / (l.length - 1)

      println(/*"cr= "+cr+" sr= "+sr+*//*" k= "+k+" time= "+*/avetime)

      //ImageIO.write(msp.getProcessedImage(), "bmp", new File("/home/nickl/out_"+cr+"_"+sr+".bmp"))
    }


  }


  
  def createPureAglomerativeSegmentator(srcimg:BufferedImage) = {
    
    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(3)
    
    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(0.2f)
      addIterationListener({var v = 0.2f; (a: CCLP) => {this.setWindowMultiplier(v); v += 0.1f}})
    }


    val datastore = new KdTreeDataStore[LuvPoint](5)
    imageAsLUVPointCollection(srcimg).foreach(datastore.add(_))

    //val datastore = new LuvDataStore(ImageToLuvDArray(srcimg))

    amsc.setDataStore(datastore)    
    amsc      
  }
   

  def createSegmentatorForImage(srcimg:BufferedImage) = {

    

    val msc = new AglomerativeClustererStack[LuvPoint]();

//    val growingSegmentator = new RegionGrowingSegmentator(){
//      setEqualityRange(10)
//      setSourceImage(srcimg)
//    }
//     msc.setInitialClusterer(new SegmentatorAdapter(growingSegmentator))


    //val ifilter = new NativeCudaMSFilter{
//    val ifilter = new SimpleMSFilter{
//      setColorRange(7)
//      setSquareRange(2)
//    }
//
//    val is  = new ru.nickl.meanShift.direct.segmentator.SimpleSegmentator(ifilter){
//      setMinRegionSize(0)
//    }
//
//
//    is.setSourceImage(srcimg)
//
//    msc.setInitialClusterer(new SegmentatorAdapter(is));



     val srcmt = Matrix(ImgUtls.ImageToLuvDArray(srcimg));

    import me.uits.aiphial.imaging.FastMatrixMS

    val is = new FastMatrixMS(srcmt)
    {
       setColorRange(7)
       setSquareRange(2)
    }

    msc.setInitialClusterer(is);

    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(3)

    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(0.2f)
      addIterationListener({var v = 0.2f; (a: CCLP) => {this.setWindowMultiplier(v); v += 0.1f}})
    }

    msc.addExtendingClustererToQueue(amsc)

//
//    msc.addIterationListener(new Searcher(ImageIO.read(new File("../../images/bluehren.png")), srcimg))
//

    msc
  }
  
 
}
