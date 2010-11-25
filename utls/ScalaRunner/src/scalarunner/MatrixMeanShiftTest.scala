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

object MatrixMeanShiftTest {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {


    val srcmt = Matrix(ImgUtls.readImageAsLuvArray("../../images/DSCN4909s400.bmp"));


    val result = MatrixMeanShift.meanshift(srcmt, 20, 7f)


    ImageIO.write(Tools.matrixToImage(result),"png", new File("./mmt.png"))

  }

}
