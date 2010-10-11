/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.port.moveport;

/**
 *
 * @author nickl
 */
public class PortedFilterWithConnector extends PortedFilter {
    
    private double[] LUV_data;

    @Override
    protected void doFilter() {
        super.doFilter();

          LUV_data = new double[N * L];
        for (int i = 0; i < L * N; i++) {
            LUV_data[i] = msRawData[i];
        }

        Connect();
    }

    protected  int regionCount = 0;

    private  int neigh[];
    protected int[] labels;
    protected double modes[];
    private void Connect()
    {

        neigh = new int[8];
        modePointCounts = new int[L];
        labels = new int[L];
        modes = new double[L*(N+5)];
        indexTable = new int[L];


        //define eight connected neighbors
        neigh[0] = 1;
        neigh[1] = 1 - width;
        neigh[2] = -width;
        neigh[3] = -(1 + width);
        neigh[4] = -1;
        neigh[5] = width - 1;
        neigh[6] = width;
        neigh[7] = width + 1;

        //initialize labels and modePointCounts
        int i;


        for (i = 0; i < width * height; i++)
        {
            labels[i] = -1;
            modePointCounts[i] = 0;
        }

        //Traverse the image labeling each new region encountered
        int k, label = -1;
        for (i = 0; i < height * width; i++)
        {
            //if this region has not yet been labeled - label it
            if (labels[i] < 0)
            {
                //assign new label to this region
                labels[i] = ++label;

                //copy region color into modes
                for (k = 0; k < N; k++)
                {
                    modes[(N * label) + k] = LUV_data[(N * i) + k];
                }
//				modes[(N*label)+k]	= (float)(LUV_data[(N*i)+k]);

                //populate labels with label for this specified region
                //calculating modePointCounts[label]...
                Fill(i, label);
            }
        }
        //calculate region count using label
        regionCount = label + 1;

        //done.
        return;
    }

    private int indexTable[];
    protected  int modePointCounts[];
    private float LUV_treshold = 1.0f;

     private void Fill(int regionLoc, int label)
    {

        //declare variables
        int i, k, neighLoc, neighborsFound, imageSize = width * height;

        //Fill region starting at region location
        //using labels...

        //initialzie indexTable
        int index = 0;
        indexTable[0] = regionLoc;

        //increment mode point counts for this region to
        //indicate that one pixel belongs to this region
        modePointCounts[label]++;

        while (true)
        {

            //assume no neighbors will be found
            neighborsFound = 0;

            //check the eight connected neighbors at regionLoc -
            //if a pixel has similar color to that located at
            //regionLoc then declare it as part of this region
            for (i = 0; i < 8; i++)
            {
                // no need
         /*
                //if at boundary do not check certain neighbors because
                //they do not exist...
                if((regionLoc%width == 0)&&((i == 3)||(i == 4)||(i == 5)))
                continue;
                if((regionLoc%(width-1) == 0)&&((i == 0)||(i == 1)||(i == 7)))
                continue;
                 */

                //check bounds and if neighbor has been already labeled
                neighLoc = regionLoc + neigh[i];
                if ((neighLoc >= 0) && (neighLoc < imageSize) && (labels[neighLoc] < 0))
                {
                    for (k = 0; k < N; k++)
                    {
//					if(LUV_data[(regionLoc*N)+k] != LUV_data[(neighLoc*N)+k])
                        if (Math.abs(LUV_data[(regionLoc * N) + k] - LUV_data[(neighLoc * N) + k]) >= LUV_treshold)
                        {
                            break;
                        }
                    }

                    //neighbor i belongs to this region so label it and
                    //place it onto the index table buffer for further
                    //processing
                    if (k == N)
                    {
                        //assign label to neighbor i
                        labels[neighLoc] = label;

                        //increment region point count
                        modePointCounts[label]++;

                        //place index of neighbor i onto the index tabel buffer
                        indexTable[++index] = neighLoc;

                        //indicate that a neighboring region pixel was
                        //identified
                        neighborsFound = 1;
                    }
                }
            }

            //check the indexTable to see if there are any more
            //entries to be explored - if so explore them, otherwise
            //exit the loop - we are finished
            if (neighborsFound != 0)
            {
                regionLoc = indexTable[index];
            } else if (index > 1)
            {
                regionLoc = indexTable[--index];
            } else
            {
                break; //fill complete
            }
        }

        //done.
        return;

    }



}
