/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanshift.direct.cuda;

import ru.nickl.meanShift.direct.*;
import javax.imageio.*;
import java.io.*;
import ru.nickl.meanShift.direct.filter.MeanShiftFilter;
import ru.nickl.meanShift.direct.filter.MeanShiftFilterImageProcessor;
import ru.nickl.meanShift.direct.filter.SimpleMSFilter;

/**
 *
 * @author nickl
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {



        NativeCudaMSFilter a = new NativeCudaMSFilter();
        a.setMode(1);
        a.setColorRange(7);
        a.setSquareRange((short)7);

        MeanShiftFilterImageProcessor msp = new MeanShiftFilterImageProcessor(a);

        //msp.setSourceImage(javax.imageio.ImageIO.read(new java.io.File("/media/disk-1/Nickl/photoes/avas/ava1smallbwbg.bmp")));
        //msp.setSourceImage(javax.imageio.ImageIO.read(new java.io.File("/home/nickl/biotecnical/Programs/IdeaProjects/images/DSC00104s241.bmp")));

        //msp.setSourceImage(javax.imageio.ImageIO.read(new java.io.File("/home/nickl/biotecnical/Programs/IdeaProjects/images/DSC00104.JPG")));
        msp.setSourceImage(javax.imageio.ImageIO.read(new java.io.File("/home/nickl/biotecnical/Programs/IdeaProjects/images/DSC00104s200.bmp")));
        
        
        msp.process();
        javax.imageio.ImageIO.write(msp.getProcessedImage(), "bmp", new java.io.File("/home/nickl/out.bmp"));

    }
}
