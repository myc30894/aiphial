package ru.nickl.meanShift.direct.port.moveport;






//define region structure
class REGION {
	int			label;
	int			pointCount;
	int			region;

}

//region class prototype...
public class RegionList {



  

	REGION[]		regionList;			//array of maxRegions regions
	int			minRegion;
	int			maxRegions;			//defines the number maximum number of regions										//allowed (determined by user during class construction)
	int			numRegions;				//the number of regions currently stored by the										//region list
	int			freeRegion;				//an index into the regionList pointing to the next
										//available region in the regionList

	//#####################################
	//###         INDEX TABLE           ###
	//#####################################

	   public int[]			indexTable;			//an array of indexes that point into an external structure
										//specifying which points belong to a region
	int			freeBlockLoc;		//points to the next free block of memory in the indexTable


	
	int			N;						//dimension of data set being classified by region list
										//class

	
	int			L;						//number of points contained by the data set being classified by
										//region list class
        
        RegionList(int maxRegions_, int L_, int N_)
{

	//Obtain maximum number of regions that can be
	//defined by user
	maxRegions = maxRegions_;
		

	//Obtain dimension of data set being classified by
	//region list class
	N = N_;
		

	//Obtain length of input data set...
	L = L_;
		

	//Allocate memory for index table
	indexTable = new int [L];

	//Allocate memory for region list array
	regionList = new REGION [maxRegions];

        for (int ii = 0; ii < regionList.length; ii++) {
        regionList[ii] = new REGION();

    }

	//Initialize region list...
	numRegions		= freeRegion = 0;

	//Initialize indexTable
	freeBlockLoc	= 0;

	//done.
	return;

}

/*******************************************************/
/*Destructor                                           */
/*******************************************************/
/*Destroys region list object.                         */
/*******************************************************/
/*Post:                                                */
/*      - region list object has been properly dest-   */
/*        oyed.                                        */
/*******************************************************/

    public void destrucror ()
{
	//de-allocate memory...
	//delete [] regionList;
	///delete [] indexTable;

	//done.
	return;
}



    public void AddRegion(int label, int pointCount, int[] indeces, int ind)
{

	//make sure that there is enough room for this new region 
	//in the region list array...
	if(numRegions >= maxRegions);


	//make sure that label is positive and point Count > 0...
	if((label < 0)||(pointCount <= 0));
		

	//make sure that there is enough memory in the indexTable
	//for this region...
	if((freeBlockLoc + pointCount) > L);
	

	//place new region into region list array using
	//freeRegion index
	regionList[freeRegion].label		= label;
	regionList[freeRegion].pointCount	= pointCount;
	regionList[freeRegion].region		= freeBlockLoc;

	//copy indeces into indexTable using freeBlock...
	int i;
	for(i = 0; i < pointCount; i++)
		indexTable[freeBlockLoc+i] = indeces[ind+i];

	//increment freeBlock to point to the next free
	//block
	freeBlockLoc	+= pointCount;

	//increment freeRegion to point to the next free region
	//also, increment numRegions to indicate that another
	//region has been added to the region list
	freeRegion++;
	numRegions++;

	//done.
	return;

}

/*******************************************************/
/*Reset                                                */
/*******************************************************/
/*Resets the region list.                              */
/*******************************************************/
/*Post:                                                */
/*      - the region list has been reset.              */
/*******************************************************/

public void Reset( )
{

	//reset region list
	freeRegion = numRegions = freeBlockLoc = 0;

	//done.
	return;

}

  /*/\/\/\/\/\/\/\/\/\/\*/
  /*  Query Region List */
  /*\/\/\/\/\/\/\/\/\/\/*/

/*******************************************************/
/*Get Number Regions                                   */
/*******************************************************/
/*Returns the number of regions stored by region list. */
/*******************************************************/
/*Post:                                                */
/*      - the number of regions stored by the region   */
/*        list is returned.                            */
/*******************************************************/

public int	GetNumRegions()
{
	// return region count
	return numRegions;
}

/*******************************************************/
/*Get Label                                            */
/*******************************************************/
/*Returns the label of a specified region.             */
/*******************************************************/
/*Pre:                                                 */
/*      - regionNum is an index into the region list   */
/*        array.                                       */
/*Post:                                                */
/*      - the label of the region having region index  */
/*        specified by regionNum has been returned.    */
/*******************************************************/

public int	GetLabel(int regionNum)
{
	//return the label of a specified region
	return regionList[regionNum].label;
}

/*******************************************************/
/*Get Region Count                                     */
/*******************************************************/
/*Returns the point count of a specified region.       */
/*******************************************************/
/*Pre:                                                 */
/*      - regionNum is an index into the region list   */
/*        array.                                       */
/*Post:                                                */
/*      - the number of points that classify the       */
/*        region whose index is specified by regionNum */
/*        is returned.                                 */
/*******************************************************/

public int GetRegionCount(int regionNum)
{
	//return the region count of a specified region
	return regionList[regionNum].pointCount;
}

/*******************************************************/
/*Get Region Indeces                                   */
/*******************************************************/
/*Returns the point indeces specifying a region.       */
/*******************************************************/
/*Pre:                                                 */
/*      - regionNum is an index into the region list   */
/*        array.                                       */
/*Post:                                                */
/*      - the region indeces specifying the points     */
/*        contained by the region specified by region- */
/*        Num are returned.                            */
/*******************************************************/

public int GetRegionIndeces(int regionNum)
{
	//return point indeces using regionNum
	return regionList[regionNum].region;
}

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@     PRIVATE METHODS     @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

  /*/\/\/\/\/\/\/\/\/\/\/\*/
  /*  Class Error Handler */
  /*\/\/\/\/\/\/\/\/\/\/\/*/

/*******************************************************/
/*Error Handler                                        */
/*******************************************************/
/*Class error handler.                                 */
/*******************************************************/
/*Pre:                                                 */
/*      - functName is the name of the function that   */
/*        caused an error                              */
/*      - errmsg is the error message given by the     */
/*        calling function                             */
/*      - status is the error status: ErrorType.FATAL or NON-    */
/*        ErrorType.FATAL                                        */
/*Post:                                                */
/*      - the error message errmsg is flagged on beh-  */
/*        ave of function functName.                   */
/*      - if the error status is ErrorType.FATAL then the program*/
/*        is halted, otherwise execution is continued, */
/*        error recovery is assumed to be handled by   */
/*        the calling function.                        */
/*******************************************************/


        

}




