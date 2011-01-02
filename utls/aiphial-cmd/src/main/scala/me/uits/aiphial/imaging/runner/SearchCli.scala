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

import me.uits.aiphial.general.aglomerative.AglomerativeClustererStack
import me.uits.aiphial.general.aglomerative.AglomerativeMeanShift
import me.uits.aiphial.general.aglomerative.IterationListener
import me.uits.aiphial.general.basic.Cluster
import me.uits.aiphial.general.basic.MeanShiftClusterer
import me.uits.aiphial.general.dataStore.DefaultDataStoreFactory
import me.uits.aiphial.general.dataStore.KdTreeDataStore
import me.uits.aiphial.general.dataStore.KdTreeDataStoreFactory
import me.uits.aiphial.general.dataStore.NDimPoint
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.ImgUtls._
import me.uits.aiphial.imaging.LuvDataStore
import me.uits.aiphial.imaging.LuvPoint

import me.uits.aiphial.imaging.MatrixMS
import me.uits.aiphial.imaging.Region
import me.uits.aiphial.imaging.searching.HistogramClusterComparer
import scala.collection.mutable.ArrayBuffer

import me.uits.aiphial.imaging.searching.shapematching.ShapeContext
import me.uits.aiphial.imaging.searching.shapematching.ShapeContextClusterComparer
import scala.collection.JavaConversions.asScalaIterable
import scala.math._
import java.awt.image.BufferedImage
import java.awt.{Graphics2D, Color, BasicStroke, Polygon}

import com.beust.jcommander.{Parameter, Parameters};

import me.uits.aiphial.imaging.Tools._

@Parameters(commandDescription = "search for given pattern in an image")
class SearchCli extends CliCommand {


  @Parameter(names = Array("-i"), description = " input file name",required = true)
  var inputFileName:String = null

  @Parameter(names = Array("-p"), description = " pattern to search file name",required = true)
  var patternFileName:String = null

  @Parameter(names = Array("-o"), description = " output files name")
  var matchName = "match.png"

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

  @Parameter(names = Array("-cs"), description = "color similarity")
  var colorSimilarity = 190

  @Parameter(names = Array("-ss"), description = "shape similarity")
  var shapeSimilarity = 2000

  @Parameter(names = Array("-ps"), description = "shape similarity")
  var paintclusters = false

  @Parameter(names = Array("-os"), description = " segmentation file names")
  var outFilesName = "segm.bmp"

  def name = "search"

  def process(): Unit = {

    DefaultDataStoreFactory.setPrototype(new KdTreeDataStoreFactory())

    //new File(matchName).mkdir;

    val startTime = System.currentTimeMillis

    //val srcimg = ImageIO.read(new File("../../images/smallgisto.jpg"))
    val srcimg = ImageIO.read(new File(inputFileName))
    //val srcimg = ImageIO.read(new File("/home/nickl/biotecnical/Диссертация/work/data/язык/1243495680.jpg"))
    //val imgtosearch = ImageIO.read(new File("../../images/bluehren.png"))
    val imgtosearch = ImageIO.read(new File(patternFileName))

    val h = srcimg.getHeight();
    val w = srcimg.getWidth();

    val msc = new AglomerativeClustererStack[LuvPoint]();




    val srcmt =matrixFromImage(srcimg);
    val ifilter = new MatrixMS(srcmt){
      setColorRange(SearchCli.this.cr)
      setSquareRange(SearchCli.this.sr)
      setMinRegionSize(SearchCli.this.minreg)
    }





    msc.setInitialClusterer(ifilter);

    val msc0 = new MeanShiftClusterer[NDimPoint]();
    msc0.setMinDistance(md)

    val amsc = new AglomerativeMeanShift[LuvPoint](msc0){
      setAutostopping(false)
      setMaxIterations(1000)
      setWindowMultiplier(windowsMultiplier)
      addIterationListener({var v = windowsMultiplier; (a: CCLP) => {this.setWindowMultiplier(v); v += windowsMultiplierStep}})
    }

    msc.addExtendingClustererToQueue(amsc)


    if(paintclusters)
    {
      msc.addIterationListener({
          var t = System.currentTimeMillis()
          var i = 0
          (a: CCLP) => {
            println(a.size + " " + (System.currentTimeMillis() - t))
            val file = new File(makeIndexedName(outFilesName,i))
            file.mkdir
            ImageIO.write(paintClusters(w,h,a), getFormatByName(file.getName).getOrElse("bmp"),file )
            t = System.currentTimeMillis()
            i = i + 1
          }
        })
    }





   
    amsc.addIterationListener({
        val cc = new HistogramClusterComparer()
        cc.setPattern(imgtosearch)
        val sc = new ShapeContextClusterComparer()
        sc.setPattern(imgtosearch)


        var i2 = 0

        (a: CCLP) => {
          for (cluster <- a) {
            val v = cc.compareCluster(cluster)
            //vals.append(v)
            if(0 <= v && v < colorSimilarity){
              val sv = sc.compareCluster(cluster)              
              if (sv < shapeSimilarity)
              {
                val img = ImgUtls.getClusterImage(srcimg, cluster)                
                if (img != null)
                {                  
                  val file = new File(makeIndexedName(matchName,"_"+i2 + "_" + v +"_"+sv))
                  file.mkdirs
                  val format = getFormatByName(file.getName).getOrElse("png")
                  if(!ImageIO.write(img, format , file))
                    throw new RuntimeException("cannot write an image in \""+format+"\" format")
                }
              }
            }
            i2 = i2 + 1
          }
        }
      })

    msc.doClustering() 


  }

}
