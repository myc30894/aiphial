

package me.uits.aiphial.imaging.runner

import org.github.scopt.OptionParser
import org.clapper.argot._
import java.io.File
import javax.imageio.ImageIO
import org.clapper.argot.ArgotConverters._

object CliRunner {

  /**
   * @param args the command line arguments
   */
  def main(args: Array[String]): Unit = {

//    var inputfile:String = null;
//    var outfile:String = "out.bmp";
//    var method:String = null;
//
//
//    val parser = new OptionParser("aiphial-cli") {
//      opt("i", "input", "<file>", "input image filename", {v: String => inputfile = v})
//      opt("o", "output", "<file>", "cluster imagefilename", {v: String => outfile = v})
//      opt("m", "method", "xyz is a boolean property", {v: String => method = v})
//      keyValueOpt("p", "param", "<paramname>", "<paramvalue>", "method-dependant parametrs",
//                  {(key: String, value: String) => { println(key+"->"+value)} })
//      //arg("<singlefile>", "<singlefile> is an argument", {v: String => config.whatnot = v})
//      // arglist("<file>...", "arglist allows variable number of arguments",
//      //   {v: String => config.files = (v :: config.files).reverse })
//    }
//    if (parser.parse(args)) {
//
//      println(inputfile+", "+outfile+","+ method);
//
//
//    }
//    else {
//      // arguments are bad, usage message will have been displayed
//    }

    val parser = new ArgotParser("cooltool", preUsage=Some("Version 1.0"))
    
   

    val outfile = parser.option[String](List("o", "output"), "filename",
                                        "cluster image filename")

    val inputfile = parser.option[String](List("i", "iterations"), "filename",
                                          "input image filename")

    val method = parser.option[String](List("m", "method"), "methodname",
                                       "clustering method name")

    
    val method0 = parser.option[String](List("m", "method"), "methodname",
                                       "clustering method name")
   

    val clusterer = new NaiveMSCli(parser)

    parser.parse(args)

    println(inputfile.value+", "+outfile.value+","+ method.value +","+method0.value  );
 
    val srcimg = ImageIO.read(new File(inputfile.value.get))

    val resimage = clusterer.process(srcimg)

    ImageIO.write(resimage, "png", new File("out00000.png"))

  }

}
