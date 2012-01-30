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

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.dataStore.KdTreeDataStore
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.ImgUtls._
import me.uits.aiphial.imaging.LuvPoint
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.math._

import me.uits.aiphial.imaging.FastMatrixMS
import me.uits.aiphial.imaging.MatrixMS

import me.uits.aiphial.imaging.Tools
import me.uits.aiphial.imaging.Tools._

object AggloSegmentationSteps {


  val sr = 2:Short
  val cr = 7f
  val minreg = 5

  def main(args: Array[String]): Unit = {

 

    val srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"))


    val msc = new AglomerativeClustererStack[LuvPoint]();

    //val initc = genOldInitClusterer(srcimg)
    val initc = genMatrixMeanshiftSegmentator(srcimg)

    msc.setInitialClusterer(initc)

    val msc0 = new MeanShiftClusterer[NDimPoint]()
    {
      setMinDistance(3)
    }


    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(0.2f)
      setWindowMultiplierStep(0.1f)
    }


    msc.addExtendingClustererToQueue(amsc)


    msc.addIterationListener({var s = 0;
                               var prevtime = System.currentTimeMillis
                              (a:CCLP)=>{
         
          val curtime = System.currentTimeMillis
          println("step "+s+" clusters="+a.size+" spent="+(curtime-prevtime))
          prevtime = curtime

          ImageIO.write(
            paintClusters(srcimg.getWidth, srcimg.getHeight, a, true),
            "bmp",
            new File("./out_"+s+".bmp"))
          s=s+1
        }})

    
    msc.doClustering()

  }

  def genMatrixMeanshiftSegmentator(srcimg:BufferedImage)={

     val srcmt =Tools.matrixFromImage(srcimg);

    val is = new MatrixMS(srcmt)
    {
       cr = AggloSegmentationSteps.cr
       sr = AggloSegmentationSteps.sr
       minsize = AggloSegmentationSteps.minreg
    }

    is
  }

 
}
