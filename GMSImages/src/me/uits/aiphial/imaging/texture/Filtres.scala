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

package me.uits.aiphial.imaging.texture

import me.uits.aiphial.imaging.Matrix
import scala.io.Source
import scala.util.parsing.json.JSON

object Filtres {
   
  val gaborFiltres9 = readfrominternaljs("g9.js")

  val gaborFiltres15 = readfrominternaljs("g15.js")


  private[this]  def readfrominternaljs(js:String) ={
    val addr = Filtres.getClass.getResourceAsStream(js)
    val input = Source.fromInputStream(addr).mkString

    addr.close

    jsontofilter(input)
  }

  private[this] def jsontofilter(input:String) = {

    //println(input)

    (JSON.parseFull(input) match {
        case Some(a:List[List[List[Double]]]) => a.toArray.map(_.toArray.map(_.toArray))
        case None => throw new RuntimeException("cant parse")
      }).map(Matrix(_))
  }

  def readfromjs(filename:String) = {
    //print("JSON("+filename+")=")
    val input = Source.fromFile(filename) .mkString
    jsontofilter(input)
  }

  val gaborMatrix ={
    val gaborMatrix9 = Filtres.gaborFiltres9
    val gaborMatrix15 = Filtres.gaborFiltres15
    
    gaborMatrix9.map(_.addBorder(3, 3, 0))++gaborMatrix15
  }


}
