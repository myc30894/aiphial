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
import me.uits.aiphial.imaging.ImgUtls
import me.uits.aiphial.imaging.Matrix
import me.uits.aiphial.imaging.Tools
import me.uits.aiphial.imaging.MatrixMeanShift
import ru.nickl.meanShift.direct.LUV

object MatrixMeanShiftTest {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {


    //val srcmt = Matrix(ImgUtls.readImageAsLuvArray("../../images/DSCN4909s400.bmp"));

    val srcmt = Tools.matrixFromImage("../../images/DSCN4909s400.bmp")

    //val result = MatrixMeanShift.meanshift(srcmt, 20, 7f)

    val result = MatrixMeanShift.fastmeanshift(srcmt, 20, 7f)
    
    ImageIO.write(Tools.matrixToImage(result),"png", new File("./mmt.png"))
    
    val regions = MatrixMeanShift.regionGroving(result,2f)
    
    val ra = Array.ofDim[LUV](result.height,result.width)

    val colors = Tools.genRandomColors(regions.size)

    import scala.collection.JavaConversions.asScalaIterable

          for((region,color) <- regions zip colors; point <- region)
          {
            ra(point.getX)(point.getY)= color
          }

    ImageIO.write(Tools.matrixToImage(Matrix(ra)),"bmp",new File("msrg.bmp"));
 
 
  }

}
