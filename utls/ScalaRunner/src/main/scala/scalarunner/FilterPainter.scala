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
import me.uits.aiphial.imaging.texture.Filtres
import me.uits.aiphial.imaging.LUV

import me.uits.aiphial.imaging.Tools._


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

    val gaborMatrix = Filtres.gaborMatrix

    val gaborimages = gaborMatrix.map(_.map( v=> new LUV(50+50*v,0.,0.)))


    for ( (mat,i) <- gaborimages zipWithIndex)
      {
         ImageIO.write(matrixToImage(mat),"bmp",new File("gab_"+i+".bmp"));
      }



  }

}

