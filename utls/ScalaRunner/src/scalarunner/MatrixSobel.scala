/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scalarunner

import java.io.File
import javax.imageio.ImageIO
import me.uits.aiphial.imaging._
import ru.nickl.meanShift.direct.LUV

object MatrixSobel {


  def matrixToImage(m:Matrix[LUV]) = ImgUtls.LuvArrayToBufferedImage(m.toArray)
  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {

    val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../../images/DSCN4909s400.bmp"))


    val gray = imagemtx.map(p => new LUV(p.l,0.,0.))
    
    ImageIO.write(matrixToImage(gray),"bmp",new File("out.bmp"));


//    val xsobel = Matrix(Array(
//        Array(-1,0,1),
//        Array(-2,0,2),
//        Array(-1,0,1)
//      ))
//
//    val ysobel = Matrix(Array(
//        Array(-1,0,1),
//        Array(-2,0,2),
//        Array(-1,0,1)
//      ))
//
//    //gray.mapMask()

    

  }

}
