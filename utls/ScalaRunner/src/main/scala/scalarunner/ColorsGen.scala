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
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.basic.SimpleBandwidthSelector
import me.uits.aiphial.general.dataStore.DataStore
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging._
import me.uits.aiphial.imaging.LUV
import me.uits.aiphial.imaging.LUV._
import me.uits.aiphial.imaging.texture.Filtres
import scala.collection.JavaConversions.asScalaIterable
import scala.collection.mutable.ArrayBuffer
import me.uits.aiphial.imaging.Tools._

import scala.math.ScalaNumericConversions

object ColorsGen {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {

    val cs = 10


    //val colors = genDistinctColors(cs*cs);

    val colors = genRandomColors(cs*cs)

    val ra1 = Array.tabulate[LUV](cs,cs)((x,y)=>colors(x*cs+y))

    ImageIO.write(ImgUtls.LuvArrayToBufferedImage(ra1),"bmp",new File("colorssc.bmp"));

    val ra2 = Array.tabulate[LUV](colors.length,1)((x,y)=>colors(x))

    ImageIO.write(ImgUtls.LuvArrayToBufferedImage(ra2),"bmp",new File("colorline.bmp"));

    




  }

}
