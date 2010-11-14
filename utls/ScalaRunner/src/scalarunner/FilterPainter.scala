/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scalarunner

import java.io.File
import javax.imageio.ImageIO
import me.uits.aiphial.imaging._
import me.uits.aiphial.imaging.texture.Gabor
import ru.nickl.meanShift.direct.LUV

import scalarunner.Tools._


object FilterPainter {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {


    //val gaborMatrix = Gabor.gaborFiltres15.map(Matrix(_

//    val gaborMatrix9 = Gabor.gaborFiltres9.map(Matrix(_))
//    val gaborMatrix = Gabor.gaborFiltres15.map(Matrix(_))
//
//    val gaborMatrix = gaborMatrix9.map(_.addBorder(3, 3, 0))++gaborMatrix15

    val gaborMatrix = Gabor.gaborMatrix

    val gaborimages = gaborMatrix.map(_.map( v=> new LUV(50+50*v,0.,0.)))


    for ( (mat,i) <- gaborimages zipWithIndex)
      {
         ImageIO.write(matrixToImage(mat),"bmp",new File("gab_"+i+".bmp"));
      }



  }

}

