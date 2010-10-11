/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.port;

import ru.nickl.meanShift.direct.filter.BaseMeanShiftFilter;
import ru.nickl.meanShift.direct.port.moveport.PortedFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import ru.nickl.meanShift.direct.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import ru.nickl.meanShift.direct.LUVConverter;
import ru.nickl.meanShift.direct.PointUtils;
import ru.nickl.meanShift.integer.CashingIS;
import ru.nickl.meanShift.integer.NPoint;

/**
 *
 * @author nickl
 */
public class Ported3Filter extends BaseMeanShiftFilter {

    private PrintStream LOG = null;
    protected int N = 3;
    //protected  double			h[];
    int L;
    final double EPSILON = 0.01;
    protected final int LIMIT = 100;
    FPoint[] NPointArray;
    Neigh neigh;

    Set<Point> endPoints = new HashSet<Point>();
    private LuvData LUVArray;
    private int height;
    private int width;
    private LuvData resultLUVArray;

    private void printEndPoints()
    {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        LUVConverter lUVConverter = new LUVConverter();

        for (Point point : endPoints)
        {
            output.setRGB(point.x, point.y, lUVConverter.LUVtoARGBint(point.c));
        }
        try
        {

            ImageIO.write(output, "bmp", new File("portedEndpoints.bmp"));
        } catch (IOException ex)
        {
            Logger.getLogger(CashingIS.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    @Override
    public LuvData filter(LuvData LUVArray) {

        this.LUVArray = LUVArray;

                height = LUVArray.getHeight();
        width = LUVArray.getWidth();

        try {
            LOG = new PrintStream("3.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PortedFilter.class.getName()).log(Level.SEVERE, null, ex);
        }


        L = height * width;

        resultLUVArray = new LuvData(width, height);
        try {
            NewNonOptimizedFilter(squareRange, colorRange);
        } finally {
            LOG.close();
        }

        printEndPoints();
        
        return  resultLUVArray;

    }
    double hiLTr;

    void NewNonOptimizedFilter(double sigmaS, double sigmaR) {

        // Declare Variables
        int iterationCount, i, j, k;
        //double mvAbs, diff, el;

        //make sure that a lattice height and width have
        //been defined...
        if (0 == height) {
            throw new IllegalStateException("Lattice height and width are undefined.");

        }

        //re-assign bandwidths to sigmaS and sigmaR


        //define input data dimension with lattice
        int lN = N + 2;

        // Traverse each data point applying mean shift
        // to each data point

        // Allcocate memory for yk
        //double yk[] = new double[lN];

        FPoint yk = new FPoint(0, 0, new LUV(0, 0, 0));

        // Allocate memory for Mh
        //double Mh[] = new double[lN];
        FPoint Mh = new FPoint();

        // let's use some temporary data
        //double[] sdata;
        //sdata = new double[lN * L];

        NPointArray = new FPoint[L];



        // copy the scaled data



        for (i = 0; i < L; i++) {
            LUV luv = LUVArray.getLUV(i % width, i / width);
            NPointArray[i] = new FPoint(((i % width) / sigmaS), ((i / width) / sigmaS), new LUV(luv.l / sigmaR, luv.u / sigmaR, luv.v / sigmaR));
        }

        neigh = new Neigh(width, height, NPointArray, sigmaS);

        hiLTr = 80.0 / sigmaR;
        // done indexing/hashing

        // proceed ...


        for (i = 0; i < L; i++) {

            // Assign window center (window centers are
            // initialized by createLattice to be the point
            // data[i])
            /*
            idxs = i * lN;
            for (j = 0; j < lN; j++)
            {
            yk[j] = sdata[idxs + j];
            }
             */

            yk = NPointArray[i].clone();





            // Calculate the mean shift vector using the lattice
            // LatticeMSVector(Mh, yk);
            /*****************************************************/
            // Initialize mean shift vector
            calcMh(Mh, yk);

            /*****************************************************/
            // Calculate its magnitude squared
            double mvAbs = 0;

            mvAbs = Dim(Mh, new FPoint());

            /*
            for (j = 0; j < lN; j++)
            {
            mvAbs += Mh[j] * Mh[j];
            }
             */
            // Keep shifting window center until the magnitude squared of the
            // mean shift vector calculated at the window center location is
            // under a specified threshold (Epsilon)

            // NOTE: iteration count is for speed up purposes only - it
            //       does not have any theoretical importance
            iterationCount = 1;

            while ((mvAbs >= EPSILON) && (iterationCount < LIMIT)) {

                yk.incrBy(Mh);

                // Calculate the mean shift vector at the new
                // window location using lattice
                // LatticeMSVector(Mh, yk);
                /*****************************************************/
                // Initialize mean shift vector
                calcMh(Mh, yk);

                mvAbs = calcmvAbs(Mh, sigmaS, mvAbs, sigmaR);

                // Increment interation count
                iterationCount++;
            }


            yk.incrBy(Mh);

            endPoints.add(new Point((short)(yk.x*sigmaS), (short)(yk.y*sigmaS),  new LUV(yk.c.l * sigmaR, yk.c.u * sigmaR, yk.c.v * sigmaR)));
            
            resultLUVArray.setLUV(i % width, i / width,  new LUV(yk.c.l * sigmaR, yk.c.u * sigmaR, yk.c.v * sigmaR));

        }


        return;

    }

    private void calcMh(FPoint Mh, FPoint yk) {
        double wsuml = 0;
        Mh.set(0, 0, 0, 0, 0);


        for (FPoint fPoint : neigh.getNeighbours(yk)) {


            // list parse, crt point is cHeadList
            for (FPoint curPoint : neigh.getSlist(fPoint)) {


                //int idxd = lN * idxd;
                // determine if inside search window
                double el = curPoint.x - yk.x;
                double diff = el * el;
                el = curPoint.y - yk.y;
                diff += el * el;

                if (diff < 1.0) {
                    el = curPoint.c.l - yk.c.l;
                    if (yk.c.l > hiLTr) {
                        diff = 4 * el * el;
                    } else {
                        diff = el * el;
                    }


                    el = curPoint.c.u - yk.c.u;
                    diff += el * el;
                    el = curPoint.c.v - yk.c.v;
                    diff += el * el;


                    if (diff < 1.0) {
                        double weight = 1;// - weightMap[idxd];

                        Mh.incrBy(curPoint);
                        /*
                        for (int k = 0; k < lN; k++)
                        {
                        Mh[k] += weight * NPointArray[idxs + k];
                        }*/
                        wsuml += weight;
                    }
                }

            }
        }

        if (wsuml > 0) {

            /*
            FPoint clone = Mh.clone();
            clone.divide((int)wsuml);

            Mh = clone.minus(yk);
             */

            Mh.divide(wsuml);
            Mh.decrBy(yk);

        /*
        for (int j = 0; j < lN; j++)
        {
        Mh[j] = Mh[j] / wsuml - yk[j];
        }*/
        } else {
            Mh.setNuls();
        }


    }

    protected static double Dim(FPoint a, FPoint b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + PointUtils.Dim(a.c, b.c);
    }

    private double calcmvAbs(double[] Mh, double sigmaS, double mvAbs, double sigmaR) {
        /*****************************************************/
        // Calculate its magnitude squared
        //mvAbs = 0;
        //for(j = 0; j < lN; j++)
        //	mvAbs += Mh[j]*Mh[j];
        mvAbs = (Mh[0] * Mh[0] + Mh[1] * Mh[1]) * sigmaS * sigmaS;
        if (N == 3) {
            mvAbs += (Mh[2] * Mh[2] + Mh[3] * Mh[3] + Mh[4] * Mh[4]) * sigmaR * sigmaR;
        } else {
            mvAbs += Mh[2] * Mh[2] * sigmaR * sigmaR;
        }
        return mvAbs;
    }

    private double calcmvAbs(FPoint Mh, double sigmaS, double mvAbs, double sigmaR) {
        /*****************************************************/
        // Calculate its magnitude squared
        //mvAbs = 0;
        //for(j = 0; j < lN; j++)
        //	mvAbs += Mh[j]*Mh[j];
        mvAbs = Dim(Mh, new FPoint()) * sigmaR * sigmaR;

        return mvAbs;
    }
}

