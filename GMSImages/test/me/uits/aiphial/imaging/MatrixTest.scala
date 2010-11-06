/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package me.uits.aiphial.imaging

import org.junit._
import Assert._

class MatrixTest {

  @Before
  def setUp: Unit = {
  }

  @After
  def tearDown: Unit = {
  }

  @Test
  def equality = {

    assertTrue(new Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
      ==
      new Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
    )

    assertTrue(new Matrix(Array(
          Array(1,2,3),
          Array(4,5,6)
        )
      )
      ==
      new Matrix(Array(
          Array(1,2,3),
          Array(4,5,6)
        )
      )
    )

    assertFalse(new Matrix(Array(
          Array(1,5,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
      ==
      new Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
    )

    assertFalse(new Matrix(Array(
          Array(1,2,3),
          Array(4,5,6)
        )
      )
      ==
      new Matrix(Array(
          Array(1,2,3),
          Array(4,5,6),
          Array(7,8,9)
        )
      )
    )

  }

  @Test
  def submatrix = {
      
    val m = new Matrix(Array(
        Array(1,2,3),
        Array(4,5,6),
        Array(7,8,9)
      )     
    )

    assertEquals("",m.submatrix(1, 1, 3, 3),new Matrix(Array(
          Array(5,6),
          Array(8,9)
        ) ))

    assertEquals("",m.submatrix(0, 0, 3, 3),m)

    assertEquals("",m.submatrix(0, 0, 2, 2),new Matrix(Array(
          Array(1,2),
          Array(4,5)
        ) ))

    assertEquals("",m.submatrix(0, 1, 2, 3),new Matrix(Array(
          Array(2,3),
          Array(5,6)
        ) ))

  }


  @Test
  def join = {

    val m1 = new Matrix(Array(
        Array(1,2,3),
        Array(4,5,6)
      )
    )

    val m2 = new Matrix(Array(
        Array(1,1,2),
        Array(-3,-5,6)
      )
    )

    val sum = (m1 join m2)(_+_)


    assertEquals("",sum,new Matrix(Array(
          Array(2,3,5),
          Array(1,0,12)
        ) ))

  }

  @Test
  def reduce = {

    val m1 = new Matrix(Array(
        Array(1,2,3),
        Array(4,5,6)
      )
    )

    assertEquals("",m1.reduce(_+_),21)

    assertEquals("",m1.reduce(math.max),6)

  }

  @Test 
  def mapmask1():Unit = {

    val m1 = new Matrix(Array(
        Array(1,2,3,1,2),
        Array(4,5,6,4,5),
        Array(1,2,3,5,9),
        Array(4,5,6,3,7)
      )
    )

    val mask = new Matrix(Array(
        Array( 1,-1, 0),
        Array( 2, 2,-1),
        Array( 1, 2, 1)
      )
    )

    
    assertEquals("",m1.mapMask(mask)(_*_)(_+_),
    new Matrix(
      Array(
        Array(19,30,39),
        Array(22,24,28)
      )
    ))
  }


}

