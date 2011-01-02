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

package me.uits.aiphial.imaging.runner


import com.beust.jcommander.JCommander
import com.beust.jcommander.JCommanderFactory
import com.beust.jcommander.ParameterException
import java.io.File
import javax.imageio.ImageIO


object CliRunner {

  val comands = List(new AgloMSCli, new NaiveMSCli, new SearchCli)
  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {


    val jc = JCommanderFactory.createWithArgs(new Object)  

    for(c <- comands)
        jc.addCommand(c.name, c)
    
    try{  
      jc.parse(args:_*);

      comands.find(jc.getParsedCommand == _.name) match {      
        case Some(c) => c.process;
        case None => jc.usage();
      }
    }
    catch{
      
      case ex:ParameterException => {
          System.out.println(ex.getLocalizedMessage);
          if(jc.getParsedCommand!=null)
          jc.usage(jc.getParsedCommand)
        else
          jc.usage
        }
      
      
    }

  }


}

 import com.beust.jcommander.Parameter;
class MainData()
{
    @Parameter(names = Array("-i"), description = " input file name", required = true)
    var inputfile:String = "../../images/smallgisto.jpg";

    @Parameter(names = Array("-o"), description = " output file name")
    var outfile:String = "out";

}