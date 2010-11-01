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

package me.uits.aiphial.imaging.runner

import java.io.File

import java.io.PrintWriter
import javax.imageio.ImageIO
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

import com.beust.jcommander.{Parameter, Parameters};

import scalarunner.Tools._

@Parameters(commandDescription = "agglomerative segmentation for image")
class AgloMSCli extends CliCommand {

  @Parameter(names = Array("-i"), description = " input file name",required = true)
  var inputFileName:String = null// = "../../images/DSCN4909s400.bmp"

  @Parameter(names = Array("-o"), description = " output file name")
  var outFilesName = "out.bmp"

  @Parameter(names = Array("-cr"), description = "color range")
  var cr = 7

  @Parameter(names = Array("-sr"), description = "square range")
  var sr:Short = 2

  @Parameter(names = Array("-mr"), description = "minimum region size")
  var minreg = 0

  @Parameter(names = Array("-md"), description = "minimum distance")
  var md = 3

  @Parameter(names = Array("-wm"), description = "windows multiplier")
  var windowsMultiplier = 0.2f

  @Parameter(names = Array("-wms"), description = "windows multiplier step")
  var windowsMultiplierStep = 0.1f


  def name = "agglosegm"

  def process(): Unit = {



    val filetoread = new File(inputFileName)
    println("reading "+filetoread.getAbsolutePath)
    val srcimg = ImageIO.read(filetoread)


    val msp = createSegmentatorForImage(srcimg)

    msp.addIterationListener({var s = 0;
                              (a:CC)=>{
              
          val fname = makeIndexedName(outFilesName,s)
          val file = new File(fname)
          println("writing:"+file.getAbsolutePath)
          
          ImageIO.write(
            paintClusters(srcimg.getWidth, srcimg.getHeight, a),
            getFormatByName(file.getName).getOrElse("bmp"),
            file)
          s=s+1
        }})

    logTime
    {
      println("clustering...")
      msp.doClustering()
      println("finished")
    }
     

  }


  
  def createPureAglomerativeSegmentator(srcimg:BufferedImage) = {
    
    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(md)
    
    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(windowsMultiplier)
      addIterationListener({var v = windowsMultiplier; (a: CC) => {this.setWindowMultiplier(v); v += windowsMultiplierStep}})
    }


    val datastore = new KdTreeDataStore[LuvPoint](5)
    imageAsLUVPointCollection(srcimg).foreach(datastore.add(_))

    //val datastore = new LuvDataStore(ImageToLuvDArray(srcimg))

    amsc.setDataStore(datastore)    
    amsc      
  }
  

 
  def createSegmentatorForImage(srcimg:BufferedImage) = {

    

    val msc = new AglomerativeClustererStack[LuvPoint]();

//    val growingSegmentator = new RegionGrowingSegmentator(){
//      setEqualityRange(10)
//      setSourceImage(srcimg)
//    }
//     msc.setInitialClusterer(new SegmentatorAdapter(growingSegmentator))


    //val ifilter = new NativeCudaMSFilter{
    val ifilter = new SimpleMSFilter{
      setColorRange(cr)
      setSquareRange(sr)
    }

    val is  = new ru.nickl.meanShift.direct.segmentator.SimpleSegmentator(ifilter){
      setMinRegionSize(minreg)
    }


//    val is = new ru.nickl.meanShift.direct.segmentator.SobelMeanShiftSegmentator(ifilter){
//      setGradTreshold(5)
//      setEqualityRange(1)
//      setColorRange(7)
//      setSquareRange(2)
//      setMinRegionSize(0)
//    }
//


    is.setSourceImage(srcimg)

    msc.setInitialClusterer(new SegmentatorAdapter(is));

    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(md)

    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(windowsMultiplier)
      addIterationListener({var v = windowsMultiplier;
                            (a: CC) => {this.setWindowMultiplier(v); v += windowsMultiplierStep}})
    }

    msc.addExtendingClustererToQueue(amsc)

//
//    msc.addIterationListener(new Searcher(ImageIO.read(new File("../../images/bluehren.png")), srcimg))
//

    msc
  }
  
 
}
