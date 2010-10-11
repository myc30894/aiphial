/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gsmimages;

import java.io.File;
import java.util.Properties;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

/**
 *
 * @author Nickl
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

        Properties pp = new Properties();

        String pathtoScripts = ".." + File.separator + "GMSPy" + File.separator + "src";

        pp.setProperty("python.home", "." + File.separator + "tmp");
        pp.setProperty("python.path", System.getProperties().getProperty("java.class.path") + File.pathSeparator + pathtoScripts + File.pathSeparator + "C:\\Program Files\\jython2.5b1\\Lib" + File.pathSeparator + "C:\\Users\\Nickl\\Documents\\biotecnical\\Programs\\nbProjects\\GMSPy\\src");

        PythonInterpreter.initialize(System.getProperties(), pp, args);

        PySystemState.add_classdir("." + File.separator + "lib");
        PySystemState.add_extdir("." + File.separator + "lib", true);

        PythonInterpreter interp = new PythonInterpreter();





        //interp.execfile("C:\\Users\\Nickl\\Documents\\biotecnical\\Programs\\nbProjects\\GMSPy\\src\\GMSPy.py");

        //interp.execfile(pathtoScripts+"DataGenerator.py");
        //String scriptName = "GMSPyAgloAgloImgSearcher.py";

        String scriptName = "msfilterenhaus.py";

        if(args.length>1)
        {
            scriptName = args[1];
        }


        interp.execfile(pathtoScripts + File.separator + scriptName);

    }
}
