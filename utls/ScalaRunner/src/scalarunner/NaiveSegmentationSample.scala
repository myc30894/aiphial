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


import javax.imageio.ImageIO

import ru.nickl.meanShift.direct.filter.FastMSFilter
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor

import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.ImgUtls._

object NaiveSegmentationSample {

  
  def main(args: Array[String]): Unit = {

    // read a buffered image from file

    val srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"))

    // then create a MSFilter, in normal case it should be a
    // FastMSFilter

    val a = new FastMSFilter(){
      // setup filter parametrs
      setColorRange(7f)
      setSquareRange(20)
    }

    // create image processoe wrapper for this filter
    val msp = new MeanShiftFilterImageProcessor(a)

    // set an image to process
    msp.setSourceImage(srcimg )

    // process this image
    msp.process()

    // write results to file
    ImageIO.write(msp.getProcessedImage(), "bmp", new File("./out_.bmp"))
    
  }


}
