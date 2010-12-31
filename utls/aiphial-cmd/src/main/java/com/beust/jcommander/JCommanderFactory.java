/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.beust.jcommander;

/**
 *
 * @author nickl
 */
public class JCommanderFactory {
    public static JCommander createWithArgs(Object cmdLineArgs) {
        JCommander jCommander = new JCommander(cmdLineArgs);
        jCommander.addConverterFactory(new FloatConverterFactory());
        return jCommander;
    }
}
