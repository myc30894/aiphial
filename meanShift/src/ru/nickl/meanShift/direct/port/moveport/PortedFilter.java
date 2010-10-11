/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.port.moveport;

import ru.nickl.meanShift.direct.*;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickl
 */
public class PortedFilter extends DataArrayISAdapter {

    
    private  PrintStream LOG = null; 
            
    
   
         final double	EPSILON		= 0.01;
    protected  final int		LIMIT           = 100;

    @Override
    protected void doFilter() {
                NewNonOptimizedFilter(squareRange, colorRange);
      
        
    }
    


    
       void NewNonOptimizedFilter(double sigmaS, double sigmaR)
    {

        // Declare Variables
        int iterationCount, i, j, k;
        double mvAbs, diff, el;

        //make sure that a lattice height and width have
        //been defined...
        if (0 == height)
        {
            throw new IllegalStateException("Lattice height and width are undefined.");
            
        }

        //re-assign bandwidths to sigmaS and sigmaR
        if (((h[0] = sigmaS) <= 0) || ((h[1] = sigmaR) <= 0))
        {
            throw new IllegalStateException("sigmaS and/or sigmaR is zero or negative.");
            
        }

        //define input data dimension with lattice
        int lN = N + 2;

        // Traverse each data point applying mean shift
        // to each data point

        // Allcocate memory for yk
        double yk[] = new double[lN];

        // Allocate memory for Mh
        double Mh[] = new double[lN];

        // let's use some temporary data
        double[] sdata;
        sdata = new double[lN * L];

        // copy the scaled data
        int idxs, idxd;
        idxs = idxd = 0;
        if (N == 3)
        {
            for (i = 0; i < L; i++)
            {
                sdata[idxs++] = (i % width) / sigmaS;
                sdata[idxs++] = (i / width) / sigmaS;
                sdata[idxs++] = data[idxd++] / sigmaR;
                sdata[idxs++] = data[idxd++] / sigmaR;
                sdata[idxs++] = data[idxd++] / sigmaR;
            }
        } else if (N == 1)
        {
            for (i = 0; i < L; i++)
            {
                sdata[idxs++] = (i % width) / sigmaS;
                sdata[idxs++] = (i / width) / sigmaS;
                sdata[idxs++] = data[idxd++] / sigmaR;
            }
        } else
        {
            for (i = 0; i < L; i++)
            {
                sdata[idxs++] = (i % width) / sigmaS;
                sdata[idxs++] = (i % width) / sigmaS;
                for (j = 0; j < N; j++)
                {
                    sdata[idxs++] = data[idxd++] / sigmaR;
                }
            }
        }
        // index the data in the 3d buckets (x, y, L)
        int[] buckets;
        int[] slist;
        slist = new int[L];
        int bucNeigh[] = new int[27];

        double sMins; // just for L
        double sMaxs[] = new double[3]; // for all
        sMaxs[0] = width / sigmaS;
        sMaxs[1] = height / sigmaS;
        sMins = sMaxs[2] = sdata[2];
        idxs = 2;
        double cval;
        for (i = 0; i < L; i++)
        {
            cval = sdata[idxs];
            if (cval < sMins)
            {
                sMins = cval;
            } else if (cval > sMaxs[2])
            {
                sMaxs[2] = cval;
            }

            idxs += lN;
        }
        
        

        int nBuck1, nBuck2, nBuck3;
        int cBuck1, cBuck2, cBuck3, cBuck;
        nBuck1 = (int) (sMaxs[0] + 3);
        nBuck2 = (int) (sMaxs[1] + 3);
        nBuck3 = (int) (sMaxs[2] - sMins + 3);
        buckets = new int[nBuck1 * nBuck2 * nBuck3];
        for (i = 0; i < (nBuck1 * nBuck2 * nBuck3); i++)
        {
            buckets[i] = -1;
        }

        idxs = 0;
        for (i = 0; i < L; i++)
        {
            // find bucket for current data and add it to the list
            cBuck1 = (int) sdata[idxs] + 1;
            cBuck2 = (int) sdata[idxs + 1] + 1;
            cBuck3 = (int) (sdata[idxs + 2] - sMins) + 1;
            cBuck = cBuck1 + nBuck1 * (cBuck2 + nBuck2 * cBuck3);

            slist[i] = buckets[cBuck];
            buckets[cBuck] = i;

            idxs += lN;
        }
        // init bucNeigh
        idxd = 0;
        for (cBuck1 = -1; cBuck1 <= 1; cBuck1++)
        {
            for (cBuck2 = -1; cBuck2 <= 1; cBuck2++)
            {
                for (cBuck3 = -1; cBuck3 <= 1; cBuck3++)
                {
                    bucNeigh[idxd++] = cBuck1 + nBuck1 * (cBuck2 + nBuck2 * cBuck3);
                }
            }
        }

        double hiLTr = 80.0 / sigmaR;
        // done indexing/hashing

        // proceed ...



        MeanShiftCalker meanShiftCalker = new MeanShiftCalker(hiLTr, lN, sdata, yk, slist, sMins, nBuck1, nBuck2, buckets, bucNeigh);

        for (i = 0; i < L; i++)
        {

            // Assign window center (window centers are
            // initialized by createLattice to be the point
            // data[i])
            idxs = i * lN;
            for (j = 0; j < lN; j++)
            {
                yk[j] = sdata[idxs + j];
            }

            // Calculate the mean shift vector using the lattice
            // LatticeMSVector(Mh, yk);
            /*****************************************************/
            // Initialize mean shift vector
            
            meanShiftCalker.calcMh(Mh);

            /*****************************************************/
            // Calculate its magnitude squared
            mvAbs = 0;
            for (j = 0; j < lN; j++)
            {
                mvAbs += Mh[j] * Mh[j];
            }

            // Keep shifting window center until the magnitude squared of the
            // mean shift vector calculated at the window center location is
            // under a specified threshold (Epsilon)

            // NOTE: iteration count is for speed up purposes only - it
            //       does not have any theoretical importance
            iterationCount = 1;
            
           
            while ((mvAbs >= EPSILON) && (iterationCount < LIMIT))
            {

//                LOGpoint(yk);
//                LOGpoint(Mh);
                
                
                // Shift window location
                for (j = 0; j < lN; j++)
                {
                    
                    yk[j] += Mh[j];
                }
                
                

                // Calculate the mean shift vector at the new
                // window location using lattice
                // LatticeMSVector(Mh, yk);
                /*****************************************************/
                // Initialize mean shift vector
                meanShiftCalker.calcMh(Mh);

                mvAbs = calcmvAbs(Mh, sigmaS, mvAbs, sigmaR);

                // Increment interation count
                iterationCount++;
            }
            

            // Shift window location
            for (j = 0; j < lN; j++)
            {
                yk[j] += Mh[j];
            }

//            LOGpoint(yk);
            //store result into msRawData...            
            for (j = 0; j < N; j++)
            {
                msRawData[N * i + j] = (double) (yk[j + 2] * sigmaR);
            }
 
        }


        return;

    }

