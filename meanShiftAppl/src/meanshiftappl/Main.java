/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package meanshiftappl;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import ru.nickl.meanShift.direct.*;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor;

/**
 *
 * @author nickl
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        /*
         Map<String, String> env = System.getenv();
        for (String string : env.keySet())
        {
        System.out.println(string+" - "+env.get(string));
        }
*/




        //System.getProperties().list(System.out);


        //System.out.println(System.getProperties().getProperty("java.class.path"));

        Properties pp = new Properties();






        pp.setProperty("python.home", "."+File.separator+"tmp");
        pp.setProperty("python.path", System.getProperties().getProperty("java.class.path")+File.pathSeparator+"/home/nickl/apps/jython2.5b0/Lib");

        PythonInterpreter.initialize(System.getProperties(), pp, args);

        
        //PySystemState systemState = new PySystemState();
        

        /*
        for (String string : System.getProperties().getProperty("java.class.path").split(File.pathSeparator))
        {
        systemState.path.append(new PyString(string));
        }
        
         */

        //systemState.path.append(new PyString("./"));


        PySystemState.add_classdir("."+File.separator+"lib");
        PySystemState.add_extdir("."+File.separator+"lib",true);
                
        PythonInterpreter interp = new PythonInterpreter();

        interp.execfile("main.py");

       /* MeanShiftFilter msf = (MeanShiftFilter) interp.get("a", MeanShiftFilter.class);
        
        LuvImageProcessor imageProcessor = new MeanShiftFilterImageProcessor(msf);

        System.out.println(msf.toString());
        System.out.println(imageProcessor.toString());*/

        //System.out.println("done");



    /*
    interp.exec("import sys");
    interp.exec("import tools");
    interp.exec("print sys.path");
    interp.exec("from ru.nickl.meanShift.direct import *");
    interp.set("a", new PyInteger(42));
    interp.exec("print a");
    interp.exec("x = 2+2");
    PyObject x = interp.get("x");
    System.out.println("x: " + x);*/

    }
}
