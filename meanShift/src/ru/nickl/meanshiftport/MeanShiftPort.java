/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.nickl.meanshiftport;

/**
 *
 * @author nickl
 */
public class MeanShiftPort {

final int		GAUSS_NUM_ELS   = 16;		// take 16 samples of exp(-u/2)
final double	GAUSS_LIMIT     = 2.9;		// GAUSS_LIMIT     = c
final double	GAUSS_INCREMENT = GAUSS_LIMIT*GAUSS_LIMIT/GAUSS_NUM_ELS;
     final double	EPSILON		= 0.01;
     final int		LIMIT           = 100;
    // final double	DELTA           = 0.00001;

     final double	TC_DIST_FACTOR	= 0.5;		// cluster search windows near convergence that are a distance

     //##########################################
   //######### INPUT DATA PARAMETERS ##########
   //##########################################

	int				L, N, kp;
            int P[];						// length, dimension, subspace number, and subspace dimensions


   //##########################################
   //######### INPUT DATA STORAGE    ##########
   //##########################################

	////////Linear Storage (used by lattice and bst)////////
	float			data[];								// memory allocated for data points stored by tree nodes
														// when used by the lattice data structure data does not store
														// the lattice information; format of data:
														// data = <x11, x12, ..., x1N,...,xL1, xL2, ..., xLN>
														// in the case of the lattice the i in data(i,j) corresponds

   //##########################################
   //######## LATTICE DATA STRUCTURE ##########
   //##########################################

	////////Lattice Data Structure////////
	int				height, width;						// Height and width of lattice

   //##########################################
   //######### KERNEL DATA STRUCTURE ##########
   //##########################################

	float			h[];									// bandwidth vector

	float			offset[];							// defines bandwidth offset caused by the use of a Gaussian kernel
                                                        // (for example)

   //##########################################
   //#########  BASIN OF ATTRACTION  ##########
   //##########################################

	int	modeTable[];							// Assigns a marking to each data point specifying whether
														// or not it has been assigned a mode. These labels are:
														// modeTable[i] = 0 - data point i is not associated with a mode
														// modeTable[i] = 1 - data point i is associated with a mode
														// modeTable[i] = 2 - data point i is associated with a mode
														//                    however its mode is yet to be determined

	int				pointList[];							// a list of data points that due to basin of attraction will
														// converge to the same mode as the mode that mean shift is
														// currently being applied to

	int				pointCount;							// the number of points stored by the point list


   //##########################################
   //#########  WEIGHT MAP USED      ##########
   //#########  WHEN COMPUTING MEAN  ##########
   //#########  SHIFT ON A LATTICE   ##########
   //##########################################

	float			weightMap[];							// weight map that may be used to weight the kernel
														// upon performing mean shift on a lattice

	boolean			weightMapDefined;					// used to indicate if a lattice weight map has been
														// defined

   //##########################################
   //#######        CLASS STATE        ########
   //##########################################

	ClassStateStruct	class_state = new ClassStateStruct();

        kernelType		kernel[];							// kernel types for each subspace S[i]

	double[][]			w;								// weight function lookup table

	double			increment[];							// increment used by weight hashing function

	boolean			uniformKernel;						// flag used to indicate if the kernel is uniform or not

        userWeightFunct	head=null, cur=null;

        float[]			range;

 //       	tree			root;								// root of kdBST used to store input

//	tree			forest;

        double[]		uv;

      //  double			wsum;

        String ErrorMessage;
ErrorLevel	ErrorStatus;

//int				LowerBoundX, UpperBoundX;			// Upper and lower bounds for lattice search window
														// in the x dimension

	//int				LowerBoundY, UpperBoundY;			// Upper and lower bounds for lattice search window
														// in the y dimension

/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@      PUBLIC METHODS     @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
/*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/

  /*/\/\/\/\/\/\/\/\/\/\/\/\/\/\*/
  /*** Constructor/Destructor ***/
  /*\/\/\/\/\/\/\/\/\/\/\/\/\/\/*/

/**
*Class Constructor
******************************************************
*Post:
*      The MeanShift class has been properly
*      initialized.
*/

