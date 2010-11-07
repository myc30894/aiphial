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


    val xsobel = Matrix(Array(
        Array(-1,0,1),
        Array(-2,0,2),
        Array(-1,0,1)
      ))

    val ysobel = xsobel.rotateClockwise


    val gradient = gray.windowingMap(3,3)(
      m=>
      {
        val s1 = m.join(xsobel)(_.l*_).reduce(_+_)
        val s2 = m.join(ysobel)(_.l*_).reduce(_+_)

        math.sqrt(s1*s1+s2*s2)
      }
    )

    ImageIO.write(matrixToImage(

        gradient.map(p => new LUV(p*2,0.,0.))


      ),"bmp",new File("out_grad.bmp"));

   
    

  }

}
