/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.uits.aiphial.imaging

import java.util.Arrays

class Matrix[T] private (private val data:Array[Array[T]])(implicit Tmf:ClassManifest[T]) {

  val height = data.length
  val width = data(0).length


  def apply(x:Int, y:Int)=data(x)(y)
  
  def map[B](f:T=>B)(implicit mf:ClassManifest[B]):Matrix[B]=new Matrix[B](    
    data.map(_.map(f))
  )


  def join[B,C](another:Matrix[B])(op:(T,B)=>C)(implicit mf:ClassManifest[C]) = { 
    
    require(this.height == another.height && this.width == another.width, "matrix must be the same size")

    new Matrix[C](    

    for ( (a,b) <- this.data zip another.data )
      yield for ( (a1,b1) <- a zip b )
        yield op(a1,b1)

  )}

  def reduce(f:(T,T)=> T) = data.flatten((a)=>a).reduceLeft(f)

  def submatrix(x1:Int, y1:Int, x2:Int, y2:Int ) = new Matrix[T](
    //TODO: implement this as view (not complex)
    data.slice(x1, x2+1).map(_.slice(y1, y2+1)).toArray
  )

  override def equals(arg0: Any) = arg0 match{

    case a: Matrix[T] => (a.data.length == this.data.length)  &&
      ((a.data zip this.data)
       forall((a)=>
          (a._1.length == a._2.length) &&
          ((a._1 zip a._2) forall((a)=>a._1== a._2))))
    case _ => false}
  

  override def toString = data map (_.mkString("Array(", ",", ")")) mkString("Matrix(Array(\n", ",\n", "\n))\n")


  def mapMask[A,T2](mask:Matrix[T2])(f:(T,T2)=>A)(reduce:(A,A)=>A)
  (implicit mf1:ClassManifest[A]):Matrix[A]=
  {

    val res = Array.ofDim[A](this.height-mask.height+1, this.width-mask.width+1)

    for(x <- 0+mask.height/2 until height-mask.height/2; y <- 0+mask.width/2 until width-mask.width/2)
    {
      val sbm = this.submatrix(x-mask.height/2, y-mask.width/2, x+mask.height/2, y+mask.width/2)

      res(x-mask.height/2)(y-mask.width/2) = (sbm join mask)(f).reduce(reduce)
      
    }

    new Matrix(res)
  }

  def sliding(h:Int, w:Int):Stream[Matrix[T]]={
    (for(x <- Stream.range(0+(h-1)/2,height-h/2); y <- Stream.range(0+(w-1)/2,width-w/2))
      yield  this.submatrix(x-(h-1)/2, y-(w-1)/2, x+h/2, y+w/2)
    )

  }


}


object Matrix{

       def apply[T](data:Array[Array[T]])(implicit Tmf:ClassManifest[T]) = new Matrix(data)


}