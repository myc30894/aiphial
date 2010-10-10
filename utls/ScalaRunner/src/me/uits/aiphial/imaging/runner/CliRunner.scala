

package me.uits.aiphial.imaging.runner


import com.beust.jcommander.JCommander
import com.beust.jcommander.JCommanderFactory
import com.beust.jcommander.ParameterException
import java.io.File
import javax.imageio.ImageIO


object CliRunner {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {
    

    val maindata = new MainData()
    val ns = new NaiveMSCli()
    
    val jc = JCommanderFactory.createWithArgs(Array(maindata,ns))
    
    try{
    

      jc.parse(args:_*);
    
      val srcimg = ImageIO.read(new File(maindata.inputfile))

      val resimage = ns.process(srcimg)

      ImageIO.write(resimage, "png", new File(maindata.outfile+".png"))

    }
    catch{
      
      case ex:ParameterException => {
          System.err.println(ex.getLocalizedMessage);
          jc.usage()
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