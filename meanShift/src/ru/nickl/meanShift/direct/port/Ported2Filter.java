/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.port;

import ru.nickl.meanShift.direct.filter.BaseMeanShiftFilter;
import ru.nickl.meanShift.direct.port.moveport.PortedFilter;
import ru.nickl.meanShift.direct.*;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.nickl.meanShift.direct.PointUtils;


/**
 *
 * @author nickl
 */
public class Ported2Filter extends BaseMeanShiftFilter {

    private PrintStream LOG = null;
    protected int N = 3;
    //protected  double			h[];
    int L;
    final double EPSILON = 0.01;
    protected final int LIMIT = 100;
    FPoint[] NPointArray;
    private int height;
    private int width;
    private LuvData LUVArray;
    private LuvData resultLUVArray;

    @Override
    public LuvData filter(LuvData LUVArray) {

        this.LUVArray = LUVArray;

                height = LUVArray.getHeight();
        width = LUVArray.getWidth();


        try {
            LOG = new PrintStream("2.txt");
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

        return resultLUVArray;

    }

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
        int idxs, idxd;
        idxs = idxd = 0;

        for (i = 0; i < L; i++) {
            LUV luv = LUVArray.getLUV(i % width, i / width);
            NPointArray[i] = new FPoint(((i % width) / sigmaS), ((i / width) / sigmaS), new LUV(luv.l / sigmaR, luv.u / sigmaR, luv.v / sigmaR));
        }

        // index the data in the 3d buckets (x, y, L)
        int[] buckets;
        int[] slist;
        slist = new int[L];
        int bucNeigh[] = new int[27];

        double minL; // just for L
        
        // just for L
        double maxX = width / sigmaS;
        double maxY = height / sigmaS;
        double maxL = minL = NPointArray[0].c.l;
        idxs = 2;
        double cval;
        for (i = 0; i < L; i++) {
            cval = NPointArray[i].c.l;
            if (cval < minL) {
                minL = cval;
            } else if (cval > maxL) {
                maxL = cval;
            }

            idxs += lN;
        }



        int xNBuck, yNBuck, lNBuck;
        int xBuck, yBuck, lBuck, cBuck;
        xNBuck = (int) (maxX + 3);
        yNBuck = (int) (maxY + 3);
        lNBuck = (int) (maxL - minL + 3);
        buckets = new int[xNBuck * yNBuck * lNBuck];
        for (i = 0; i < (xNBuck * yNBuck * lNBuck); i++) {
            buckets[i] = -1;
        }

        idxs = 0;
        for (i = 0; i < L; i++) {
            // find bucket for current data and add it to the list
            xBuck = (int) NPointArray[i].x + 1;
            yBuck = (int) NPointArray[i].y + 1;
            lBuck = (int) (NPointArray[i].c.l - minL) + 1;
            cBuck = xBuck + xNBuck * (yBuck + yNBuck * lBuck);

            slist[i] = buckets[cBuck];
            buckets[cBuck] = i;

            idxs += lN;
        }
        // init bucNeigh
        idxd = 0;
        for (xBuck = -1; xBuck <= 1; xBuck++) {
            for (yBuck = -1; yBuck <= 1; yBuck++) {
                for (lBuck = -1; lBuck <= 1; lBuck++) {
                    bucNeigh[idxd++] = xBuck + xNBuck * (yBuck + yNBuck * lBuck);
                }
            }
        }

        double hiLTr = 80.0 / sigmaR;
        // done indexing/hashing

        // proceed ...



        MeanShiftCalker meanShiftCalker = new MeanShiftCalker(hiLTr, lN, NPointArray, yk, slist, minL, xNBuck, yNBuck, buckets, bucNeigh);

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

            meanShiftCalker.yk = yk;



            // Calculate the mean shift vector using the lattice
            // LatticeMSVector(Mh, yk);
            /*****************************************************/
            // Initialize mean shift vector
            meanShiftCalker.calcMh(Mh);

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

                //LOG.println(yk);
                //LOG.println(Mh);
                // Shift window location
                /*
                for (j = 0; j < lN; j++)
                {
                yk[j] += Mh[j];
                }
                 */

                yk.incrBy(Mh);

                // Calculate the mean shift vector at the new
                // window location using lattice
                // LatticeMSVector(Mh, yk);
                /*****************************************************/
                // Initialize mean shift vector
                meanShiftCalker.yk = yk;
                
                meanShiftCalker.calcMh(Mh);

                mvAbs = calcmvAbs(Mh, sigmaS, mvAbs, sigmaR);

                // Increment interation count
                iterationCount++;
            }



