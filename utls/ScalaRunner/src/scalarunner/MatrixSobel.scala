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
import me.uits.aiphial.imaging._
import me.uits.aiphial.imaging.LUV

import Tools._

object MatrixSobel {

 
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
