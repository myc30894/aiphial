/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift;

import java.io.File;
import org.junit.Ignore;

/**
 *
 * @author nickl
 */
@Ignore public class TestData {

    public static File getOutputFolder()
    {
        File result = new File("testresult");
        result.mkdirs();
        return result;
    }

    public static File getSourceImage()
    {
        return new File("DSCN4909s100.bmp");
    }

}