            // Shift window location
            /*
            for (j = 0; j < lN; j++)
            {
            yk[j] += Mh[j];
            }
             */
            yk.incrBy(Mh);


            //store result into msRawData...
            /*
            for (j = 0; j < N; j++)
            {
            msRawData[N * i + j] =  (yk[j + 2] * sigmaR);
            }
             */
//            LOG.println(yk);
            resultLUVArray.setLUV(i % width, i / width, new LUV(yk.c.l * sigmaR, yk.c.u * sigmaR, yk.c.v * sigmaR));

        }


        return;

    }

    protected static double Dim(FPoint a, FPoint b) {
        return (a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y) + PointUtils.Dim(a.c, b.c);
    }

    class MeanShiftCalker {

        private double wsuml = 0;
        private double hiLTr;
        private int lN;
        private FPoint[] NPointArray;
        private FPoint yk;
        private int[] slist;
        private double sMins;
        private int nBuck1;
        private int nBuck2;
        private int[] buckets;
        private int[] bucNeigh;

        public MeanShiftCalker(double hiLTr, int lN, FPoint[] NPointArray, FPoint yk, int[] slist, double sMins, int nBuck1, int nBuck2, int[] buckets, int[] bucNeigh) {



            this.hiLTr = hiLTr;
            this.lN = lN;
            this.NPointArray = NPointArray;
            this.yk = yk;
            this.slist = slist;
            this.sMins = sMins;
            this.nBuck1 = nBuck1;
            this.nBuck2 = nBuck2;
            this.buckets = buckets;
            this.bucNeigh = bucNeigh;
        }

        private void calcMh(FPoint Mh) {
            wsuml = 0;
            /*
            this.hiLTr=hiLTr;
            this.lN=lN;
            this.sdata=sdata;
            this.yk=yk;            
            this.slist = slist;
             */

            Mh.set(0, 0, 0, 0, 0);

            /*
            for (int j = 0; j < lN; j++)
            {
            Mh[j] = 0;
            }
             */

            // kernelType.UniformLSearch(Mh, yk_ptr); // modify to new
            // find bucket of yk
            
            int cBuck1 = (int) yk.x + 1;
            int cBuck2 = (int) yk.y + 1;
            int cBuck3 = (int) (yk.c.l - sMins) + 1;
            int cBuck = cBuck1 + nBuck1 * (cBuck2 + nBuck2 * cBuck3);
            
            for (int j = 0; j < 27; j++) {

             

                int idxd = buckets[cBuck + bucNeigh[j]];
                
                  
                                

                // list parse, crt point is cHeadList
                while (idxd >= 0) {
                    
                    //int idxd = lN * idxd;
                    // determine if inside search window
                    double el = NPointArray[idxd].x - yk.x;
                    double diff = el * el;
                    el = NPointArray[idxd].y - yk.y;
                    diff += el * el;

                    if (diff < 1.0) {
                        el = NPointArray[idxd].c.l - yk.c.l;
                        if (yk.c.l > hiLTr) {
                            diff = 4 * el * el;
                        } else {
                            diff = el * el;
                        }

                        if (N > 1) {
                            el = NPointArray[idxd].c.u - yk.c.u;
                            diff += el * el;
                            el = NPointArray[idxd].c.v - yk.c.v;
                            diff += el * el;
                        }

                        if (diff < 1.0) {
                            double weight = 1;// - weightMap[idxd];

                            Mh.incrBy(NPointArray[idxd]);
                            /*
                            for (int k = 0; k < lN; k++)
                            {
                            Mh[k] += weight * NPointArray[idxs + k];
                            }*/
                            wsuml += weight;
                        }
                    }
                    idxd = slist[idxd];
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