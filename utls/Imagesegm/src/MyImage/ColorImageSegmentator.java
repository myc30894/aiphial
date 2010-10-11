/*
 *
 * This file is part of Aiphial.
 *
 * Copyright (c) 2010 Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package MyImage;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class ColorImageSegmentator
{

    private static int[][] xKernel = new int[][]{
        {-1, 0, 1},
        {-2, 0, 2},
        {-1, 0, 1}};
    private static int[][] yKernel = new int[][]{
        {1, 2, 1},
        {0, 0, 0},
        {-1, -2, -1}};

    public static BufferedImage getColorGradient(Image src, double k)
    {
        try
        {
            int width = src.getWidth(null);
            int height = src.getHeight(null);


            //BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            PixelGrabber pg = new PixelGrabber(src, 0, 0, width, height, true);

            pg.grabPixels();

            int[] srcpixels = (int[]) pg.getPixels();

            int[] resPixels = new int[srcpixels.length];

            for (int i = width + 1; i < srcpixels.length - width - 1; i++)
            {

                ColorSrt[][] colormat = new ColorSrt[][]{
                    {new ColorSrt(srcpixels[i - width - 1]), new ColorSrt(srcpixels[i - width]), new ColorSrt(srcpixels[i - width + 1])},
                    {new ColorSrt(srcpixels[i - 1]), new ColorSrt(srcpixels[i]), new ColorSrt(srcpixels[i + 1])},
                    {new ColorSrt(srcpixels[i + width - 1]), new ColorSrt(srcpixels[i + width]), new ColorSrt(srcpixels[i + width + 1])}};


                ColorSrt dx = getGradValue(colormat, xKernel);
                ColorSrt dy = getGradValue(colormat, yKernel);

                double sum = dx.r * dx.r + dx.g * dx.g + dx.b * dx.b + dy.r * dy.r + dy.g * dy.g + dy.b * dy.b;

                int grad = (int) (Math.sqrt(sum) * k);

                if (grad > 255)
                {
                    grad = 255;
                }

                resPixels[i] = (0 << 24) | (grad << 16) | (grad << 8) | grad;

            }

            return toImage(resPixels, width, height);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(ColorImageSegmentator.class.getName()).log(Level.SEVERE, null, ex);

            return null;
        }
    }

    private static ColorSrt getGradValue(ColorSrt[][] colors, int[][] matrix)
    {
        int dr = 0;
        int dg = 0;
        int db = 0;

        for (int i = 0; i < 3; i++)
        {


            for (int j = 0; j < 3; j++)
            {
                dr += colors[i][j].r * matrix[i][j];
                dg += colors[i][j].g * matrix[i][j];
                db += colors[i][j].b * matrix[i][j];
            }

        }

        return new ColorSrt(dr, dg, db);
    }

    static BufferedImage toImage(int[] pixels, int w, int h)
    {
        DataBuffer db = new DataBufferInt(pixels, w * h);
        WritableRaster raster = Raster.createPackedRaster(db,
                w, h, w, new int[]{0xff0000, 0xff00, 0xff}, null);
        ColorModel cm = new DirectColorModel(24, 0xff0000, 0xff00, 0xff);
        return new BufferedImage(cm, raster, false, null);
    }
}

class ColorSrt
{
    int r = 0;
    int g = 0;
    int b = 0;

    public ColorSrt(int r, int g, int b)
    {
        this.r = r;
        this.b = b;
        this.g = g;
    }

    public ColorSrt(int code)
    {
        r = ((code & 0x00ff0000) >> 16);
        g = ((code & 0x0000ff00) >> 8);
        b = ((code & 0x000000ff) >> 0);
    }
}

