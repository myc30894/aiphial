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
public class IplConvertor {

    public static IplImage fromBufferedImage(BufferedImage bi) {

        final int width = bi.getWidth();
        final int height = bi.getHeight();


        IplImage img = cxcore.cvCreateImage(new CvSize(width, height).byValue(),
                8, 3);


        byte[] bytes = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();

        img.imageData.getPointer().write(0, bytes, 0, bytes.length);

        cxcore.cvSetData(new CvArr(img.getPointer()), img.imageData.getPointer(), width * 3);

//        Memory mem = new Memory(bytes.length);
//        mem.write(0, bytes, 0, bytes.length);

        // cxcore.cvSetData(new CvArr(img.getPointer()), mem, width * 3);

        //mem.clear();


        return img;
    }

    /**
     * from cv4hci project http://www.cim.mcgill.ca/
     * @author Samuel Audet
     */
    static private class BufferedIplImage extends BufferedImage {

        /**
         * Converts an IplImage type to a BufferedImage type.
         *
         * @param image     input IplImage whose type we are interested in
         * @return          corresponding type of BufferedImage (-1 == incompatible)
         */
        static public int getImageType(IplImage image) {
            int imageType = -1;
            if (image.depth == IPL_DEPTH_8U && image.nChannels == 3) {
                imageType = TYPE_3BYTE_BGR;
            } else if (image.depth == IPL_DEPTH_1U && image.nChannels == 1) {
                imageType = TYPE_BYTE_BINARY;
            } else if (image.depth == IPL_DEPTH_8U && image.nChannels == 1) {
                imageType = TYPE_BYTE_GRAY;
            } else if (image.depth == IPL_DEPTH_8U && image.nChannels == 4) {
                imageType = TYPE_INT_ARGB; // watch out, possible endian problems here...
            } else if (image.depth == IPL_DEPTH_16U && image.nChannels == 1) {
                imageType = TYPE_USHORT_GRAY;
            }
            // other combinations and types are not cross-compatible...
            return imageType;
        }

        /**
         * BufferedIplImage constructor specifiying the IplImage to copy from.
         * Note: No exception is thrown, but it might well be the case that
         * the IplImage provided has no BufferedImage equivalent, in which case
         * it will not be copied.
         */
        public BufferedIplImage(IplImage image) {
            super(image.width, image.height, getImageType(image));
            update(image);
        }

        /**
         * Updates this BufferedIplImage with new data from the IplImage. If the size
         * or the type of the image is different from us, false will be returned.
         *
         * @param image     new data to copy to us
         * @return          true if IplImage was compatible with us, otherwise false
         */
        public boolean update(IplImage image) {
            if (getWidth() != image.width || getHeight() != image.height
                    || getImageType(image) != getType()) {
                return false; // IplImage not compatible with this BufferedImage
            }
            DataBuffer dataBuffer = getRaster().getDataBuffer();
            byte[] myData = null;
            switch (dataBuffer.getDataType()) {
                case DataBuffer.TYPE_BYTE:
                    myData = ((DataBufferByte) dataBuffer).getData();
                    break;
                case DataBuffer.TYPE_DOUBLE:
                case DataBuffer.TYPE_FLOAT:
                case DataBuffer.TYPE_INT:
                case DataBuffer.TYPE_SHORT:
                case DataBuffer.TYPE_USHORT:
                    // no easy way to get a byte[] from those in Java... TBA
                    return false;
            }
            // note: there could still be a problem with the image padding needed
            // for alignment... this would require more processing than a simple
            // "dump"
            //image.dumpData(myData);

            image.imageData.getPointer().read(0, myData, 0, myData.length);

            // java only supports top-left images... flip it if necessary
            if (image.origin == IPL_ORIGIN_BL) {
                int width = myData.length / getHeight();
                byte[] aRow = new byte[width];
                for (int i = 0; i < (myData.length - width) / 2; i += width) {
                    int j = myData.length - i - width;
                    System.arraycopy(myData, i, aRow, 0, width);
                    System.arraycopy(myData, j, myData, i, width);
                    System.arraycopy(aRow, 0, myData, j, width);
                }
            }
            return true;
        }
    }

    public static BufferedImage toBufferedImage(IplImage image) {
        return new BufferedIplImage(image);
    }
}