    class MeanShiftCalker
    {

        double wsuml = 0;
        double hiLTr;
        int lN;
        double[] sdata;
        double[] yk;
        int[] slist;
        private double sMins;
        private int nBuck1;
        private int nBuck2;
        private int[] buckets;
        private int[] bucNeigh;

        public MeanShiftCalker(double hiLTr, int lN, double[] sdata, double[] yk, int[] slist, double sMins, int nBuck1, int nBuck2, int[] buckets, int[] bucNeigh)
        {            
          
            this.hiLTr = hiLTr;
            this.lN = lN;
            this.sdata = sdata;
            this.yk = yk;
            this.slist = slist;
            this.sMins = sMins;
            this.nBuck1 = nBuck1;
            this.nBuck2 = nBuck2;
            this.buckets = buckets;
            this.bucNeigh = bucNeigh;
        }

        
         private void gn(double[] Mh,int idxd)
        {
            

            int idxs = lN * idxd;
            // determine if inside search window
            double el = sdata[idxs + 0] - yk[0];
            double diff = el * el;
            el = sdata[idxs + 1] - yk[1];
            diff += el * el;

            if (diff < 1.0)
            {
                el = sdata[idxs + 2] - yk[2];
                if (yk[2] > hiLTr)
                {
                    diff = 4 * el * el;
                } else
                {
                    diff = el * el;
                }

                if (N > 1)
                {
                    el = sdata[idxs + 3] - yk[3];
                    diff += el * el;
                    el = sdata[idxs + 4] - yk[4];
                    diff += el * el;
                }

                if (diff < 1.0)
                {
                    double weight = 1;// - weightMap[idxd];
                    for (int k = 0; k < lN; k++)
                    {
                        Mh[k] += weight * sdata[idxs + k];
                    }
                    wsuml += weight;
                }
            }

                      
            
        }
        
