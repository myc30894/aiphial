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

import java.io.File

import java.io.PrintWriter
import javax.imageio.ImageIO
import ru.nickl.meanShift.direct.LUV
import ru.nickl.meanShift.direct.LUVConverter
import ru.nickl.meanShift.direct.filter.FastMSFilter
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor
import ru.nickl.meanShift.direct.filter.SimpleMSFilter
import ru.nickl.meanShift.direct.segmentator.RegionGrowingSegmentator
import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift
import me.uits.aiphial.general.aglomerative.IterationListener
import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.basic.ProgressListener
import me.uits.aiphial.general.dataStore.KdTreeDataStore
import me.uits.aiphial.general.dataStore.NDimPoint

import me.uits.aiphial.imaging._

import me.uits.aiphial.imaging.searching.HistogramClusterComparer
import scala.collection.immutable.NumericRange
import scala.collection.mutable.ArrayBuffer

import me.uits.aiphial.imaging.searching.shapematching.ShapeContext
import me.uits.aiphial.imaging.searching.shapematching.ShapeContextClusterComparer
import scala.collection.JavaConversions.asScalaIterable
import scala.math._
import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Color, BasicStroke, Polygon}
import scala.runtime.RichDouble
import scala.util.Random

object Tools {



  /**
   * short name for collection of Clusters of LuvPoints
   */
  type CCLP = CC[LuvPoint]

  /**
   * short name for collection of Clusters
   */
  type CC[T <: NDimPoint] = java.util.Collection[_<:Cluster[T]]

  implicit def ClusterToRegion(cluster: Cluster[LuvPoint]) = new Region(cluster)

  implicit def lambdaToItearationListener[T <: NDimPoint](funk: CC[T] => Unit): IterationListener[T] = {
    new IterationListener[T]() {
      def IterationDone(a: CC[T]) {
        funk(a)
      }
    }
  }
  
  implicit def lambdaToProgressListener(funk: Float => Unit): ProgressListener = {
    new ProgressListener {
      def onStepDone(v:Float) = funk(v)
    }
  }

  class Persentlogger(val step:Float = 0.01f, perc:Int = 0 ) extends ProgressListener{
    private var prev = Float.NegativeInfinity

    val fs = "%2."+perc+"f"

    def onStepDone(v:Float) = {
      if(v-step>prev)
      {
        val tp = v * 100
        println(tp.formatted(fs)+"%")
        prev = v
      }
      if(v>=1f)
        {
          prev = Float.NegativeInfinity
        }
    }
  }
  
  class LinearPersentlogger(points:Int = 5, m:Int = 9) extends ProgressListener{
    require(points >=2, "points must be >=2")
    require(m >=1, "m must be >=1")

    private var prev = -1

    val step = 1./(m*(points-1))
 
    def onStepDone(v:Float)={

      if(prev == -1) {pf("0%"); prev = 1 }

      while(v/step-prev >= step)
      {

      if(prev % m == 0) pf((prev * step * 100).intValue+"%")
        else pf(".");

        prev = prev + 1;
      }

      if(v>=1f)
      {
        println("100%");
        prev = -1
      }

    }
    def pf(s:String) = {print(s); System.out.flush}
  }


  /**
   * method to scale image
   * @param img source image
   * @param k scale coefficient
   * @return scaled image
   */
  def scale(img:BufferedImage, k:Double):BufferedImage = {

    import java.awt.{Graphics2D, Image, Toolkit}
    import java.awt.image.{AreaAveragingScaleFilter,BufferedImage,FilteredImageSource,ReplicateScaleFilter}
    import javax.imageio.ImageIO


    val origh = img.getHeight();
    val origw = img.getWidth();

    val sf =new  AreaAveragingScaleFilter((origw * k).toInt, (origh * k).toInt);

    val res = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(img.getSource(), sf));

    val w = res.getWidth(null);
    val h = res.getHeight(null);
    val ty = BufferedImage.TYPE_INT_RGB;
    val dest = new BufferedImage(w, h, ty);
    val g2 = dest.createGraphics();
    g2.drawImage(res, 0, 0, null);
    g2.dispose();

    return dest
  }

  /**
   * Paints clusters on image with color for each cluster
   *  from Cluster.getBasinOfAttraction method
   *  @param w image width
   *  @param h imahe height
   *  @param cc clusters to paint
   *  @returns image painted with clusters
   */
  def paintClusters(w:Int, h:Int, cc: CCLP):BufferedImage={
    import ru.nickl.meanShift.direct.LUV
    val array = Array.ofDim[LUV](h,w)

    for(cluster <- cc){
      val cl = cluster.getBasinOfAttraction()
      val cp = new LUV(cl.getCoord(2).doubleValue,cl.getCoord(3).doubleValue,cl.getCoord(4).doubleValue)
      for (l <- cluster)
        array(l.getY)(l.getX) = cp
    }

    new LUVConverter().LUVArrayToBufferedImage(array)

  }
  /**
   * tries to figure out image format from given file name/
   * Format must be supported to write by ImageIO
   */
  def getFormatByName(filename:String):Option[String]={

    val ext = filename.drop(filename.lastIndexOf(".")+1)

    ImageIO.getWriterFormatNames().find(_.compareToIgnoreCase(ext)==0)
  }

  /**
   * Inserts index into filename before extension.
   * @param filename0 base file name
   * @param index - index to insert
   */
  def makeIndexedName(filename0:String, index:Int):String={

    val filename = filename0.drop(filename0.lastIndexOf(File.separator)+1) // new File(filename0).getName

    val ld = filename.lastIndexOf(".")

    val (name, ext) = if(ld>0)filename.splitAt(ld) else (filename,"")
    
    name + index + ext
    
  }

  /**
   * returns the time spend on computation of lambda function.
   * <code>
   * val t = measureTime{
   *  //a computation-expensive funtion
   * }
   * </code>
   */
  def measureTime(f: =>Any):Long={
    val start = System.currentTimeMillis
    f
    System.currentTimeMillis-start
  }
  /**
   * prints to stdout the time spend on computation of lambda function.
   */
  def logTime(f: =>Any):Unit={
    println("elapsed time= "+measureTime(f))

  }

  def matrixToImage(m:Matrix[LUV]) = ImgUtls.LuvArrayToBufferedImage(m.transpose.toArray)

  def matrixFromImage(filename:String) = Matrix(ImgUtls.readImageAsLuvArray(filename).transpose)

  def matrixFromImage(bufferedimage:BufferedImage) = Matrix(ImgUtls.ImageToLuvDArray(bufferedimage).transpose)

  def genRandomColors():Stream[LUV]= {
    val r = new Random()

    Stream.continually{
      new LUV(30+r.nextDouble*70,r.nextDouble*200-100,r.nextDouble*200-100)
    }
    
  }

  def genRandomColors(cn: Int):IndexedSeq[LUV] = genRandomColors().take(cn).toIndexedSeq


  def genDistinctColors(cn: Int):IndexedSeq[LUV] = {

    val lc = new LUVConverter

    val sccount = sqrt(cn).ceil.intValue

    val step:Float = 1f/sccount

    for(s <- 0 until sccount; h <- 0 until sccount)
      yield {
        val rgb = new Color(Color.HSBtoRGB(step*h, 1f, 1f-0.6f*step*s))

        

        lc.RGBtoLUV(rgb.getRed, rgb.getBlue, rgb.getGreen)}



//    val sccount = sqrt(cn).ceil.intValue
//
//    val step = 200./sccount
//
//    for(u <- 0 until sccount; v <- 0 until sccount)
//      yield  new LUV(50 + step/4 * v,-100 + step * u, 0)

  }

}
