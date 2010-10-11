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

package imagescaler;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ReplicateScaleFilter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Nicolay Mitropolsky <NicolayMitropolsky@gmail.com>
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException
    {
        String imgname = "DSC00104s400";

        BufferedImage img = ImageIO.read(new File(imgname + ".bmp"));

    

        for (double k = 0.1; k <= 1; k+=0.01)
        {
            int origh = img.getHeight();
            int origw = img.getWidth();

            ReplicateScaleFilter sf = new AreaAveragingScaleFilter((int) (origw * k), (int) (origh * k));

            Image res = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(img.getSource(), sf));

            int w = res.getWidth(null);
            int h = res.getHeight(null);
            int type = BufferedImage.TYPE_INT_RGB;  // other options
            BufferedImage dest = new BufferedImage(w, h, type);
            Graphics2D g2 = dest.createGraphics();
            g2.drawImage(res, 0, 0, null);
            g2.dispose();

            String kstr = String.format("%.2f", k);
            
            ImageIO.write(dest, "bmp", new File("out" + File.separator + imgname + "x" + kstr + ".bmp"));
        }

    }
}
