/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.uits.aiphial.imaging

import org.junit._
import Assert._
import scala.collection.mutable.ArrayBuffer

class MatrixTest {

  @Before
  def setUp: Unit = {
  }

  @After
  def tearDown: Unit = {
  }

  @Test
  def equality = {

    assertTrue(Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
      ==
      Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
    )

    assertTrue(Matrix(Array(
          Array(1,2,3),
          Array(4,5,6)
        )
      )
      ==
      Matrix(Array(
          Array(1,2,3),
          Array(4,5,6)
        )
      )
    )

    assertFalse(Matrix(Array(
          Array(1,5,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
      ==
      Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
    )

    assertFalse(Matrix(Array(
          Array(1,2,3),
          Array(4,5,6)
        )
      )
      ==
      Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
    )

  }

  @Test
  def submatrix = {
      
    val m = Matrix(Array(
        Array(1,2,3),
        Array(4,5,6),
        Array(7,8,9)
      )     
    )

    assertEquals("",m.submatrix(1, 1, 2, 2),Matrix(Array(
          Array(5,6),
          Array(8,9)
        ) ))

    assertEquals("",m.submatrix(0, 0, 2, 2),m)

    assertEquals("",m.submatrix(0, 0, 1, 1),Matrix(Array(
          Array(1,2),
          Array(4,5)
        ) ))

    assertEquals("",m.submatrix(0, 1, 1, 2),Matrix(Array(
          Array(2,3),
          Array(5,6)
        ) ))

  }


  @Test
  def join = {

    val m1 = Matrix(Array(
        Array(1,2,3),
        Array(4,5,6)
      )
    )

    val m2 = Matrix(Array(
        Array(1,1,2),
        Array(-3,-5,6)
      )
    )

    val sum = (m1 join m2)(_+_)


    assertEquals("",sum,Matrix(Array(
          Array(2,3,5),
          Array(1,0,12)
        ) ))

  }

   @Test
  def toLine = {

    val m = Matrix(Array(
        Array(1,2,3),
        Array(4,5,6),
        Array(7,8,9)
      )
    )

    assertEquals("",Seq(1,2,3,4,5,6,7,8,9),m.asOneLine.toSeq)
  }

   @Test
  def toLinewithindex = {

    val m = Matrix(Array(
        Array(1,2,3),
        Array(4,5,6),
        Array(7,8,9)
      )
    )

    assertEquals("",Seq((0,0,1),(0,1,2),(0,2,3),(1,0,4),(1,1,5),(1,2,6),(2,0,7),(2,1,8),(2,2,9)),m.asOneLineWithIndex.toSeq)
  }
   @Test
  def foreachTest = {

    val m = Matrix(Array(
        Array(1,2,3),
        Array(4,5,6),
        Array(7,8,9)
      )
    )

    val ab = new ArrayBuffer[(Int,Int,Int)](9);

    m.foreach((x,y,v)=> ab.append((x,y,v)))

    assertEquals("",Seq((0,0,1),(0,1,2),(0,2,3),(1,0,4),(1,1,5),(1,2,6),(2,0,7),(2,1,8),(2,2,9)),ab)
  }

  @Test
  def reduce = {

    val m1 = Matrix(Array(
        Array(1,2,3),
        Array(4,5,6)
      )
    )

    assertEquals("",m1.reduce(_+_),21)

    assertEquals("",m1.reduce(math.max),6)

  }

  @Test 
  def mapmask():Unit = {

    val m1 = Matrix(Array(
        Array(1,2,3,1,2),
        Array(4,5,6,4,5),
        Array(1,2,3,5,9),
        Array(4,5,6,3,7)
      )
    )

    val mask = Matrix(Array(
        Array( 1,-1, 0),
        Array( 2, 2,-1),
        Array( 1, 2, 1)
      )
    )

    
    assertEquals("",m1.mapMask(mask)(_*_)(_+_),
                 Matrix(
        Array(
          Array(19,30,39),
          Array(22,24,28)
        )
      ))
  }

  @Test
  def convolve():Unit = {

    val m1 = Matrix(Array(
        Array(1,2,3,1,2),
        Array(4,5,6,4,5),
        Array(1,2,3,5,9),
        Array(4,5,6,3,7)
      )
    )

    val mask = Matrix(Array(
        Array( 1,-1, 0),
        Array( 2, 2,-1),
        Array( 1, 2, 1)
      )
    )


    assertEquals("",m1.convolve(mask),
                 Matrix(
        Array(
          Array(19,30,39),
          Array(22,24,28)
        )
      ))
  }


  @Test
  def windowingMap = {
    val m1 = Matrix(Array(
        Array(1,2,3,1,2),
        Array(4,5,6,4,5),
        Array(1,2,3,5,9),
        Array(4,5,6,3,7)
      )
    )
    assertEquals(Matrix(Array(
          Array(5,6,6,5),
          Array(5,6,6,9),
          Array(5,6,6,9)
        )
      ),
      m1.windowingMap(2,2)((m) =>  m.reduce(math.max(_, _))))

  }

  @Test
  def  withinwindow = {
    val m1 = Matrix(Array(
        Array(1,2,3,1,2),
        Array(4,5,6,4,5),
        Array(1,2,3,5,9),
        Array(4,5,6,3,7)
      )
    )

    assertEquals(Matrix(Array(
          Array(1,2),
          Array(4,5)
        )
      ),
      m1.getWithinWindow((0,0), 2, 2))

    assertEquals(Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(1,2,3)
        )
      ),
      m1.getWithinWindow((1,1), 3, 3))

    assertEquals(Matrix(Array(
          Array(3,1,2),
          Array(6,4,5),
          Array(3,5,9)
        )
      ),
      m1.getWithinWindow((1,3), 3, 3))


  }



  @Test
  def  iterator = {

    val m1 = Matrix(Array(
        Array(1,2,3,1,2),
        Array(4,5,6,4,5),
        Array(1,2,3,5,9),
        Array(4,5,6,3,7)
      )
    )

    //println("sliding" + m1.sliding(2,3).mkString(",\n"))

    assertEquals(Seq(
        Matrix(Array(
            Array(1,2,3),
            Array(4,5,6)
          ))
        ,
        Matrix(Array(
            Array(2,3,1),
            Array(5,6,4)
          ))
        ,
        Matrix(Array(
            Array(3,1,2),
            Array(6,4,5)
          ))
        ,
        Matrix(Array(
            Array(4,5,6),
            Array(1,2,3)
          ))
        ,
        Matrix(Array(
            Array(5,6,4),
            Array(2,3,5)
          ))
        ,
        Matrix(Array(
            Array(6,4,5),
            Array(3,5,9)
          ))
        ,
        Matrix(Array(
            Array(1,2,3),
            Array(4,5,6)
          ))
        ,
        Matrix(Array(
            Array(2,3,5),
            Array(5,6,3)
          ))
        ,
        Matrix(Array(
            Array(3,5,9),
            Array(6,3,7)
          ))), m1.sliding(2,3))



    assertEquals(Seq(
        Matrix(Array(
            Array(1,2,3),
            Array(4,5,6),
            Array(1,2,3)
          ))
        ,
        Matrix(Array(
            Array(2,3,1),
            Array(5,6,4),
            Array(2,3,5)
          ))
        ,
        Matrix(Array(
            Array(3,1,2),
            Array(6,4,5),
            Array(3,5,9)
          ))
        ,
        Matrix(Array(
            Array(4,5,6),
            Array(1,2,3),
            Array(4,5,6)
          ))
        ,
        Matrix(Array(
            Array(5,6,4),
            Array(2,3,5),
            Array(5,6,3)
          ))
        ,
        Matrix(Array(
            Array(6,4,5),
            Array(3,5,9),
            Array(6,3,7)

          ))), m1.sliding(3,3))    
  }

  @Test
  def border = {

    val m1 = Matrix(Array(
        Array(1,2,3,1,2),
        Array(4,5,6,4,5),
        Array(1,2,3,5,9),
        Array(4,5,6,3,7)
      )
    )

    assertEquals(Matrix(Array(
          Array(5,6,4),
          Array(2,3,5)
        )), m1.chopBorder(1,1))


    assertEquals(Matrix(Array(
          Array(5,5,5,5,5,5,5,5,5,5,5),
          Array(5,5,5,5,5,5,5,5,5,5,5),
          Array(5,5,5,1,2,3,1,2,5,5,5),
          Array(5,5,5,4,5,6,4,5,5,5,5),
          Array(5,5,5,1,2,3,5,9,5,5,5),
          Array(5,5,5,4,5,6,3,7,5,5,5),
          Array(5,5,5,5,5,5,5,5,5,5,5),
          Array(5,5,5,5,5,5,5,5,5,5,5)  )
      ), m1.addBorder(2,3,5))


    assertEquals(m1.addBorder(3, 8, 0).chopBorder(3, 8),m1)


  }

  @Test
  def rotate = {
    val m = Matrix(Array(
        Array(5,6,4),
        Array(2,3,5)
      ))

    assertEquals(
      Matrix(Array(
          Array(4,5),
          Array(6,3),
          Array(5,2)
        )),m.rotateCounterClockwise())
    
    assertEquals(
      Matrix(Array(
          Array(2,5),
          Array(3,6),
          Array(5,4)
        )),m.rotateClockwise())
    
    assertEquals(
      Matrix(Array(
        Array(5,3,2),
        Array(4,6,5)
        )),m.rotateReflex())

  }


}

