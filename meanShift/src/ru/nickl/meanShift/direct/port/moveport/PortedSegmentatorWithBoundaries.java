/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanShift.direct.port.moveport;



/**
 *
 * @author nickl
 */
public class PortedSegmentatorWithBoundaries extends PortedSegmentator {

    public PortedSegmentatorWithBoundaries(int minRegion) {
        super(minRegion);
    }

    RegionList regionList=null;


    public RegionList GetBoundaries()
    {

        //define bounds using label information
        if (regionList==null)
        {
            DefineBoundaries();
        }

        //return region list structure
        return regionList;

    }

    /**
     *Define Boundaries
     ******************************************************
     *Defines the boundaries for each region of the segm-
     *ented image storing the result into a region list
     *object.
     ******************************************************
     *Pre:
     *      - the image has been segmented and a classifi-
     *        cation structure has been created for this
     *        image
     *Post:
     *      - the boundaries of the segmented image have
     *        been defined and the boundaries of each reg-
     *        ion has been stored into a region list obj-
     *        ect.
     */
    private void DefineBoundaries()
    {

        //declare and allocate memory for boundary map and count
        int boundaryMap[], boundaryCount[] = null;
        if ((null == (boundaryMap = new int[L])) || (null == (boundaryCount = new int[regionCount])))
        {
           
        }

        //initialize boundary map and count
        int i;
        for (i = 0; i < L; i++)
        {
            boundaryMap[i] = -1;
        }
        for (i = 0; i < regionCount; i++)
        {
            boundaryCount[i] = 0;
        }

        //initialize and declare total boundary count -
        //the total number of boundary pixels present in
        //the segmented image
        int totalBoundaryCount = 0;

        //traverse the image checking the right and bottom
        //four connected neighbors of each pixel marking
        //boundary map with the boundaries of each region and
        //incrementing boundaryCount using the label information

        //***********************************************************************
        //***********************************************************************

        int j, label, dataPoint;

        //first row (every pixel is a boundary pixel)
        for (i = 0; i < width; i++)
        {
            boundaryMap[i] = label = labels[i];
            boundaryCount[label]++;
            totalBoundaryCount++;
        }

        //define boundaries for all rows except for the first
        //and last one...
        for (i = 1; i < height - 1; i++)
        {
            //mark the first pixel in an image row as an image boundary...
            dataPoint = i * width;
            boundaryMap[dataPoint] = label = labels[dataPoint];
            boundaryCount[label]++;
            totalBoundaryCount++;

            for (j = 1; j < width - 1; j++)
            {
                //define datapoint and its right and bottom
                //four connected neighbors
                dataPoint = i * width + j;

                //check four connected neighbors if they are
                //different this pixel is a boundary pixel
                label = labels[dataPoint];
                if ((label != labels[dataPoint - 1]) || (label != labels[dataPoint + 1]) ||
                        (label != labels[dataPoint - width]) || (label != labels[dataPoint + width]))
                {
                    boundaryMap[dataPoint] = label = labels[dataPoint];
                    boundaryCount[label]++;
                    totalBoundaryCount++;
                }
            }

            //mark the last pixel in an image row as an image boundary...
            dataPoint = (i + 1) * width - 1;
            boundaryMap[dataPoint] = label = labels[dataPoint];
            boundaryCount[label]++;
            totalBoundaryCount++;

        }

        //last row (every pixel is a boundary pixel) (i = height-1)
        int start = (height - 1) * width, stop = height * width;
        for (i = start; i < stop; i++)
        {
            boundaryMap[i] = label = labels[i];
            boundaryCount[label]++;
            totalBoundaryCount++;
        }

        //***********************************************************************
        //***********************************************************************

        //store boundary locations into a boundary buffer using
        //boundary map and count

        //***********************************************************************
        //***********************************************************************

        int boundaryBuffer[] = new int[totalBoundaryCount], boundaryIndex[] = new int[regionCount];

        //use boundary count to initialize boundary index...
        int counter = 0;
        for (i = 0; i < regionCount; i++)
        {
            boundaryIndex[i] = counter;
            counter += boundaryCount[i];
        }

        //traverse boundary map placing the boundary pixel
        //locations into the boundaryBuffer
        for (i = 0; i < L; i++)
        {
            //if its a boundary pixel store it into
            //the boundary buffer
            if ((label = boundaryMap[i]) >= 0)
            {
                boundaryBuffer[boundaryIndex[label]] = i;
                boundaryIndex[label]++;
            }
        }

        //***********************************************************************
        //***********************************************************************

        //store the boundary locations stored by boundaryBuffer into
        //the region list for each region

        //***********************************************************************
        //***********************************************************************

        //destroy the old region list
        //if(regionList!=null)	//delete regionList;

        //create a new region list
        if (null == (regionList = new RegionList(regionCount, totalBoundaryCount, N)))
        {
            
        }

        //add boundary locations for each region using the boundary
        //buffer and boundary counts
        counter = 0;
        for (i = 0; i < regionCount; i++)
        {
            regionList.AddRegion(i, boundaryCount[i], boundaryBuffer, counter);
            counter += boundaryCount[i];
        }

        //***********************************************************************
        //***********************************************************************

        // dealocate local used memory
        //delete [] boundaryMap;
        //delete [] boundaryCount;
        //delete [] boundaryBuffer;
        //delete [] boundaryIndex;

        //done.
        return;

    }



}
