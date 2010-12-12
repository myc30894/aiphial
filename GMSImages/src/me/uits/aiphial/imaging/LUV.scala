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

package me.uits.aiphial.imaging

class LUV(val l:Double,val u:Double, val v:Double) {

  override def toString() = "{l="+l+" u="+u+" v="+v+"}";

  def -(a:LUV):LUV = new LUV(l-a.l, u-a.u, v-a.v)

  def *(m:Int) = new LUV(l*m, u*m, v*m);


  def /(m:Int) = new LUV(l/m, u/m, v/m)

  def +(a:LUV) = new LUV(l+a.l, u+a.u, v+a.v)
 
  def minus(a:LUV):LUV = this-a

  def mult(m:Int) = this*m

  def div(m:Int) = this/m
  
  def plus(a:LUV):LUV = this+a

  @deprecated
  def m()=this

}

object LUV {

  implicit def oldArrays(arr:Array[LUV]):Array[ru.nickl.meanShift.direct.LUV] = arr.map(tooldLUV(_))

  implicit def oldDArrays(arr:Array[Array[LUV]]):Array[Array[ru.nickl.meanShift.direct.LUV]] = arr.map(_.map(tooldLUV(_)))

  implicit def modernArrays(arr:Array[Array[ru.nickl.meanShift.direct.LUV]]):Array[Array[LUV]] = arr.map(_.map(tomodernLUV(_)))

  implicit def tomodernLUV(o:ru.nickl.meanShift.direct.LUV) = new LUV(o.l,o.u,o.v)

  implicit def tooldLUV(o:LUV) = new ru.nickl.meanShift.direct.LUV(o.l,o.u,o.v)
}