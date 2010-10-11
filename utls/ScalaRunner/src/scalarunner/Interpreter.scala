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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package scalarunner

import java.io.PrintWriter
 import scala.io.Source._
/**
 * Created by IntelliJ IDEA.
 * User: Nickl
 * Date: 11.12.2009
 * Time: 18:25:45
 * To change this template use File | Settings | File Templates.
 */

object Interpreter  {
    def jarPathOfClass(className: String): String = {
    val resource = className.split('.').mkString("/", "/", ".class")
    var path = getClass.getResource(resource).getPath
    val indexOfFile = path.indexOf("file:")
    val indexOfSeparator = path.lastIndexOf('!')
    //path.substring(indexOfFile, indexOfSeparator)
    //path = path.substring(5)

    if (indexOfSeparator != -1) path.substring(0, indexOfSeparator) else path
  }

  def main2(args: Array[String]): Unit = {


    //val script = new BufferedReader(new FileReader("src/shapematch.scala"))

    val script = fromFile("shapematch.scala").getLines().mkString("\n")

    val settings = new scala.tools.nsc.Settings()


    val origBootclasspath = settings.bootclasspath.value
    //    val pathList = List(
    //      //jarPathOfClass("scala.tools.nsc.Interpreter"),
    //      //jarPathOfClass("scala.ScalaObject"),
    //      ":"+System.getProperties().getProperty("java.class.path")
    //      )
    //
    val pathList = scala.collection.immutable.List(
      ":" + jarPathOfClass("me.uits.aiphial.imaging.searching.ShapeContext"),
      ":" + jarPathOfClass("scala.tools.nsc.Interpreter"),
      ":" + jarPathOfClass("scala.ScalaObject"),
      ":" + jarPathOfClass("me.uits.aiphial.general.aglomerative.AglomerativeMeanShift"),
      ":" + jarPathOfClass("me.uits.aiphial.imaging.ImgUtls"),
      ":" + jarPathOfClass("ru.nickl.meanShift.ImageUtls")
      )


    val pt = (origBootclasspath :: pathList).mkString(java.io.File.separator)

    println(pt)

    settings.bootclasspath.value = pt


    val interpreter = new scala.tools.nsc.Interpreter(settings, new PrintWriter(System.out))



    interpreter.interpret(script)


  }
}