        private void calcMh(double[] Mh)
        {
            wsuml = 0;
            /*
            this.hiLTr=hiLTr;
            this.lN=lN;
            this.sdata=sdata;
            this.yk=yk;            
            this.slist = slist;
             */
            for (int j = 0; j < lN; j++)
            {
                Mh[j] = 0;
            }


            // kernelType.UniformLSearch(Mh, yk_ptr); // modify to new
            // find bucket of yk
            int cBuck1 = (int) yk[0] + 1;
            int cBuck2 = (int) yk[1] + 1;
            int cBuck3 = (int) (yk[2] - sMins) + 1;
            int cBuck = cBuck1 + nBuck1 * (cBuck2 + nBuck2 * cBuck3);
            for (int j = 0; j < 27; j++)
            {
                //LOG.println(cBuck+"\t"+j+"\t"+bucNeigh[j]);
                
                int idxd = buckets[cBuck + bucNeigh[j]];
                // list parse, crt point is cHeadList
                while (idxd >= 0)
                {
                    
                    
                    int idxs = lN * idxd;
                    // determine if inside search window
                    double el = sdata[idxs + 0] - yk[0];
                    double diff = el * el;
                    el = sdata[idxs + 1] - yk[1];
                    diff += el * el;

                    if (diff < 1.0)
                    {
                        el = sdata[idxs + 2] - yk[2];
                        if (yk[2] > hiLTr)
                        {
                            diff = 4 * el * el;
                        } else
                        {
                            diff = el * el;
                        }

                        if (N > 1)
                        {
                            el = sdata[idxs + 3] - yk[3];
                            diff += el * el;
                            el = sdata[idxs + 4] - yk[4];
                            diff += el * el;
                        }

                        if (diff < 1.0)
                        {
                            double weight = 1;// - weightMap[idxd];
                            for (int k = 0; k < lN; k++)
                            {
                                Mh[k] += weight * sdata[idxs + k];
                            }
                            wsuml += weight;
                        }
                    }
                    idxd = slist[idxd];
                }
            }

            if (wsuml > 0)
            {
                for (int j = 0; j < lN; j++)
                {
                    Mh[j] = Mh[j] / wsuml - yk[j];
                }
            } else
            {
                for (int j = 0; j < lN; j++)
                {
                    Mh[j] = 0;
                }
            }
            
           
        }
    }

     private double calcmvAbs(double[] Mh, double sigmaS, double mvAbs, double sigmaR)
    {
        /*****************************************************/
        // Calculate its magnitude squared
        //mvAbs = 0;
        //for(j = 0; j < lN; j++)
        //	mvAbs += Mh[j]*Mh[j];
        mvAbs = (Mh[0] * Mh[0] + Mh[1] * Mh[1]) * sigmaS * sigmaS;
        if (N == 3)
        {
            mvAbs += (Mh[2] * Mh[2] + Mh[3] * Mh[3] + Mh[4] * Mh[4]) * sigmaR * sigmaR;
        } else
        {
            mvAbs += Mh[2] * Mh[2] * sigmaR * sigmaR;
        }
        return mvAbs;
    } 

     
        private void LOGpoint(double[] Mh)
        {
            LOG.println(Mh[0] + "\t" + Mh[1] + "\t" + Mh[2] + "\t" + Mh[3] + "\t" + Mh[4]);
        }
        
     
}