    public MeanShiftPort( )
{
	
	//intialize input data set parameters...
	P							= null;
	L							= 0;
	N							= 0;
	kp							= 0;
	
	//initialize input data set storage structures...
	data						= null;
	
	//initialize input data set kd-tree
	//root						= null;
	//forest						= null;
	//range						= null;
	
	//intialize lattice structure...
	height						= 0;
	width						= 0;
	
	//intialize kernel strucuture...
	h							= null;
	kernel						= null;
	w							= null;
	offset						= null;
	increment					= null;
	uniformKernel				= false;
	
	//initialize weight function linked list...
	//head						= cur	= null;
	
	//intialize mean shift processing data structures...
	//uv							= null;

        

	//set lattice weight map to null
	weightMap					= null;

	//indicate that the lattice weight map is undefined
	weightMapDefined			= false;
	
	//allocate memory for error message buffer...
	ErrorMessage				= new String();
	
	//initialize error status to OKAY
	ErrorStatus					= ErrorLevel.EL_OKAY;
	
	//Initialize class state...
	class_state.INPUT_DEFINED	= false;
	class_state.KERNEL_DEFINED	= false;
	class_state.LATTICE_DEFINED	= false;
	class_state.OUTPUT_DEFINED	= false;
	
}





/**
*Define Kernel
******************************************************
*Creats custom user defined Kernel to be used by the
*mean shift procedure.
******************************************************
*Pre:
*      - kernel is an array of kernelTypes specifying
*        the type of kernel to be used on each sub-
*        space of the input data set x
*      - h is the set of bandwidths used to define the
*        the search window
*      - P is a one dimensional array of integers of
*        size kp, that specifies the dimension of each
*        subspace of the input data set x
*      - kp is the total number of subspaces used to
*        the input data set x
*Post:
*      - the custom kernel has been created for use
*        by the mean shift procedure.
*/

void DefineKernel(kernelType kernel_[], float h_[], int P_[], int kp_)
{
	
	// Declare variables
	int i, kN;
	
	//if a kernel has already been created then destroy it
	if(kp!=0)
		          throw new UnsupportedOperationException("kernal redef was removed");
	
	//Obtain kp...
	if((kp = kp_) <= 0)
	{
		ErrorHandler("MeanShift", "CreateKernel", "Subspace count (kp) is zero or negative.");
		return;
	}
	
	//Allocate memory for h, P, kernel, offset, and increment
	P = new int [kp];
        h = new float [kp];
        kernel = new kernelType [kp];
		offset = new float [kp];
                increment = new double [kp];
	
	
	//Populate h, P and kernel, also use P to calculate
	//the dimension (N_) of the potential input data set x
	kN = 0;
	for(i = 0; i < kp; i++)
	{
		if((h[i] = h_[i]) <= 0)
		{
			ErrorHandler("MeanShift", "CreateKernel", "Negative or zero valued bandwidths are prohibited.");
			return;
		}
		if((P[i] = P_[i]) <= 0)
		{
			ErrorHandler("MeanShift", "CreateKernel", "Negative or zero valued subspace dimensions are prohibited.");
			return;
		}
		kernel[i] = kernel_[i];
		kN	   += P[i];
	}
	
	//Allocate memory for range vector and uv using N_
	range = new float [2*kN];
        uv = new double [kN];

	
	// Generate weight function lookup table
	// using above information and user
	// defined weight function list
	generateLookupTable();
	
	//check for errors
	if(ErrorStatus == ErrorLevel.EL_ERROR)
		return;
	
	//indicate that the kernel has been defined
	class_state.KERNEL_DEFINED	= true;
	
	//done.
	return;
	
}




/**
*Define Lattice
******************************************************
*Defines the height and width of the input lattice.
******************************************************
*Pre:
*      - ht is the height of the lattice
*      - wt is the width of the lattice
*Post:
*      - the height and width of the lattice has been
*        specified.
*      - if a data set is presently loaded into the
*        mean shift class, an error is flagged if the
*        number of elements in that data set does not
*        equal the product ht*wt.
*/

void DefineLInput(float x[], int ht, int wt, int N_)
{
	
	//if input data is defined de-allocate memory, and
	//re-initialize the input data structure
	if((class_state.INPUT_DEFINED)||(class_state.LATTICE_DEFINED))
		          throw new UnsupportedOperationException("ResetInput()");
	
	//Obtain lattice height and width
	if(((height	= ht) <= 0)||((width	= wt) <= 0))
	{
		ErrorHandler("MeanShift", "DefineLInput", "Lattice defined using zero or negative height and/or width.");
		return;
	}
	
	//Obtain input data dimension
	if((N = N_) <= 0)
	{
		ErrorHandler("MeanShift", "DefineInput", "Input defined using zero or negative dimension.");
		return;
	}
	
	//compute the data length, L, of input data set
	//using height and width
	L		= height*width;
	
	//Allocate memory for input data set, and copy
	//x into the private data members of the mean
	//shift class
	InitializeInput(x);
	
	//check for errors
	if(ErrorStatus == ErrorLevel.EL_ERROR)
		return;

	//allocate memory for weight map
	weightMap = new float [L];

	//initialize weightMap to an array of zeros
	//memset(weightMap, 0, L*(sizeof(float)));
	
        for (int i = 0; i < weightMap.length; i++) {
        weightMap[i] = 0;
        
    }
        
	//Indicate that a lattice input has recently been
	//defined
	class_state.LATTICE_DEFINED	= true;
	class_state.INPUT_DEFINED	= false;
	class_state.OUTPUT_DEFINED	= false;
	
	//done.
	return;
	
}




/**
*Class Consistency Check
******************************************************
*Checks the state of the class prior to the applicat-
*ion of mean shift.
******************************************************
*Pre:
*      - iN is the specified dimension of the input,
*        iN = N for a general input data set, iN = N
*        + 2 for a input set defined using a lattice
*Post:
*      - if the kernel has not been created, an input
*        has not been defined and/or the specified
*        input dimension (iN) does not match that of
*        the kernel a fatal error is flagged.
*/

void classConsistencyCheck(int iN, boolean usingLattice)
{
	
	//make sure that kernel has been created...
	if(class_state.KERNEL_DEFINED == false)
	{
		ErrorHandler("MeanShift", "classConsistencyCheck", "Kernel not created.");
		return;
	}
	
	//make sure input data set has been loaded into mean shift object...
	if((class_state.INPUT_DEFINED == false)&&(false==usingLattice))
	{
		ErrorHandler("MeanShift", "classConsistencyCheck", "No input data specified.");
		return;
	}
	
	//make sure that the lattice is defined if it is being used
	if((class_state.LATTICE_DEFINED == false)&&(usingLattice))
	{
		ErrorHandler("MeanShift", "classConsistencyCheck", "Latice not created.");
		return;
	}
	
	//make sure that dimension of the kernel and the input data set
	//agree
	
	//calculate dimension of kernel (kN)
	int i, kN	= 0;
	for(i = 0; i < kp; i++)
		kN	+= P[i];
	
	//perform comparison...
	if(iN != kN)
	{
		ErrorHandler("MeanShift", "classConsitencyCheck", "Kernel dimension does not match defined input data dimension.");
		return;
	}
	
	//done.
	return;
	
}

