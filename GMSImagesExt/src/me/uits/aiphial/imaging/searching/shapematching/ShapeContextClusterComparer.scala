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

package me.uits.aiphial.imaging.searching.shapematching

import java.awt.image.BufferedImage
import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.LuvPoint
import me.uits.aiphial.imaging.Region
import me.uits.aiphial.imaging.searching.RegionComparer

import scala.collection.JavaConversions.asIterable

class ShapeContextClusterComparer extends RegionComparer {
  private val num = 100

  private var pattern: ShapeContext = null

  override def compareCluster(cluster: Region): Double =
    {
      require(pattern != null, "pattern was not set")

      val contour = cluster.getContour
      if (contour.size > num)
        return Matching.cyclicMatch(this.pattern, ShapeContext(contour, num))._1
      else
        return Double.PositiveInfinity
    }

  override def setPattern(pattern: Region) = {
    this.pattern = ShapeContext(pattern.getContour, num)
  }

  def setPattern(pattern: BufferedImage) {


    val c = ImgUtls.asRegion(ImgUtls.luvDArraytoLuvPoints(ImgUtls.ImageToLuvDArray(pattern)))
    this.setPattern(c)
  }

  //   public void setPattern(BufferedImage orig)
  //    {
  //        LUV[][] toLUVDArray = new LUVConverter().toLUVDArray(orig);
  //        Collection<LuvPoint> luvDArraytoLuvPoints = ImgUtls.luvDArraytoLuvPoints(toLUVDArray);
  //        this.patternCluster = luvDArraytoLuvPoints;
  //        this.pattern = LUVHistorgam.build(this.patternCluster);
  //    }

}


