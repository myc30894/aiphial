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

import java.util.Arrays

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
class Matrix[T] private (private val data:Array[Array[T]],xstart:Int = 0,ystart:Int = 0)(implicit Tmf:ClassManifest[T]) {

  val height = data.length
  val width = data(0).length


  def apply(x:Int, y:Int)=data(x-xstart)(y-ystart)

  def minx = xstart
  def miny = ystart
  def maxx = xstart + height - 1
  def maxy = ystart + width -1

  def map[B](f:T=>B)(implicit mf:ClassManifest[B]):Matrix[B]=new Matrix[B](
    data.map(_.map(f)),xstart,ystart
  )
 
  def mapWithIndex[B](f:(Int,Int,T)=>B)(implicit mf:ClassManifest[B]):Matrix[B] =new Matrix(
    (Array.tabulate(this.height, this.width)
    ((x,y)=>f(x+xstart,y+ystart,this(x+xstart,y+ystart)))),xstart,ystart
  )

  def zeroIndexes() = if (minx==0 && miny == 0) this else new Matrix(data,0,0)

  def asOneLine = data.flatten((a)=>a)


  def asOneLineWithIndex = for(x <- Range.inclusive(minx,maxx); y <- Range.inclusive(miny, maxy)) yield (x,y,this(x,y))
  
  def foreach(f:((Int,Int,T))=>Any):Unit = for(x <- Range.inclusive(minx,maxx); y <- Range.inclusive(miny, maxy))  f(x,y,this(x,y))

  @deprecated("not implemented, throwns an UnsupportedOperationException."+
              " It is there only to make for-comprehension work")
  def filter(f:((Int,Int,T))=>Boolean):Matrix[T] = throw new UnsupportedOperationException("filter is dummy")


  def join[B,C](another:Matrix[B])(op:(T,B)=>C)(implicit mf:ClassManifest[C]) = { 
    
    require(this.height == another.height && this.width == another.width, "matrix must be the same size")

    new Matrix[C](    

      for ( (a,b) <- this.data zip another.data )
        yield for ( (a1,b1) <- a zip b )
          yield op(a1,b1)

    ,minx,miny)}

  def reduce(f:(T,T)=> T) = asOneLine.reduceLeft(f)

  def submatrix(x1:Int, y1:Int, x2:Int, y2:Int ) = new Matrix[T](
    //TODO: implement this as view (not complex)
    data.slice(x1, x2+1).map(_.slice(y1, y2+1)).toArray, math.max(x1,0),math.max(y1,0)
  )

   def addBorder(hsize:Int,wsize:Int,fillvalue: T) = new Matrix[T](
     Array.tabulate(this.height+2*hsize, this.width+2*wsize)
     ((x,y) =>{
      val sx = x-hsize;
      val sy = y-wsize;
      if(sx>=0 && sx < this.height && sy>=0 && sy < this.width)
        this(sx,sy)
      else
        fillvalue
     })
  )

  def chopBorder(hsize:Int,wsize:Int) = new Matrix[T](
     Array.tabulate(this.height-2*hsize, this.width-2*wsize)
     ((x,y) =>{
      val sx = x+hsize;
      val sy = y+wsize;
        this(sx,sy)
     })
  )

  override def equals(arg0: Any) = arg0 match{

    case a: Matrix[T] => (a.data.length == this.data.length)  &&
      ((a.data zip this.data)
       forall((a)=>
          (a._1.length == a._2.length) &&
          ((a._1 zip a._2) forall((a)=>a._1== a._2))))
    case _ => false}
  

  override def toString = data map (_.mkString("Array(", ",", ")")) mkString("Matrix(Array(\n", ",\n", "\n))\n")

  def toJSON = data map (_.mkString("[", ",", "]")) mkString("[\n", ",\n", "\n]\n")

  
  def toArray:Array[Array[T]] = data;

  def mapMask[A,T2](mask:Matrix[T2])(f:(T,T2)=>A)(reduce:(A,A)=>A)
  (implicit mf1:ClassManifest[A]):Matrix[A]=
    windowingMap(mask.height, mask.width)(_.join(mask)(f).reduce(reduce))

  def wndDotProduct(mask:Matrix[T])(implicit num:Numeric[T]) = mapMask(mask)(num.times(_, _))(num.plus(_, _))

  def windowingMap[A](h:Int, w:Int)(f:Matrix[T]=>A)(implicit mf1:ClassManifest[A]):Matrix[A]= {
        Matrix(Array.tabulate(this.height-h+1, this.width-w+1)
           ((x,y) =>
        f(getWithinWindow((x+(h-1)/2, y+(w-1)/2),h, w))
      )
    )

  }

  
  def sliding(h:Int, w:Int):Stream[Matrix[T]]=
    for(x <- Stream.range(0+(h-1)/2,height-h/2); y <- Stream.range(0+(w-1)/2,width-w/2))
      yield  getWithinWindow((x,y),h,w)


  def slidingWithIndex(h:Int, w:Int):Stream[(Int,Int,Matrix[T])]=
    for(x <- Stream.range(0+(h-1)/2,height-h/2); y <- Stream.range(0+(w-1)/2,width-w/2))
      yield (x,y, getWithinWindow((x,y),h,w))


  def getWithinWindow(c:Tuple2[Int,Int],h:Int,w:Int):Matrix[T]= this.submatrix(c._1-(h-1)/2, c._2-(w-1)/2, c._1+h/2, c._2+w/2)

  def rotateCounterClockwise():Matrix[T] = {
    Matrix(
      Array.tabulate(this.width,this.height)(
        (x,y)=> this(y,this.width-1-x)
      )
    )
  }

  def rotateClockwise():Matrix[T] = {
    Matrix(
      Array.tabulate(this.width,this.height)(
        (x,y)=> this(this.height-1-y,x)
      )
    )
  }

  def rotateReflex():Matrix[T] = {
    Matrix(
      Array.tabulate(this.height,this.width)(
        (x,y)=> this(this.height-1-x,this.width-1-y)
      )
    )
  }

  def transpose():Matrix[T] = {
    Matrix(
      Array.tabulate(this.width,this.height)(
        (x,y)=> this(y,x)
      )
    )
  }

}


object Matrix{

  def apply[T](data:Array[Array[T]])(implicit Tmf:ClassManifest[T]) = new Matrix(data)


}