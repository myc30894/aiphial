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

package me.uits.aiphial.imaging.runner


import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import me.uits.aiphial.imaging.SegmentatorAdapter

import ru.nickl.meanShift.direct.filter.SimpleMSFilter

import scala.collection.JavaConversions.asScalaIterable
import com.beust.jcommander.{Parameter, Parameters};
import scalarunner.Tools._

@Parameters(commandDescription = "naive mean-shift segmentation for image")
class NaiveMSCli() extends CliCommand {

  @Parameter(names = Array("-i"), description = " input file name",required = true)
  var inputFileName:String = null;// = "../../images/DSCN4909s400.bmp"

  @Parameter(names = Array("-o"), description = " output file name")
  var outFilesName = "out.bmp"

  @Parameter(names = Array("-cr"), description = "color range")
  var cr = 7

  @Parameter(names = Array("-sr"), description = "square range")
  var sr:Short = 2

  @Parameter(names = Array("-mr"), description = "minimum region size")
  var minreg = 0

  def name = "mssegm"

  def process(): Unit = {

    val ifilter = new SimpleMSFilter{
      setColorRange(cr)
      setSquareRange(sr)
    }

    val is  = new ru.nickl.meanShift.direct.segmentator.SimpleSegmentator(ifilter){
      setMinRegionSize(minreg)

    }

    val filetoread = new File(inputFileName)
    println("reading "+filetoread.getAbsolutePath)
    val image = ImageIO.read(filetoread)

    is.setSourceImage(image)
    
    val segmentator = new SegmentatorAdapter(is)

    logTime{ 
      println("clustering...")
      segmentator.doClustering()
      println("finished")
    }
    

    val r = paintClusters(image.getWidth,image.getHeight, segmentator.getClusters())
    val file = new File(outFilesName)
    println("writing:"+file.getAbsolutePath)
    ImageIO.write(
      r,
      getFormatByName(file.getName).getOrElse("bmp"),
      file)
   
  }



}
