/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.uits.aiphial.imaging.runner

import org.clapper.argot.ArgotParser
import java.awt.image.BufferedImage
import me.uits.aiphial.imaging.SegmentatorAdapter
import org.clapper.argot.ArgotConverters._
import ru.nickl.meanShift.direct.filter.SimpleMSFilter

import scala.collection.JavaConversions.asIterable

import scalarunner.Tools._

class NaiveMSCli(parser: ArgotParser) {

  val coloRange = parser.option[Float](List("cr", "colorRange"), "size",
                                    "TODO")

  val spatialRange = parser.option[Short](List("sr", "spatialRange"), "size",
                                    "TODO")

  val minreg = parser.option[Int](List("mr", "minreg"), "size",
                                    "TODO")


  def process(image:BufferedImage):BufferedImage={

    val ifilter = new SimpleMSFilter{
      setColorRange(coloRange.value.getOrElse(7))
      setSquareRange(spatialRange.value.getOrElse(2) )
    }

    val is  = new ru.nickl.meanShift.direct.segmentator.SimpleSegmentator(ifilter){
      setMinRegionSize(minreg.value.getOrElse(0))

    }

    is.setSourceImage(image)
    
    val segmentator = new SegmentatorAdapter(is)
    segmentator.doClustering()
    

    val r = paintClusters(image.getWidth,image.getHeight, segmentator.getClusters())

    return r;
  }



}
