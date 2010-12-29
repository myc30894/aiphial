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

import me.uits.aiphial.general.dataStore.NDimPoint

class LuvPoint() extends NDimPoint  {

  private[this] var _c = new LUV(0,0,0)
  private[this] var _x = 0
  private[this] var _y = 0
  private[this] var coords = Array.ofDim[Float](5)

  def this(x:Int,y:Int,c:LUV) = {
    this()
    this._c = c;
    this._x = x;
    this._y = y;

    coords = Array(
      x.floatValue,
      y.floatValue,
      c.l.floatValue,
      c.u.floatValue,
      c.v.floatValue
    )

  }

  def this(array:Float*)={
    this()
    for((e,i) <- array zipWithIndex )
    {
      setCoord(i, array(i));
    }
  }

  def this(ndp: NDimPoint){
    this()
    for(i <- Range(0,ndp.getDimensions) )
    {
      setCoord(i, ndp.getCoord(i));
    }
  }

  override def getCoord(i:Int) = coords(i);
  override def setCoord(i:Int, v0:java.lang.Float) = {
    val v = v0.floatValue
    coords(i) = v
    i match {
      case 0 => setX(v.intValue)
      case 1 => setY(v.intValue)
      case 2 => _c = new LUV(v, _c.u, _c.v);
      case 3 => _c = new LUV(_c.l, v, _c.v);
      case 4 => _c = new LUV(_c.l, _c.u, v);
      case _ => throw new IndexOutOfBoundsException("no such coord:" + i);
    }
  }

  override def getDimensions() = 5
  override def getWeight() = 1f

  def getLUV() = _c
  def c = _c
  def setLUV(c:LUV)={
    this._c = c;
    coords(2) =c.l.floatValue
    coords(3) =c.u.floatValue
    coords(4) =c.v.floatValue
  }
  def c_=(c:LUV) = setLUV(c)

  def getX() = _x
  def x = _x
  def setX(x:Int)={
    this._x = x
    coords(0) = x.floatValue
  }
  def x_=(x:Int) = setX(x)

  def getY() = _y
  def y = _y
  def setY(y:Int)={
    this._y = y
    coords(1) = y.floatValue
  }
  def y_=(y:Int) = setY(y)

  override def toString = "x:" + _x + " y:" + _y + " luv:" + _c;


}