  /*/\/\/\/\/\/\/\/\/\/\/\/\/\*/
  /*** Class Error Handler  ***/
  /*\/\/\/\/\/\/\/\/\/\/\/\/\/*/

/**
*Error Handler
******************************************************
*Class error handler.
******************************************************
*Pre:
*      - className is the name of the class that fl-
*        agged an error
*      - methodName is the name of the method that
*        flagged an error
*      - errmsg is the error message given by the
*        calling function
*Post:
*      - the error message errmsg is flagged on beh-
*        ave of method methodName belonging to class
*        className:
*
*        (1) ErrorMessage has been updated with the
*            appropriate error message using the arg-
*            ments passed to this method.
*        (2) ErrorStatus is set to ERROR
*            (ErrorStatus = 1)
*/

void ErrorHandler(String className, String methodName, String errmsg)
{
	
    StringBuilder sb = new StringBuilder();
    
    sb.append(className);
    sb.append("::");
    sb.append(methodName);
    sb.append(" Error: ");
    sb.append(errmsg);
    
    ErrorMessage = sb.toString(); 
    
	
	//set error status to ERROR
	ErrorStatus = ErrorLevel.EL_ERROR;
	
	
}

void generateLookupTable()
{

	// Declare Variables
	int i,j;

	// Allocate memory for lookup table w
	w = new double[kp][];

	// Traverse through kernel generating weight function
	// lookup table w

	// Assume kernel is uniform
	uniformKernel = true;

	for(i = 0; i < kp; i++)
    {
		switch(kernel[i])
		{
			// *Uniform Kernel* has weight funciton w(u) = 1
			// therefore, a weight funciton lookup table is
			// not needed for this kernel -. w[i] = null indicates
			// this
		case Uniform:

			w        [i] = null;  //weight function not needed for this kernel
			offset   [i] =    1;  //uniform kernel has u < 1.0
			increment[i] =    1;  //has no meaning
			break;

			// *Gaussian Kernel* has weight function w(u) = constant*exp(-u^2/[2h[i]^2])
		case Gaussian:

			// Set uniformKernel to false
			uniformKernel = false;

			// generate weight function using expression,
			// exp(-u/2), where u = norm(xi - x)^2/h^2

			// Allocate memory for weight table
			w[i] = new double [GAUSS_NUM_ELS+1];

			for(j = 0; j <= GAUSS_NUM_ELS; j++)
				w[i][j] = Math.exp(-j*GAUSS_INCREMENT/2);

			// Set offset = offset^2, and set increment
			offset   [i] = (float)(GAUSS_LIMIT*GAUSS_LIMIT);
			increment[i] = GAUSS_INCREMENT;

			// done
			break;

			// *User Define Kernel* uses the weight function wf(u)
		case UserDefined:

			// Set uniformKernel to false
			uniformKernel = false;

			// Search for user defined weight function
			// defined for subspace (i+1)
			cur = head;
			while((cur!=null)&&(cur.subspace != (i+1)))
				cur = cur.next;

			// If a user defined subspace has not been found
			// for this subspace, flag an error
			if(cur == null)
			{
				                        throw new RuntimeException("\ngenerateLookupTable Fatal Error: User defined kernel for subspace "+ (i+1)+" undefined.\n\nAborting Program.\n\n");
				
			}

			// Otherwise, copy weight function lookup table to w[i]
			w[i] = new double [cur.sampleNumber+1];
			for(j = 0; j <= cur.sampleNumber; j++)
				w[i][j] = cur.w[j];

			// Set offset and increment accordingly
			offset   [i] = (float)(cur.halfWindow);
			increment[i] = cur.halfWindow/(float)(cur.sampleNumber);

			// done
			break;

		default:

			ErrorHandler("MeanShift", "generateLookupTable", "Unknown kernel type.");

		}

    }
}




/**
*Initialize Input
******************************************************
*Allocates memory for and initializes the input data
*structure.
******************************************************
*Pre:
*      - x is a floating point array of L, N dimens-
*        ional input data points
*Post:
*      - memory has been allocated for the input data
*        structure and x has been stored using into
*        the mean shift class using the resulting
*        structure.
*/

void InitializeInput(float x[])
{
	
	//allocate memory for input data set
	if(null==(data = new float [L*N]))
	{
		ErrorHandler("MeanShift", "InitializeInput", "Not enough memory.");
		return;
	}
	
	//copy x into data
	int i;
	for(i = 0; i < L*N; i++)
		data[i]	= x[i];
	
	//done.
	return;
	
}










}
