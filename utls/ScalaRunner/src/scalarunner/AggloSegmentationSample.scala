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
import me.uits.aiphial.general.dataStore.KdTreeDataStore
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.ImgUtls._
import me.uits.aiphial.imaging.LuvPoint
import scala.collection.JavaConversions.asIterable
import scala.math._

import Tools._

object AggloSegmentationSample {


  def main(args: Array[String]): Unit = {

    // read a buffered image from file

    val srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"))

    // then create a mean-shift clusterer which would be used
    // at each step of agglomareative clustering
    val msc0 = new MeanShiftClusterer[NDimPoint]()
    {
      setMinDistance(3)
    }

    // create an agglomerative clusterer which is based on created mean-shift clusterer
    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(0.2f)
      // an iteration listener that would increment window multiplier on each step
      // to provide additional agglomeretivity :)
      addIterationListener({var v = 0.2f; (a: CC) => {this.setWindowMultiplier(v); v += 0.1f}})
    }

    // create a datastore and add all points from image to it
    val datastore = new KdTreeDataStore[LuvPoint](5)
    imageAsLUVPointCollection(srcimg).
      foreach(datastore.add(_))

    // set created datasrote as data source for agglomerative clusterer
    amsc.setDataStore(datastore)


    // add an iteration listener that would write to image file
    // results of each step of the agglomerative clustering
    amsc.addIterationListener({var s = 0;
                              (a:CC)=>{
          ImageIO.write(
            paintClusters(srcimg.getWidth, srcimg.getHeight, a),
            "bmp",
            new File("../out_"+s+".bmp"))
          s=s+1
        }})

    // start the clustering process
    amsc.doClustering()

  }
  
 
}
