/*
 *
 * This file is part of Aiphial.
 *
 * Copyright (c) 2010 Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package scalarunner

object RunAll {

  /**
   * something like a smoke-test
   */
  def main(args: Array[String]): Unit = {
    

    println("ShapeCompare.main")
    ShapeCompare.main(args)

    println("AgloSpeedTest.main")
    AgloSpeedTest.main(args);

    println("Search.main")
    Search.main(args)

    println("Search.main0")
    Search.main0(args)

    println("NaiveSegmentationSample.main")
    NaiveSegmentationSample.main(args)

    println("Segmentation.main")
    Segmentation.main(args);

    System.exit(0);
  }

}
