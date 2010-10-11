/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.integer;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import ru.nickl.meanShift.direct.LUVConverter;

/**
 *
 * @author nickl
 */
public class CashingIS extends NImageSegmentator{

    
    
    Map<NPoint, NPoint> cashed = new HashMap<NPoint, NPoint>();
    
    Set<NPoint> endNPoints = new HashSet<NPoint>();
    
    @Override
    protected NPoint calkEndPoint(short x, short y)
    {
        way = new HashSet<NPoint>();
        NPoint endPoint = super.calkEndPoint(x, y);
        
        for (NPoint nPoint : way)
        {
            cashed.put(nPoint, endPoint);
        }
        
        endNPoints.add(endPoint);
       
        return endPoint;
        
    }

    Set<NPoint> way = null;
    
    @Override
    protected NPoint calkMh(NPoint old)
    {

        NPoint get = getNeares(old);
        if(get!=null) 
        {
            System.out.print("1-");
            return get;
        }
        
        way.add(old.clone());        
        
        return super.calkMh(old);
    }

    @Override
    public BufferedImage getProcessedImage()
    {
        printBasins();
        return super.getProcessedImage();
    }

    private NPoint getNeares(NPoint old)
    {
        return cashed.get(old);
    }

    private boolean isnear(NPoint nPoint, NPoint old)
    {
        return Math.abs(nPoint.x-nPoint.x)<squareRange && Math.abs(nPoint.y-nPoint.y)<squareRange && Dim(nPoint.c, old.c)<colorRangeP2;
    }
    
    private void printBasins()
    {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        LUVConverter lUVConverter = new LUVConverter(); 
        
        for (NPoint point : endNPoints)
        {
            output.setRGB(point.x, point.y, lUVConverter.LUVtoARGBint(toLUV(point.c)));
        }
        try
        {

            ImageIO.write(output, "bmp", new File("myEndpoints.bmp"));
        } catch (IOException ex)
        {
            Logger.getLogger(CashingIS.class.getName()).log(Level.SEVERE, null, ex);
        }
                
     
    }
   
    

}
