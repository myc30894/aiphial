/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import me.uits.aiphial.imaging.texture.Gabor
import ru.nickl.meanShift.direct.LUV
import scala.collection.JavaConversions.asScalaIterable
import scala.collection.mutable.ArrayBuffer
import scalarunner.Tools._

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
