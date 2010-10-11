/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package opencvbind;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import cv.*;
import cxcore.*;
import highgui.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.File;
import javax.imageio.ImageIO;
import ml.*;

import static cv.CvLibrary.*;
import static cxcore.CxcoreLibrary.*;
import static highgui.HighguiLibrary.*;
import static ml.MlLibrary.*;

import static opencv.OpenCV.*;



/**
 *
 * @author nickl
 */
public class Main {

    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            BufferedImage bi = ImageIO.read(new File("/home/nickl/out2.bmp"));


//            final int width = bi.getWidth();
//            final int height = bi.getHeight();
//
//
//            IplImage img = cxcore.cvCreateImageHeader(new CvSize(width, height).byValue(),
//                    8, 3);
//
//
//            byte[] bytes = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
//
//
//            Memory mem = new Memory(bytes.length);
//            mem.write(0, bytes, 0, bytes.length);
//
//            cxcore.cvSetData(new CvArr(img.getPointer()), mem, width * 3);




           IplImage img = IplConvertor.fromBufferedImage(bi);


   //         IplImage img = highgui.cvLoadImage("/home/nickl/out2.bmp", CV_LOAD_IMAGE_UNCHANGED);


 //           Memory m = new Memory(10000);
 //           img.imageData.setPointer(m);
//            img.imageDataOrigin.setPointer(m);
//
            
            //highgui.cvNamedWindow("Example1", CV_WINDOW_AUTOSIZE);
            //highgui.cvShowImage("Example1", new CvArr(img.getPointer()));
            //System.in.read();
            //HighguiLibrary.INSTANCE.cvWaitKey(0);
            //cxcore.cvReleaseImage(new PointerByReference(img.getPointer()));
            highgui.cvSaveImage("/home/nickl/out2c.bmp", new CvArr(img.getPointer()));

            ImageIO.write(IplConvertor.toBufferedImage(img), "bmp", new File("/home/nickl/out2d.bmp"));

            //cxcore.cvReleaseImageHeader(new IplImage.ByReference[]{img.byReference()});
            cxcore.cvReleaseImage(new IplImage.ByReference[]{img.byReference()});
            highgui.cvDestroyWindow("Example1");


        } catch (Throwable th) {
            th.printStackTrace();
        }


    }
}
