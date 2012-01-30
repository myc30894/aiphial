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



import me.uits.aiphial.imaging.FastMatrixMS
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.ImgUtls._
import me.uits.aiphial.imaging.MatrixMS
import me.uits.aiphial.imaging.Tools

object NaiveSegmentationSample {

  
  def main(args: Array[String]): Unit = {

    // read a buffered image from file
    val srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"))

    // then create a Clusterer, FastMatrixMS is a simple Mean Shift Clusterer for images
    val a = new FastMatrixMS(Tools.matrixFromImage(srcimg)){
      // setup filter parametrs
      setColorRange(7f)
      setSquareRange(20)
    }

    // process
    a.doClustering();    
    
    // paint clusters on image
    val img = Tools.paintClusters(srcimg.getWidth(), srcimg.getHeight, a.getClusters, false)
    
    // write results to file
    ImageIO.write(img, "bmp", new File("./out_.bmp"))
    
  }


}
