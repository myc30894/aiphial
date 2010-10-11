/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct;

import ru.nickl.meanShift.direct.LUV;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.nickl.meanShift.ImageUtls;

/**
 *
 * @author nickl
 */
public class LUVConverter
{

    final private double Yn = 1.00000;
    final private double Zn = 1.08870;
    final private double Un_prime = 0.19784977571475;
    final private double Vn_prime = 0.46834507665248;
    final private double Lt = 0.008856;
    final private double XYZ[][] =
    {
        {
            0.4125, 0.3576, 0.1804
        },
        {
            0.2125, 0.7154, 0.0721
        },
        {
            0.0193, 0.1192, 0.9502
        }
    };    //LUV to RGB conversion
    final private double RGB[][] =
    {
        {
            3.2405, -1.5371, -0.4985
        },
        {
            -0.9693, 1.8760, 0.0416
        },
        {
            0.0556, -0.2040, 1.0573
        }
    };

    public LUV[] toLUVArray(BufferedImage bufferedImage)
    {
        try
        {
            PixelGrabber pg = new PixelGrabber(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), true);

            pg.grabPixels();

            int[] gra = (int[]) pg.getPixels();

            LUV[] result = new LUV[bufferedImage.getHeight() * bufferedImage.getWidth()];


            for (int i = 0; i < gra.length; i++)
            {

                int a = (gra[i] & 0xff000000) >> 24;
                int r = (gra[i] & 0x00ff0000) >> 16;
                int g = (gra[i] & 0x0000ff00) >> 8;
                int b = (gra[i] & 0x000000ff) >> 0;

                //System.out.println("a:"+a);
                if (a == 0)
                {
                    result[i] = null;
                } else
                {
                    result[i] = RGBtoLUV(r, g, b);
                }

            }

            return result;
        } catch (InterruptedException ex)
        {
            Logger.getLogger(LUVConverter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public LUV[][] toLUVDArray(BufferedImage img)
    {
        int height = img.getHeight();
        int width = img.getWidth();

        LUV[] linearluv = toLUVArray(img);
        LUV[][] result = new LUV[height][width];

        for (int i = 0; i < linearluv.length; i++)
        {
            result[i / width][i % width] = linearluv[i];
        }

        return result;
    }

    public BufferedImage LUVArrayToBufferedImage(LUV[][] arg)
    {
        int h = arg.length;
        int w = arg[0].length;

        LUV[] luvarray = new LUV[w * h];

        for (int i = 0; i < arg.length; i++)
        {
            for (int j = 0; j < arg[0].length; j++)
            {
                luvarray[i * w + j] = arg[i][j];
            }

        }

        return LUVArrayToBufferedImage(luvarray, h, w);

    }

    public BufferedImage LUVArrayToBufferedImage(LUV[] luvarray, int height, int width)
    {

        int[] resPixels = new int[height * width];
        //PrintStream opixelsStream = new PrintStream("opicels.txt");
        for (int i = 0; i < resPixels.length; i++)
        {
            //opixelsStream.printf("[%d] r=\t%d g=\t%d b=\t%d\n", i, result[3 * i], result[3 * i + 1], result[3 * i + 2]);
            resPixels[i] = LUVtoARGBint(luvarray[i]);
        }
        //opixelsStream.close();
        //File fout = new File("ints.txt");
        //PrintStream fos = new PrintStream(fout);
       /* for (int i = 0; i < resPixels.length; i++)
        {
        fos.print('[');
        fos.print(i);
        fos.println(']');
        fos.println(gra[i]);
        fos.println(resPixels[i]);
        fos.println();
        }
        fos.close();
        System.out.println(fout.getPath());*/

        return ImageUtls.toImage(resPixels, width, height);

        //ImageIO.write(toImage(resPixels, width, height), "bmp", new File("/home/nickl/NetBeansProjects/cpp/meanShift/filt.bmp"));

    }

    public LUV RGBtoLUV(int r, int g, int b)
    {

        LUV res = new LUV();


        //delcare variables
        double x, y, z, L0, u_prime, v_prime, finalant;

        //convert RGB to XYZ...
        x = XYZ[0][0] * r + XYZ[0][1] * g + XYZ[0][2] * b;
        y = XYZ[1][0] * r + XYZ[1][1] * g + XYZ[1][2] * b;
        z = XYZ[2][0] * r + XYZ[2][1] * g + XYZ[2][2] * b;

        //convert XYZ to LUV...

        //compute L*
        L0 = y / (255.0 * Yn);
        if (L0 > Lt)
        {
            res.l = (116.0 * (Math.pow(L0, 1.0 / 3.0)) - 16.0);
        } else
        {
            res.l = (903.3 * L0);
        }

        //compute u_prime and v_prime
        finalant = x + 15 * y + 3 * z;
        if (finalant != 0)
        {
            u_prime = (4 * x) / finalant;
            v_prime = (9 * y) / finalant;
        } else
        {
            u_prime = 4.0;
            v_prime = 9.0 / 15.0;
        }

        //compute u* and v*
        res.u = (13 * res.l * (u_prime - Un_prime));
        res.v = (13 * res.l * (v_prime - Vn_prime));

        //done.
        return res;
    }

    public int LUVtoARGBint(LUV luv)
    {

        if (luv == null)
        {
            luv = new LUV();
        }
        //declare variables...
        int r, g, b;
        double x, y, z, u_prime, v_prime;

        //perform conversion
        if (luv.l < 0.1)
        {
            r = g = b = 0;
        } else
        {
            //convert luv to xyz...
            if (luv.l < 8.0)
            {
                y = Yn * luv.l / 903.3;
            } else
            {
                y = (luv.l + 16.0) / 116.0;
                y *= Yn * y * y;
            }

            u_prime = luv.u / (13 * luv.l) + Un_prime;
            v_prime = luv.v / (13 * luv.l) + Vn_prime;

            x = 9 * u_prime * y / (4 * v_prime);
            z = (12 - 3 * u_prime - 20 * v_prime) * y / (4 * v_prime);

            //convert xyz to rgb...
            //[r, g, b] = RGB*[x, y, z]*255.0
            r = (int) Math.round((RGB[0][0] * x + RGB[0][1] * y + RGB[0][2] * z) * 255.0);
            g = (int) Math.round((RGB[1][0] * x + RGB[1][1] * y + RGB[1][2] * z) * 255.0);
            b = (int) Math.round((RGB[2][0] * x + RGB[2][1] * y + RGB[2][2] * z) * 255.0);

            //check bounds...
            if (r < 0)
            {
                r = 0;
            }
            if (r > 255)
            {
                r = 255;
            }
            if (g < 0)
            {
                g = 0;
            }
            if (g > 255)
            {
                g = 255;
            }
            if (b < 0)
            {
                b = 0;
            }
            if (b > 255)
            {
                b = 255;
            }

        }

        return (0xff << 24) | (r << 16) | (g << 8) | b;
    }
}
