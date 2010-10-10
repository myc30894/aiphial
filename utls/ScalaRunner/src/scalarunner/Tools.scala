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

object Tools {



  type CC = java.util.Collection[_<:Cluster[LuvPoint]]

  implicit def ClusterToRegion(cluster: Cluster[LuvPoint]) = new Region(cluster)

  implicit def lambdaToItearationListener(funk: CC => Unit): IterationListener[LuvPoint] = {
    new IterationListener[LuvPoint]() {
      def IterationDone(a: CC) {
        funk(a)
      }
    }
  }

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


    def paintClusters(w:Int, h:Int, cc: CC):BufferedImage={
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

}
