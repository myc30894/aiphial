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

package scalarunner

import java.io.File

import java.io.PrintWriter
import javax.imageio.ImageIO
import ru.nickl.meanShift.direct.MeanShiftImageProcessor
import ru.nickl.meanShift.direct.filter.FastMSFilter
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor
import ru.nickl.meanShift.direct.filter.SimpleMSFilter
import ru.nickl.meanShift.direct.segmentator.RegionGrowingSegmentator
import ru.nickl.meanshift.direct.cuda.NativeCudaMSFilter
import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift
import me.uits.aiphial.general.aglomerative.IterationListener
import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.dataStore.KdTreeDataStore
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
import scala.runtime.RichDouble

import Tools._

object Segmentation {

  
  def main(args: Array[String]): Unit = {

   

    val startTime = System.currentTimeMillis
    
    val srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"))
    //val srcimg = ImageIO.read(new File("../../images/Gysto/helmcells400.bmp"))
    //val srcimg = ImageIO.read(new File("../../images/DSC00104s400.bmp"))
    //val imgtosearch = ImageIO.read(new File("../../images/bluehren.png"))

    val h = srcimg.getHeight();
    val w = srcimg.getWidth();    
    
    val a = new FastMSFilter(){
          setDiffColor(10)
          setDiffSquare(3)
    }
//    val a = new NativeCudaMSFilter()


    val t = 2


    /*for( cr <- new RichDouble(2.5).until(2.6).by(0.1))*/{
      /*for (sr <- 7 until 8)*/{
        for (k <- 0.2 until 1 by 0.1){

          var msp:MeanShiftImageProcessor= null;

          var l = List[Long]()
          for(i <- 1 to t){
            val start = System.currentTimeMillis

            val scaledimg = scale(srcimg, k)
            a.setColorRange(7f)
            a.setSquareRange(20)

            msp = new MeanShiftFilterImageProcessor(a)

            msp.setSourceImage(scaledimg)

            msp.process()
            val elapsed = System.currentTimeMillis-start
            //println("elapsed= "+elapsed)
            l = elapsed ::l

            //scala.immutable.HashMap
          }
      
          val avetime = (l.sum -l.max) / (l.length - 1)

          println(/*"cr= "+cr+" sr= "+sr+*//*" k= "+k+" time= "+*/avetime)

          ImageIO.write(msp.getProcessedImage(), "bmp", new File("./out_"+k+"_"+msp.getColorRange+"_"+msp.getSquareRange+".bmp"))
        }
      }
    }     
    
  }


}
