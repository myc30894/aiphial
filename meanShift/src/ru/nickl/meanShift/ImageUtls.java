/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author nickl
 */
public class ImageUtls {
    
    static BufferedImage toImage0(byte[] pixels, int w, int h)
    {
        DataBuffer db = new DataBufferByte(pixels, w * h * 3);
        WritableRaster raster = Raster.createPackedRaster(db,
                w, h, w, new int[]
                {
                    0xff0000, 0xff00, 0xff
                }, null);
        ColorModel cm =   new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
        return new BufferedImage(cm, raster, false, null);
    }

    public static BufferedImage toImage1(byte[] data, int w, int h)
    {
        DataBuffer buffer = new DataBufferByte(data, w * h);

        int pixelStride = 3; //assuming r, g, b, skip, r, g, b, skip...
        int scanlineStride = 3 * w; //no extra padding
        int[] bandOffsets =
        {
            0, 1, 2
        }; //r, g, b
        //WritableRaster raster = Raster.createInterleavedRaster(buffer, w, h, scanlineStride, pixelStride, bandOffsets, null);

        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        boolean hasAlpha = false;
        boolean isAlphaPremultiplied = false;
        int transparency = Transparency.OPAQUE;
        int transferType = DataBuffer.TYPE_BYTE;
        ColorModel colorModel = new ComponentColorModel(colorSpace, hasAlpha, isAlphaPremultiplied, transparency, transferType);
        
        SampleModel sm = new ComponentSampleModel(transferType, w, h, pixelStride, scanlineStride, bandOffsets);

        WritableRaster raster = WritableRaster.createWritableRaster(sm, buffer, null);

        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }
    
    public static BufferedImage toImage2(byte[] data, int w, int h) throws IOException
    {
        InputStream in = new ByteArrayInputStream(data);
        BufferedImage image = javax.imageio.ImageIO.read(in);

        return image;        
    }
    
    public static BufferedImage toImage(byte[] data, int w, int h)
    {
       
        
        
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        int[] nBits = {8, 8 ,8};
        ColorModel cm = new ComponentColorModel(cs, nBits, false, false,
                                                Transparency.OPAQUE,
                                                DataBuffer.TYPE_BYTE);
        SampleModel sm = cm.createCompatibleSampleModel(w, h);
        DataBufferByte db = new DataBufferByte(data, w*h);
        WritableRaster raster = Raster.createWritableRaster(sm, db, null);
        BufferedImage bm = new BufferedImage(cm, raster, false, null);

        return bm;
    }
    
    public static BufferedImage toImage5(byte[] data, int w, int h)
    {
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);

        ColorModel cm = bi.getColorModel();

        System.out.println(bi.getData().getDataBuffer());
        
        SampleModel sm = bi.getData().getSampleModel();
        
        DataBufferByte db = new DataBufferByte(data, w*h);

                WritableRaster raster = Raster.createWritableRaster(sm, db, new Point(0, 0));
        BufferedImage bm = new BufferedImage(cm, raster, false, null);

        return bm;        
        
    }
    
    public static BufferedImage toImage(int[] pixels, int w, int h)
    {
        DataBuffer db = new DataBufferInt(pixels, w * h);
        WritableRaster raster = Raster.createPackedRaster(db,
                w, h, w, new int[]{0xff0000, 0xff00, 0xff}, null);
        ColorModel cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
        return new BufferedImage(cm, raster, false, null);
    }

    
    public static BufferedImage toImage1(int[] data, int w, int h)
    {
        DataBuffer buffer = new DataBufferInt(data, w * h);

        int pixelStride = 4; //assuming r, g, b, skip, r, g, b, skip...
        int scanlineStride = 4 * w; //no extra padding
        int[] bandOffsets =
        {
            1, 2, 3
        }; //r, g, b
        WritableRaster raster = Raster.createInterleavedRaster(buffer, w, h, scanlineStride, pixelStride, bandOffsets, null);

        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        boolean hasAlpha = false;
        boolean isAlphaPremultiplied = false;
        int transparency = Transparency.OPAQUE;
        int transferType = DataBuffer.TYPE_INT;
        ColorModel colorModel = new ComponentColorModel(colorSpace, hasAlpha, isAlphaPremultiplied, transparency, transferType);
        
        SampleModel sm = new ComponentSampleModel(transferType, w, h, pixelStride, scanlineStride, bandOffsets);

        //WritableRaster raster = WritableRaster.createWritableRaster(sm, buffer, null);

        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }
       
    
    public static byte[] BGRtoRGB(byte [] bgr)
    {
        byte[] rgb = new byte[bgr.length];

        for (int i = 0; i < rgb.length; i+=3)
        {
            rgb[i]=bgr[i+2];
            rgb[i+1] = bgr[i+1];
            rgb[i+2] = bgr[i];

        }

        return rgb;

    }

}
