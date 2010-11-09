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


object Textures {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {


    val gaborMatrix = Gabor.garbormatrix.map(Matrix(_))


    val gaborimages = gaborMatrix.map(_.map( v=> new LUV(100*v,0.,0.)))


    for ( (mat,i) <- gaborimages zipWithIndex)
      {
         ImageIO.write(matrixToImage(mat),"bmp",new File("gab_"+i+".bmp"));
      }
    


  }

}
