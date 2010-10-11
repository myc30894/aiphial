/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanshift.direct.cuda;

import java.io.File;
import java.util.Arrays;
import ru.nickl.meanShift.direct.LUV;
import ru.nickl.meanShift.direct.LuvData;
import ru.nickl.meanShift.direct.filter.BaseMeanShiftFilter;

/**
 *
 * @author nickl
 */
public class NativeCudaMSFilter extends BaseMeanShiftFilter {

    static{
        //System.out.println(System.getenv("LD_LIBRARY_PATH"));
        //System.loadLibrary("cudart");

        try{
            System.loadLibrary("cudart");
        }
        catch(UnsatisfiedLinkError e)
        {
            try
            {
               System.load("/usr/local/cuda/lib64/libcudart.so");
            }
            catch(UnsatisfiedLinkError e1)
            {
                throw new RuntimeException(e.getMessage()+", "+e1.getMessage());
            }
        }


        //TODO: remove hardcode
        
        System.load(new File("../../CudaMeanShiftNative/dist/Debug/CUDA-Linux-x86/libcudameanshift.so").getAbsolutePath());
    }


    private int mode = 0;

    public LuvData filter(LuvData rawData) {

        final int numberofColors = 3;
        final int width = rawData.getWidth();
        final int height = rawData.getHeight();

        double[] srcArray = new double[height * width * numberofColors];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                LUV l = rawData.getLUV(x, y);
                srcArray[(y * width + x) * numberofColors + 0] = l.l;
                srcArray[(y * width + x) * numberofColors + 1] = l.u;
                srcArray[(y * width + x) * numberofColors + 2] = l.v;
            }
        }



        double[] resultArray = new double[height * width * numberofColors];

        doNativefilter(resultArray, srcArray, height, width, squareRange, colorRange, mode);

        LuvData result = new LuvData(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                result.setLUV(x, y, new LUV(
                        resultArray[(y * width + x) * numberofColors + 0],
                        resultArray[(y * width + x) * numberofColors + 1],
                        resultArray[(y * width + x)* numberofColors + 2]));

            }
        }

        return result;


    }

//    private void doNativefilter(double[] resultArray, double[] srcArray, int height, int width, short squareRange, double colorRange) {
//        for (int i = 0; i < srcArray.length; i++) {
//            resultArray[i] = srcArray[i];
//        }
//    }
    
    native private void doNativefilter(double[] resultArray, double[] srcArray, int height, int width, short squareRange, double colorRange, int mode);

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(int mode) {
        this.mode = mode;
    }


}
