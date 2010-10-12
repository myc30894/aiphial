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

import javax.swing.JFrame
import java.awt.{Toolkit, Graphics2D, Graphics}

class Painter(val sWidth:Int,val sHeight:Int) extends JFrame {
  this.setTitle("Painter")
  
  var fs=8

  private[this] val dim = Toolkit.getDefaultToolkit().getScreenSize()
  this.setBounds(dim.width/fs,dim.height/fs,dim.width*(fs-2)/fs,dim.height*(fs-2)/fs )

  //this.setSize(100, 100);
  setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

  var painter: (Graphics2D, Float, Float) => Unit = (a, b, c) => {}


  def this(sWidth:Int,sHeight:Int,p: (Graphics2D, Float, Float) => Unit) {
    this (sWidth,sHeight)
    this.painter = p;
  }


  override def paint(g: Graphics) = {
    super.paint(g)

    val g2: Graphics2D = g.asInstanceOf[Graphics2D]

    val td = g2.create(10,30,getWidth-10,getHeight-10).asInstanceOf[Graphics2D]

    td.scale(getWidth / sWidth, getHeight / sHeight)

    if (painter != null)
      {
        painter(td, 1, 1)
      }

  }

}
