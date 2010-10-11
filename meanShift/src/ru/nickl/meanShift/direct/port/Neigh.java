/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanShift.direct.port;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nickl
 */
public class Neigh {

    private int xNBuck,  yNBuck,  lNBuck;
    private double minL; // just for L
    // just for L
    int[] buckets;
    int[] slist;
    int bucNeigh[] = new int[27];
    private int width;
    private int height;
    private FPoint[] NPointArray;
    private int L;
    double sigmaS;
    PrintStream LOG;

    public Neigh(int width, int height, FPoint[] NPointArray, double sigmaS) {

        try {
            LOG = new PrintStream("slist.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Neigh.class.getName()).log(Level.SEVERE, null, ex);
        }


        this.width = width;
        this.height = height;
        this.NPointArray = NPointArray;
        this.sigmaS = sigmaS;

        this.L = width * height;

        calkNeigh();

    }

    @Override
    protected void finalize() throws Throwable {
        LOG.close();
        super.finalize();
    }
    Map<FPoint, Integer> idxdMAP;

    public List<FPoint> getNeighbours(FPoint yk) {


        List<FPoint> res = new ArrayList<FPoint>(27);
        idxdMAP = new HashMap<FPoint, Integer>(32);

        int cBuck1 = (int) yk.x + 1;
        int cBuck2 = (int) yk.y + 1;
        int cBuck3 = (int) (yk.c.l - minL) + 1;

        int cBuck = cBuck1 + xNBuck * (cBuck2 + yNBuck * cBuck3);

        for (int j = 0; j < 27; j++) {



            int idxd = buckets[cBuck + bucNeigh[j]];

            if (idxd != -1) {

                NPointArray[idxd].idxd = idxd;
                res.add(NPointArray[idxd]);

            }
        }

        return res;

    }
    Map<FPoint, List<FPoint>> cachedSLists = new WeakHashMap<FPoint, List<FPoint>>();

    public List<FPoint> getSlist(FPoint yk) {

        List<FPoint> cached = cachedSLists.get(yk);

        if (cached != null) {
            return cached;
        }

        List<FPoint> res = new ArrayList<FPoint>(10);

        int idxd = yk.idxd;
        if (idxd < 0) {
            throw new RuntimeException("invalid point");
        }


        //LOG.print((int)(yk.x*sigmaS)+" "+(int)(yk.y*sigmaS)+": {");


        while (idxd >= 0) {
            // LOG.print("("+(int)(NPointArray[idxd].x*sigmaS)+" "+(int)(NPointArray[idxd].y*sigmaS)+"),");
            res.add(NPointArray[idxd]);
            idxd = slist[idxd];

        }

        // LOG.println("}");

        cachedSLists.put(yk, res);


        return res;

    }

    private void calkNeigh() {

        int i;
        int idxd;

        // index the data in the 3d buckets (x, y, L)
        slist = new int[L];
        bucNeigh = new int[27];
        // just for L
        double maxX = width / sigmaS;
        double maxY = height / sigmaS;
        double maxL = minL = NPointArray[0].c.l;

        double cval;
        for (i = 0; i < L; i++) {
            cval = NPointArray[i].c.l;
            if (cval < minL) {
                minL = cval;
            } else if (cval > maxL) {
                maxL = cval;
            }

        }
        int xBuck;
        int yBuck;
        int lBuck;
        int cBuck;
        xNBuck = (int) (maxX + 3);
        yNBuck = (int) (maxY + 3);
        lNBuck = (int) (maxL - minL + 3);
        buckets = new int[xNBuck * yNBuck * lNBuck];
        for (i = 0; i < (xNBuck * yNBuck * lNBuck); i++) {
            buckets[i] = -1;
        }

        for (i = 0; i < L; i++) {
            // find bucket for current data and add it to the list
            xBuck = (int) NPointArray[i].x + 1;
            yBuck = (int) NPointArray[i].y + 1;
            lBuck = (int) (NPointArray[i].c.l - minL) + 1;
            cBuck = xBuck + xNBuck * (yBuck + yNBuck * lBuck);
            slist[i] = buckets[cBuck];

            buckets[cBuck] = i;

        //LOG.println(i+"  "+cBuck+" "+slist[i]);

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
    }
}