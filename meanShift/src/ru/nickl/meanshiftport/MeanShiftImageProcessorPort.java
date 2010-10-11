/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nickl.meanshiftport;

/**
 *
 * @author nickl
 */
public class MeanShiftImageProcessorPort extends MeanShiftPort
{

//    final double Xn = 0.95050;
    final double Yn = 1.00000;
    final double Zn = 1.08870;
    final double Un_prime = 0.19784977571475;
    final double Vn_prime = 0.46834507665248;
    final double Lt = 0.008856;
    final double XYZ[][] =
    {
        {
            0.4125, 0.3576, 0.1804
        },
        {
            0.2125, 0.7154, 0.0721
        },
        {
            0.0193, 0.1192, 0.9502
        }
    };    //LUV to RGB conversion
    final double RGB[][] =
    {
        {
            3.2405, -1.5371, -0.4985
        },
        {
            -0.9693, 1.8760, 0.0416
        },
        {
            0.0556, -0.2040, 1.0573
        }
    };    /////////Image Boundaries/////////
    RegionList regionList;			// stores the boundary locations for each region

    /////////Image Regions////////
    int regionCount;			// stores the number of connected regions contained by the
    // image

    /////////8 Connected Neighbors/////////
    int neigh[] = new int[8];    /////////Index Table/////////////////
    int indexTable[];			//used during fill algorithm

    /////////LUV_data/////////////////
    //int            *LUV_data;           //stores modes in integer format on lattice
    float LUV_data[];				//stores modes in float format on lattice
    float LUV_treshold;        //in float mode this determines what "close" means between modes

    ////////Raw Data (1 to 1 correlation with input)////////
    float msRawData[];				// Raw data output of mean shift algorithm
    // to the location of the data point on the lattice

    ////////Data Modes////////
    int labels[];				// assigns a label to each data point associating it to
    // a mode in modes (e.g. a data point having label l has
    // mode modes[l])
    float modes[];					// stores the mode data of the input data set, indexed by labels
    int modePointCounts[];		// stores for each mode the number of point mapped to that mode,
    // indexed by labels
    //////////Region Adjacency List/////////
    RAList raList[];				// an array of RAList objects containing an entry for each
    // region of the image

    //////////RAMatrix Free List///////////
    RAList freeRAList;			// a pointer to the head of a region adjacency list object
    // free list
    RAList raPool[];				// a pool of RAList objects used in the construction of the
    // RAM

    //////////Epsilon///////////
    float epsilon;				//Epsilon used for transitive closure

    //////Visit Table//////
    int visitTable[];			//Table used to keep track of which pixels have been
    //already visited upon computing the boundary edge strengths
    ////////Transitive Closure/////////
    float rR2;					//defines square range radius used when clustering pixels
    //together, thus defining image regions
    float speedThreshold; // the % of window radius used in new optimized filter 2.
    private float percent_complete;
    private final int PROGRESS_RATE = 100;
    private final int NODE_MULTIPLE = 10;

    /**
     *Class finalructor
     ******************************************************
     *Post:
     *      The msImageProcessor class has been properly
     *      initialized.
     */
    public MeanShiftImageProcessorPort()
    {


        //intialize basin of attraction structure
        //used by the filtering algorithm
        modeTable = null;
        pointList = null;
        pointCount = 0;

        //initialize region list
        regionList = null;

        //initialize output structures...
        msRawData = null;
        labels = null;
        modes = null;
        modePointCounts = null;
        regionCount = 0;

        //intialize temporary buffers used for
        //performing connected components
        indexTable = null;
        LUV_data = null;

        //initialize region adjacency matrix
        raList = null;
        freeRAList = null;
        raPool = null;

        //intialize visit table to having null entries
        visitTable = null;

        //initialize epsilon such that transitive closure
        //does not take edge strength into consideration when
        //fusing regions of similar color
        epsilon = 1.0f;

        //initialize class state to indicate that
        //an output data structure has not yet been
        //created...
        class_state.OUTPUT_DEFINED = false;


        LUV_treshold = 1.0f;
    }

    /**
     *Class Destructor
     ******************************************************
     *Post:
     *      The msImageProcessor class has been properly
     *      destroyed.
     */
    public void destructor()
    {        //de-allocate memory
        //if(class_state.OUTPUT_DEFINED)	DestroyOutput();
        //if(regionList)					//delete regionList;
        //regionList = null;

        //done.
    }

    /**
     *Define Image
     ******************************************************
     *Uploads an image into the image segmenter class to
     *be segmented.
     ******************************************************
     *Pre:
     *      - data_ is a one dimensional array of unsigned
     *        char RGB vectors
     *      - type is the type of the image: COLOR or
     *        GREYSCALE
     *      - height_ and width_ define the dimension of
     *        the image
     *      - if the image is of type GREYSCALE then
     *        data containes only one number per pixel
     *        location, where a pixel location is defined
     *        by the index into the data array
     *Post:
     *      - the image specified has been uploaded into
     *        the image segmenter class to be segmented.
     */
    public void DefineImage(int data_[], imageType type, int height_, int width_)
    {

        //obtain image dimension from image type
        int dim;
        if (type == imageType.COLOR)
        {
            dim = 3;
        } else
        {
            dim = 1;
        }

        //perfor rgb to luv conversion
        int i;
        float luv[] = new float[height_ * width_ * dim];
        if (dim == 1)
        {
            for (i = 0; i < height_ * width_; i++)
            {
                luv[i] = (float) (data_[i]);
            }
        } else
        {
            for (i = 0; i < height_ * width_; i++)
            {
                RGBtoLUV(data_, dim * i, luv, dim * i);
            }
        }

        //define input defined on a lattice using mean shift base class
        DefineLInput(luv, height_, width_, dim);

        //Define a default kernel if it has not been already
        //defined by user
        if (null == h)
        {
            //define default kernel paramerters...
            kernelType k[] = new kernelType[]
            {
                kernelType.Uniform, kernelType.Uniform
            };
            int P[] = new int[]
            {
                2, N
            };
            float tempH[] = new float[]
            {
                1.0f, 1.0f
            };

            //define default kernel in mean shift base class
            DefineKernel(k, tempH, P, 2);
        }

        //de-allocate memory
        ////delete [] luv;

        //done.
        return;

    }

    /**
     *Filter
     ******************************************************
     *Performs mean shift filtering on the specified input
     *image using a user defined kernel.
     ******************************************************
     *Pre:
     *      - the user defined kernel used to apply mean
     *        shift filtering to the defined input image
     *        has spatial bandwidth sigmaS and range band-
     *        width sigmaR
     *      - speedUpLevel determines whether or not the
     *        filtering should be optimized for faster
     *        execution: a value of NO_SPEEDUP turns this
     *        optimization off and a value SPEEDUP turns
     *        this optimization on
     *      - a data set has been defined
     *      - the height and width of the lattice has been
     *        specified using method DefineLattice()
     *Post:
     *      - mean shift filtering has been applied to the
     *        input image using a user defined kernel
     *      - the filtered image is stored in the private
     *        data members of the msImageProcessor class.
     */
    public void Filter(int sigmaS, float sigmaR, SpeedUpLevel speedUpLevel)
    {

        //Check Class consistency...

        //check:
        // (1) if this operation is consistent
        // (2) if kernel was created
        // (3) if data set is defined
        // (4) if the dimension of the kernel agrees with that
        //     of the defined data set
        // if not ... flag an errornull==
        classConsistencyCheck(N + 2, true);
        if (ErrorStatus == ErrorStatus.EL_ERROR)
        {
            return;
        }

        //If the algorithm has been halted, then exit
        if ((ErrorStatus = msSys.Progress((float) (0.0))) == ErrorLevel.EL_HALT)
        {
            return;
        }

        //If the image has just been read then allocate memory
        //for and initialize output data structure used to store
        //image modes and their corresponding regions...
        if (class_state.OUTPUT_DEFINED == false)
        {
            InitializeOutput();

            //check for errors...
            if (ErrorStatus == ErrorStatus.EL_ERROR)
            {
                return;
            }
        }

        //****************** Allocate Memory ******************

        //Allocate memory for basin of attraction mode structure...
        if ((null == (modeTable = new int[L])) || (null == (pointList = new int[L])))
        {
            ErrorHandler("msImageProcessor", "Allocate", "Not enough memory.");
            return;
        }

        //start timer
        double timer;
        msSys.StartTimer();

        //*****************************************************

        //filter image according to speedup level...
        switch (speedUpLevel)
        {
            //no speedup...
            case NO_SPEEDUP:
                //NonOptimizedFilter((float)(sigmaS), sigmaR);	break;
                NewNonOptimizedFilter((float) (sigmaS), sigmaR);
                break;
            //medium speedup
            case MED_SPEEDUP:
                //OptimizedFilter1((float)(sigmaS), sigmaR);		break;
                NewOptimizedFilter1((float) (sigmaS), sigmaR);
                break;
            //high speedup
            case HIGH_SPEEDUP:
                //OptimizedFilter2((float)(sigmaS), sigmaR);		break;
                NewOptimizedFilter2((float) (sigmaS), sigmaR);
                break;
            // new speedup
        }

        //****************** Deallocate Memory ******************

        //de-allocate memory used by basin of attraction mode structure
        //delete [] modeTable;
        //delete [] pointList;

        //re-initialize structure
        modeTable = null;
        pointList = null;
        pointCount = 0;

        //*******************************************************

        //If the algorithm has been halted, then de-allocate the output
        //and exit
        if ((ErrorStatus = msSys.Progress((float) (0.8))) == ErrorLevel.EL_HALT)
        {
            DestroyOutput();
            return;
        }

        //Label image regions, also if segmentation is not to be
        //performed use the resulting classification structure to
        //calculate the image boundaries...

        /*
        //copy msRawData into LUV_data, rounding each component of each
        //LUV value stored by msRawData to the nearest integer
        int	i;
        for(i = 0; i < L*N; i++)
        {
        if(msRawData[i] < 0)
        LUV_data[i] = (int)(msRawData[i] - 0.5);
        else
        LUV_data[i] = (int)(msRawData[i] + 0.5);
        }
         */
        int i;
        for (i = 0; i < L * N; i++)
        {
            LUV_data[i] = msRawData[i];
        }



        timer = msSys.ElapsedTime();
        msSys.Prompt("(%6.2f sec)\nConnecting regions         ...", timer);
        msSys.StartTimer();


        //Perform connecting (label image regions) using LUV_data
        Connect();


        timer = msSys.ElapsedTime();
        msSys.Prompt("done. (%6.2f seconds, numRegions = %6d)\n", timer, regionCount);
        msSys.StartTimer();



        //done.
        return;

    }

    /**
     *Fuse Regions
     ******************************************************
     *Fuses the regions of a filtered image.
     ******************************************************
     *Pre:
     *      - the range radius is specified by sigmaR
     *      - minRegion is the minimum point density that
     *        a region may have in the resulting segment-
     *        ed image
     *      - a data set has been defined
     *      - the height and width of the lattice has been
     *        specified using method DefineLattice()
     *Post:
     *      - the image regions have been fused.
     *      - if an result is stored by this class then
     *        this result is used as input to this method.
     *      - if no result is stored by this class,
     *        the input image defined by calling the
     *        method DefineImage is used.
     */
    public void FuseRegions(float sigmaS, int minRegion)
    {

        //Check Class consistency...

        //check:
        // (1) if this operation is consistent
        // (2) if kernel was created
        // (3) if data set is defined
        // (4) if the dimension of the kernel agrees with that
        //     of the defined data set
        // if not ... flag an errornull==
        classConsistencyCheck(N + 2, true);
        if (ErrorStatus == ErrorStatus.EL_ERROR)
        {
            return;
        }

        //Check to see if the algorithm is to be halted, if so then
        //destroy output and exit
        if ((ErrorStatus = msSys.Progress((float) (0.8))) == ErrorLevel.EL_HALT)
        {
            if (class_state.OUTPUT_DEFINED)
            {
                DestroyOutput();
            }
            return;
        }

        //obtain sigmaS (make sure it is not zero or negative, if not
        //flag an error)
        if ((h[1] = sigmaS) <= 0)
        {
            ErrorHandler("msImageProcessor", "FuseRegions", "The feature radius must be greater than or equal to zero.");
            return;
        }

        //if output has not yet been generated then classify the input
        //image regions to be fused...
        if (!(class_state.OUTPUT_DEFINED))
        {

            //Initialize output data structure used to store
            //image modes and their corresponding regions...
            InitializeOutput();

            //check for errors...
            if (ErrorStatus == ErrorStatus.EL_ERROR)
            {
                return;
            }

            //copy data into LUV_data used to classify
            //image regions
      /*
            int i;
            for(i = 0; i < L*N; i++)
            {
            if(data[i] < 0)
            LUV_data[i] = (int)(data[i] - 0.5);
            else
            LUV_data[i] = (int)(data[i] + 0.5);
            }
             */
            int i;
            for (i = 0; i < L * N; i++)
            {
                LUV_data[i] = data[i];
            }


            msSys.Prompt("Connecting regions         ...");
            msSys.StartTimer();


            //Perform connecting (label image regions) using LUV_data
            Connect();

            //check for errors
            if (ErrorStatus == ErrorStatus.EL_ERROR)
            {
                return;
            }


            double timer = msSys.ElapsedTime();
            msSys.Prompt("done. (%6.2f seconds, numRegions = %6d)\n", timer, regionCount);


        }

        //Check to see if the algorithm is to be halted, if so then
        //destroy output and exit
        if ((ErrorStatus = msSys.Progress((float) (0.85))) == ErrorLevel.EL_HALT)
        {
            DestroyOutput();
            return;
        }


        msSys.Prompt("Applying transitive closure...");
        msSys.StartTimer();


        //allocate memory visit table
        visitTable = new int[L];

        //Apply transitive closure iteratively to the regions classified
        //by the RAM updating labels and modes until the color of each neighboring
        //region is within sqrt(rR2) of one another.
        rR2 = (float) (h[1] * h[1] * 0.25);
        TransitiveClosure();
        int oldRC = regionCount;
        int deltaRC, counter = 0;
        do
        {
            TransitiveClosure();
            deltaRC = oldRC - regionCount;
            oldRC = regionCount;
            counter++;
        } while ((deltaRC <= 0) && (counter < 10));

        //de-allocate memory for visit table
        //delete [] visitTable;
        visitTable = null;

        //Check to see if the algorithm is to be halted, if so then
        //destroy output and region adjacency matrix and exit
        if ((ErrorStatus = msSys.Progress((float) (1.0))) == ErrorLevel.EL_HALT)
        {
            DestroyRAM();
            DestroyOutput();
            return;
        }


        double timer = msSys.ElapsedTime();
        msSys.Prompt("done. (%6.2f seconds, numRegions = %6d)\nPruning spurious regions   ...", timer, regionCount);
        msSys.StartTimer();


        //Prune spurious regions (regions whose area is under
        //minRegion) using RAM
        Prune(minRegion);


        timer = msSys.ElapsedTime();
        msSys.Prompt("done. (%6.2f seconds, numRegions = %6d)\n", timer, regionCount);
        msSys.StartTimer();


        //Check to see if the algorithm is to be halted, if so then
        //destroy output and region adjacency matrix and exit
        if ((ErrorStatus = msSys.Progress((float) (1.0))) == ErrorLevel.EL_HALT)
        {
            DestroyRAM();
            DestroyOutput();
            return;
        }

        //de-allocate memory for region adjacency matrix
        DestroyRAM();

        //output to msRawData
        int i, j, label;
        for (i = 0; i < L; i++)
        {
            label = labels[i];
            for (j = 0; j < N; j++)
            {
                msRawData[N * i + j] = modes[N * label + j];
            }
        }

        //done.
        return;

    }

    /**
     *Segment
     ******************************************************
     *Segments the defined image.
     ******************************************************
     *Pre:
     *      - sigmaS and sigmaR are the spatial and range
     *        radii of the search window respectively
     *      - minRegion is the minimum point density that
     *        a region may have in the resulting segment-
     *        ed image
     *      - speedUpLevel determines whether or not the
     *        filtering should be optimized for faster
     *        execution: a value of NO_SPEEDUP turns this
     *        optimization off and a value SPEEDUP turns
     *        this optimization on
     *Post:
     *      - the defined image is segmented and the
     *        resulting segmented image is stored in the
     *        private data members of the image segmenter
     *        class.
     *      - any regions whose point densities are less
     *        than or equal to minRegion have been pruned
     *        from the segmented image.
     */
    public void Segment(int sigmaS, float sigmaR, int minRegion, SpeedUpLevel speedUpLevel)
    {

        //make sure kernel is properly defined...
        if ((null == h) || (kp < 2))
        {
            ErrorHandler("msImageProcessor", "Segment", "Kernel corrupt or undefined.");
            return;
        }

        //Apply mean shift to data set using sigmaS and sigmaR...
        Filter(sigmaS, sigmaR, speedUpLevel);

        //check for errors
        if (ErrorStatus == ErrorStatus.EL_ERROR)
        {
            return;
        }

        //check to see if the system has been halted, if so exit
        if (ErrorStatus == ErrorLevel.EL_HALT)
        {
            return;
        }

        //Check to see if the algorithm is to be halted, if so then
        //destroy output and exit
        if ((ErrorStatus = msSys.Progress((float) (0.85))) == ErrorLevel.EL_HALT)
        {
            DestroyOutput();
            return;
        }


        msSys.Prompt("Applying transitive closure...");
        msSys.StartTimer();


        //allocate memory visit table
        visitTable = new int[L];

        //Apply transitive closure iteratively to the regions classified
        //by the RAM updating labels and modes until the color of each neighboring
        //region is within sqrt(rR2) of one another.
        rR2 = (float) (h[1] * h[1] * 0.25);
        TransitiveClosure();
        int oldRC = regionCount;
        int deltaRC, counter = 0;
        do
        {
            TransitiveClosure();
            deltaRC = oldRC - regionCount;
            oldRC = regionCount;
            counter++;
        } while ((deltaRC <= 0) && (counter < 10));

        //de-allocate memory for visit table
        //delete [] visitTable;
        visitTable = null;

        //Check to see if the algorithm is to be halted, if so then
        //destroy output and regions adjacency matrix and exit
        if ((ErrorStatus = msSys.Progress((float) (0.95))) == ErrorLevel.EL_HALT)
        {
            DestroyRAM();
            DestroyOutput();
            return;
        }


        double timer = msSys.ElapsedTime();
        msSys.Prompt("done. (%6.2f seconds, numRegions = %6d).\nPruning spurious regions\t... ", timer, regionCount);
        msSys.StartTimer();


        //Prune spurious regions (regions whose area is under
        //minRegion) using RAM
        Prune(minRegion);


        timer = msSys.ElapsedTime();
        msSys.Prompt("done. (%6.2f seconds, numRegions = %6d)\nPruning spurious regions    ...", timer, regionCount);
        msSys.StartTimer();


        //Check to see if the algorithm is to be halted, if so then
        //destroy output and regions adjacency matrix and exit
        if ((ErrorStatus = msSys.Progress(1.0)) == ErrorLevel.EL_HALT)
        {
            DestroyRAM();
            DestroyOutput();
            return;
        }

        //de-allocate memory for region adjacency matrix
        DestroyRAM();

        //output to msRawData
        int j, i, label;
        for (i = 0; i < L; i++)
        {
            label = labels[i];
            for (j = 0; j < N; j++)
            {
                msRawData[N * i + j] = modes[N * label + j];
            }
        }

        //done.
        return;

    }

    /**
     *RGB To LUV
     ******************************************************
     *Converts an RGB vector to LUV.
     *
     *See:
     *   G. Wyszecki and W.S. Stiles: Color Science:
     *   Concepts and Methods, Quantitative Data and
     *   Formulae, Wiley, New York, 1982.
     ******************************************************
     *Pre:
     *      - rgbVal is an int array containing
     *        the RGB vector
     *      - luvVal is a floating point array containing
     *        the resulting LUV vector
     *Post:
     *      - rgbVal has been converted to LUV and the
     *        result has been stored in luvVal.
     */
    void RGBtoLUV(int[] rgbVal, int fi, float luvVal[], int filuv)
    {

        //delcare variables
        double x, y, z, L0, u_prime, v_prime, finalant;

        //convert RGB to XYZ...
        x = XYZ[0][0] * rgbVal[fi + 0] + XYZ[0][1] * rgbVal[fi + 1] + XYZ[0][2] * rgbVal[fi + 2];
        y = XYZ[1][0] * rgbVal[fi + 0] + XYZ[1][1] * rgbVal[fi + 1] + XYZ[1][2] * rgbVal[fi + 2];
        z = XYZ[2][0] * rgbVal[fi + 0] + XYZ[2][1] * rgbVal[fi + 1] + XYZ[2][2] * rgbVal[fi + 2];

        //convert XYZ to LUV...

        //compute L*
        L0 = y / (255.0 * Yn);
        if (L0 > Lt)
        {
            luvVal[filuv + 0] = (float) (116.0 * (Math.pow(L0, 1.0 / 3.0)) - 16.0);
        } else
        {
            luvVal[filuv + 0] = (float) (903.3 * L0);
        }

        //compute u_prime and v_prime
        finalant = x + 15 * y + 3 * z;
        if (finalant != 0)
        {
            u_prime = (4 * x) / finalant;
            v_prime = (9 * y) / finalant;
        } else
        {
            u_prime = 4.0;
            v_prime = 9.0 / 15.0;
        }

        //compute u* and v*
        luvVal[filuv + 1] = (float) (13 * luvVal[filuv + 0] * (u_prime - Un_prime));
        luvVal[filuv + 2] = (float) (13 * luvVal[filuv + 0] * (v_prime - Vn_prime));

        //done.
        return;

    }

    /**
     *LUV To RGB
     ******************************************************
     *Converts an LUV vector to RGB.
     ******************************************************
     *Pre:
     *      - luvVal is a floating point array containing
     *        the LUV vector
     *      - rgbVal is an int array containing
     *        the resulting RGB vector
     *Post:
     *      - luvVal has been converted to RGB and the
     *        result has been stored in rgbVal.
     *///define inline rounding function...
    int my_round(double in_x)
    {
        if (in_x < 0)
        {
            return (int) (in_x - 0.5);
        } else
        {
            return (int) (in_x + 0.5);
        }
    }

    void LUVtoRGB(float luvVal[], int luvValind, int rgbVal[], int rgbValind)
    {

        //declare variables...
        int r, g, b;
        double x, y, z, u_prime, v_prime;

        //perform conversion
        if (luvVal[luvValind + 0] < 0.1)
        {
            r = g = b = 0;
        } else
        {
            //convert luv to xyz...
            if (luvVal[luvValind + 0] < 8.0)
            {
                y = Yn * luvVal[luvValind + 0] / 903.3;
            } else
            {
                y = (luvVal[luvValind + 0] + 16.0) / 116.0;
                y *= Yn * y * y;
            }

            u_prime = luvVal[luvValind + 1] / (13 * luvVal[luvValind + 0]) + Un_prime;
            v_prime = luvVal[luvValind + 2] / (13 * luvVal[luvValind + 0]) + Vn_prime;

            x = 9 * u_prime * y / (4 * v_prime);
            z = (12 - 3 * u_prime - 20 * v_prime) * y / (4 * v_prime);

            //convert xyz to rgb...
            //[r, g, b] = RGB*[x, y, z]*255.0
            r = my_round((RGB[0][0] * x + RGB[0][1] * y + RGB[0][2] * z) * 255.0);
            g = my_round((RGB[1][0] * x + RGB[1][1] * y + RGB[1][2] * z) * 255.0);
            b = my_round((RGB[2][0] * x + RGB[2][1] * y + RGB[2][2] * z) * 255.0);

            //check bounds...
            if (r < 0)
            {
                r = 0;
            }
            if (r > 255)
            {
                r = 255;
            }
            if (g < 0)
            {
                g = 0;
            }
            if (g > 255)
            {
                g = 255;
            }
            if (b < 0)
            {
                b = 0;
            }
            if (b > 255)
            {
                b = 255;
            }

        }

        //assign rgb values to rgb vector rgbVal[rgbValind+
        rgbVal[rgbValind + 0] = (int) r;
        rgbVal[rgbValind + 1] = (int) g;
        rgbVal[rgbValind + 2] = (int) b;

        //done.
        return;

    }

    /**
     *Get Raw Data
     ******************************************************
     *The output image data is returned.
     ******************************************************
     *Pre:
     *      - outputImageData is a pre-allocated floating
     *        point array used to store the filtered or
     *        segmented image pixels.
     *Post:
     *      - the filtered or segmented image data is
     *        stored by outputImageData.
     */
    public void GetRawData(float outputImageData[])
    {
        //make sure that outputImageData is not null
        if (null == outputImageData)
        {
            ErrorHandler("msImageProcessor", "GetRawData", "Output image data buffer is null.");
            return;
        }

        //copy msRawData to outputImageData
        int i;
        for (i = 0; i < L * N; i++)
        {
            outputImageData[i] = msRawData[i];
        }

        //done.
        return;
    }

    /**
     *Get Results
     ******************************************************
     *The output image is returned.
     ******************************************************
     *Pre:
     *      - outputImage is a pre-allocated unsinged char
     *        array used to store the filtered or segment-
     *        ed image pixels
     *Post:
     *      - the filtered or segmented image is stored by
     *        outputImage.
     */
    public void GetResults(int outputImage[])
    {

        //make sure that outpuImage is not null
        if (null == outputImage)
        {
            ErrorHandler("msImageProcessor", "GetResults", "Output image buffer is null.");
            return;
        }

        //if the image type is GREYSCALE simply
        //copy it over to the segmentedImage
        if (N == 1)
        {
            //copy over msRawData to segmentedImage checking
            //bounds
            int i, pxValue;
            for (i = 0; i < L; i++)
            {

                //get value
                pxValue = (int) (msRawData[i] + 0.5);

                //store into segmented image checking bounds...
                if (pxValue < 0)
                {
                    outputImage[i] = (int) (0);
                } else if (pxValue > 255)
                {
                    outputImage[i] = (int) (255);
                } else
                {
                    outputImage[i] = (int) (pxValue);
                }

            }

        } else if (N == 3)
        {

            //otherwise convert msRawData from LUV to RGB
            //storing the result in segmentedImage
            int i;
            for (i = 0; i < L; i++)
            {
                LUVtoRGB(msRawData, N * i, outputImage, N * i);
            }

        } else //Unknown image type: should use MeanShift::GetRawData()...
        {
            ErrorHandler("msImageProcessor", "GetResults", "Unknown image type. Try using MeanShift::GetRawData().");
        }

        //done.
        return;

    }

    /**
     *Get Boundaries
     ******************************************************
     *A region list containing the boundary locations for
     *each region is returned.
     ******************************************************
     *Post:
     *      - a region list object containing the boundary
     *        locations for each region is finalructed
     *      - the region list is returned
     *      - null is returned if the image has not been
     *        filtered or segmented
     */
    public RegionList GetBoundaries()
    {

        //define bounds using label information
        if (class_state.OUTPUT_DEFINED)
        {
            DefineBoundaries();
        }

        //return region list structure
        return regionList;

    }

    void Connect()
    {

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

    /**
     *Fill
     ******************************************************
     *Given a region seed and a region label, Fill uses
     *the region seed to perform an eight-connected fill
     *for the specified region, labeling all pixels con-
     *tained by the region with the specified label:
     *label.
     ******************************************************
     *Pre:
     *      - regionLoc is a region seed - a pixel that is
     *        identified as being part of the region
     *        labled using the label, label.
     *Post:
     *      - all pixels belonging to the region specified
     *        by regionLoc (having the same integer LUV
     *        value specified by LUV_data) are classified
     *        as one region by labeling each pixel in the
     *        image clasification structure using label
     *        via an eight-connected fill.
     */
    void Fill(int regionLoc, int label)
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

    /**
     *Build Region Adjacency Matrix
     ******************************************************
     *finalructs a region adjacency matrix.
     ******************************************************
     *Pre:
     *      - the classification data structure has been
     *        finalructed.
     *Post:
     *      - a region adjacency matrix has been built
     *        using the classification data structure.
     */
    void BuildRAM()
    {

        //Allocate memory for region adjacency matrix if it hasn't already been allocated
        raList = new RAList[regionCount];

        for (int ii = 0; ii < raList.length; ii++)
        {
            raList[ii] = new RAList();
        }

        raPool = new RAList[NODE_MULTIPLE * regionCount];

        for (int ii = 0; ii < raPool.length; ii++)
        {
            raPool[ii] = new RAList();

        }


        //initialize the region adjacency list
        int i;
        for (i = 0; i < regionCount; i++)
        {
            raList[i].edgeStrength = 0;
            raList[i].edgePixelCount = 0;
            raList[i].label = i;
            raList[i].next = null;
        }

        //initialize RAM free list
        freeRAList = raPool[0];
        for (i = 0; i < NODE_MULTIPLE * regionCount - 1; i++)
        {
            raPool[i].edgeStrength = 0;
            raPool[i].edgePixelCount = 0;
            raPool[i].next = raPool[i + 1];
        }
        raPool[NODE_MULTIPLE * regionCount - 1].next = null;

        //traverse the labeled image building
        //the RAM by looking to the right of
        //and below the current pixel location thus
        //determining if a given region is adjacent
        //to another
        int j, curLabel, rightLabel, bottomLabel, exists;
        RAList raNode1, raNode2, oldRAFreeList;
        for (i = 0; i < height - 1; i++)
        {
            //check the right and below neighbors
            //for pixel locations whose x < width - 1
            for (j = 0; j < width - 1; j++)
            {
                //calculate pixel labels
                curLabel = labels[i * width + j];	//current pixel
                rightLabel = labels[i * width + j + 1];	//right   pixel
                bottomLabel = labels[(i + 1) * width + j];	//bottom  pixel

                //check to the right, if the label of
                //the right pixel is not the same as that
                //of the current one then region[j] and region[j+1]
                //are adjacent to one another - update the RAM
                if (curLabel != rightLabel)
                {
                    //obtain RAList object from region adjacency free
                    //list
                    raNode1 = freeRAList;
                    raNode2 = freeRAList.next;

                    //keep a pointer to the old region adj. free
                    //list just in case nodes already exist in respective
                    //region lists
                    oldRAFreeList = freeRAList;

                    //update region adjacency free list
                    freeRAList = freeRAList.next.next;

                    //populate RAList nodes
                    raNode1.label = curLabel;
                    raNode2.label = rightLabel;

                    //insert nodes into the RAM
                    exists = 0;
                    raList[curLabel].Insert(raNode2);
                    exists = raList[rightLabel].Insert(raNode1);

                    //if the node already exists then place
                    //nodes back onto the region adjacency
                    //free list
                    if (exists != 0)
                    {
                        freeRAList = oldRAFreeList;
                    }

                }

                //check below, if the label of
                //the bottom pixel is not the same as that
                //of the current one then region[j] and region[j+width]
                //are adjacent to one another - update the RAM
                if (curLabel != bottomLabel)
                {
                    //obtain RAList object from region adjacency free
                    //list
                    raNode1 = freeRAList;
                    raNode2 = freeRAList.next;

                    //keep a pointer to the old region adj. free
                    //list just in case nodes already exist in respective
                    //region lists
                    oldRAFreeList = freeRAList;

                    //update region adjacency free list
                    freeRAList = freeRAList.next.next;

                    //populate RAList nodes
                    raNode1.label = curLabel;
                    raNode2.label = bottomLabel;

                    //insert nodes into the RAM
                    exists = 0;
                    raList[curLabel].Insert(raNode2);
                    exists = raList[bottomLabel].Insert(raNode1);

                    //if the node already exists then place
                    //nodes back onto the region adjacency
                    //free list
                    if (exists != 0)
                    {
                        freeRAList = oldRAFreeList;
                    }

                }

            }

            //check only to the bottom neighbors of the right boundary
            //pixels...

            //calculate pixel locations (j = width-1)
            curLabel = labels[i * width + j];	//current pixel
            bottomLabel = labels[(i + 1) * width + j];	//bottom  pixel

            //check below, if the label of
            //the bottom pixel is not the same as that
            //of the current one then region[j] and region[j+width]
            //are adjacent to one another - update the RAM
            if (curLabel != bottomLabel)
            {
                //obtain RAList object from region adjacency free
                //list
                raNode1 = freeRAList;
                raNode2 = freeRAList.next;

                //keep a pointer to the old region adj. free
                //list just in case nodes already exist in respective
                //region lists
                oldRAFreeList = freeRAList;

                //update region adjacency free list
                freeRAList = freeRAList.next.next;

                //populate RAList nodes
                raNode1.label = curLabel;
                raNode2.label = bottomLabel;

                //insert nodes into the RAM
                exists = 0;
                raList[curLabel].Insert(raNode2);
                exists = raList[bottomLabel].Insert(raNode1);

                //if the node already exists then place
                //nodes back onto the region adjacency
                //free list
                if (exists != 0)
                {
                    freeRAList = oldRAFreeList;
                }

            }
        }

        //check only to the right neighbors of the bottom boundary
        //pixels...

        //check the right for pixel locations whose x < width - 1
        for (j = 0; j < width - 1; j++)
        {
            //calculate pixel labels (i = height-1)
            curLabel = labels[i * width + j];	//current pixel
            rightLabel = labels[i * width + j + 1];	//right   pixel

            //check to the right, if the label of
            //the right pixel is not the same as that
            //of the current one then region[j] and region[j+1]
            //are adjacent to one another - update the RAM
            if (curLabel != rightLabel)
            {
                //obtain RAList object from region adjacency free
                //list
                raNode1 = freeRAList;
                raNode2 = freeRAList.next;

                //keep a pointer to the old region adj. free
                //list just in case nodes already exist in respective
                //region lists
                oldRAFreeList = freeRAList;

                //update region adjacency free list
                freeRAList = freeRAList.next.next;

                //populate RAList nodes
                raNode1.label = curLabel;
                raNode2.label = rightLabel;

                //insert nodes into the RAM
                exists = 0;
                raList[curLabel].Insert(raNode2);
                exists = raList[rightLabel].Insert(raNode1);

                //if the node already exists then place
                //nodes back onto the region adjacency
                //free list
                if (exists != 0)
                {
                    freeRAList = oldRAFreeList;
                }

            }

        }

        //done.
        return;

    }

    /**
     *Destroy Region Adjacency Matrix
     ******************************************************
     *Destroy a region adjacency matrix.
     ******************************************************
     *Post:
     *      - the region adjacency matrix has been destr-
     *        oyed: (1) its memory has been de-allocated,
     *        (2) the RAM structure has been initialize
     *        for re-use.
     */
    void DestroyRAM()
    {

        //de-allocate memory for region adjaceny list
        //if (raList)				//delete [] raList;
        //if (raPool)				//delete [] raPool;

        //initialize region adjacency matrix
        raList = null;
        freeRAList = null;
        raPool = null;

        //done.
        return;

    }

    /**
     *Transitive Closure
     ******************************************************
     *Applies transitive closure to the RAM updating
     *labels, modes and modePointCounts to reflect the new
     *set of merged regions resulting from transitive clo-
     *sure.
     ******************************************************
     *Post:
     *      - transitive closure has been applied to the
     *        regions classified by the RAM and labels,
     *        modes and modePointCounts have been updated
     *        to reflect the new set of mergd regions res-
     *        ulting from transitive closure.
     */
    void TransitiveClosure()
    {

        //Step (1):

        // Build RAM using classifiction structure originally
        // generated by the method GridTable::Connect()
        BuildRAM();

        //Step (1a):
        //Compute weights of weight graph using confidence map
        //(if defined)
        if (weightMapDefined)
        {
            ComputeEdgeStrengths();
        }

        //Step (2):

        //Treat each region Ri as a disjoint set:

        // - attempt to join Ri and Rj for all i != j that are neighbors and
        //   whose associated modes are a normalized distance of < 0.5 from one
        //   another

        // - the label of each region in the raList is treated as a pointer to the
        //   canonical element of that region (e.g. raList[i], initially has raList[i].label = i,
        //   namely each region is initialized to have itself as its canonical element).

        //Traverse RAM attempting to join raList[i] with its neighbors...
        int i, iCanEl, neighCanEl;
        float threshold;
        RAList neighbor;
        for (i = 0; i < regionCount; i++)
        {
            //aquire first neighbor in region adjacency list pointed to
            //by raList[i]
            neighbor = raList[i].next;

            //compute edge strenght threshold using global and local
            //epsilon
            if (epsilon > raList[i].edgeStrength)
            {
                threshold = epsilon;
            } else
            {
                threshold = raList[i].edgeStrength;
            }

            //traverse region adjacency list of region i, attempting to join
            //it with regions whose mode is a normalized distance < 0.5 from
            //that of region i...
            while (neighbor != null)
            {
                //attempt to join region and neighbor...
                if ((InWindow(i, neighbor.label)) && (neighbor.edgeStrength < epsilon))
                {
                    //region i and neighbor belong together so join them
                    //by:

                    // (1) find the canonical element of region i
                    iCanEl = i;
                    while (raList[iCanEl].label != iCanEl)
                    {
                        iCanEl = raList[iCanEl].label;
                    }

                    // (2) find the canonical element of neighboring region
                    neighCanEl = neighbor.label;
                    while (raList[neighCanEl].label != neighCanEl)
                    {
                        neighCanEl = raList[neighCanEl].label;
                    }

                    // if the canonical elements of are not the same then assign
                    // the canonical element having the smaller label to be the parent
                    // of the other region...
                    if (iCanEl < neighCanEl)
                    {
                        raList[neighCanEl].label = iCanEl;
                    } else
                    {
                        //must replace the canonical element of previous
                        //parent as well
                        raList[raList[iCanEl].label].label = neighCanEl;

                        //re-assign canonical element
                        raList[iCanEl].label = neighCanEl;
                    }
                }

                //check the next neighbor...
                neighbor = neighbor.next;

            }
        }

        // Step (3):

        // Level binary trees formed by canonical elements
        for (i = 0; i < regionCount; i++)
        {
            iCanEl = i;
            while (raList[iCanEl].label != iCanEl)
            {
                iCanEl = raList[iCanEl].label;
            }
            raList[i].label = iCanEl;
        }

        // Step (4):

        //Traverse joint sets, relabeling image.

        // (a)

        // Accumulate modes and re-compute point counts using canonical
        // elements generated by step 2.

        //allocate memory for mode and point count temporary buffers...
        float modes_buffer[] = new float[N * regionCount];
        int MPC_buffer[] = new int[regionCount];

        //initialize buffers to zero
        for (i = 0; i < regionCount; i++)
        {
            MPC_buffer[i] = 0;
        }
        for (i = 0; i < N * regionCount; i++)
        {
            modes_buffer[i] = 0;
        }

        //traverse raList accumulating modes and point counts
        //using canoncial element information...
        int k, iMPC;
        for (i = 0; i < regionCount; i++)
        {

            //obtain canonical element of region i
            iCanEl = raList[i].label;

            //obtain mode point count of region i
            iMPC = modePointCounts[i];

            //accumulate modes_buffer[iCanEl]
            for (k = 0; k < N; k++)
            {
                modes_buffer[(N * iCanEl) + k] += iMPC * modes[(N * i) + k];
            }

            //accumulate MPC_buffer[iCanEl]
            MPC_buffer[iCanEl] += iMPC;

        }

        // (b)

        // Re-label new regions of the image using the canonical
        // element information generated by step (2)

        // Also use this information to compute the modes of the newly
        // defined regions, and to assign new region point counts in
        // a consecute manner to the modePointCounts array

        //allocate memory for label buffer
        int label_buffer[] = new int[regionCount];

        //initialize label buffer to -1
        for (i = 0; i < regionCount; i++)
        {
            label_buffer[i] = -1;
        }

        //traverse raList re-labeling the regions
        int label = -1;
        for (i = 0; i < regionCount; i++)
        {
            //obtain canonical element of region i
            iCanEl = raList[i].label;
            if (label_buffer[iCanEl] < 0)
            {
                //assign a label to the new region indicated by canonical
                //element of i
                label_buffer[iCanEl] = ++label;

                //recompute mode storing the result in modes[label]...
                iMPC = MPC_buffer[iCanEl];
                for (k = 0; k < N; k++)
                {
                    modes[(N * label) + k] = (modes_buffer[(N * iCanEl) + k]) / (iMPC);
                }

                //assign a corresponding mode point count for this region into
                //the mode point counts array using the MPC buffer...
                modePointCounts[label] = MPC_buffer[iCanEl];
            }
        }

        //re-assign region count using label counter
        int oldRegionCount = regionCount;
        regionCount = label + 1;

        // (c)

        // Use the label buffer to refinalruct the label map, which specified
        // the new image given its new regions calculated above

        for (i = 0; i < height * width; i++)
        {
            labels[i] = label_buffer[raList[labels[i]].label];
        }

        //de-allocate memory
        ////delete [] modes_buffer;
        ////delete [] MPC_buffer;
        ////delete [] label_buffer;

        //done.
        return;

    }

    /**
     *Compute Edge Strengths
     ******************************************************
     *Computes the a weight for each link in the region
     *graph maintined by the RAM, resulting in a weighted
     *graph in which the weights consist of a confidence
     *between zero and one indicating if the regions are
     *separated by a strong or weak edge.
     ******************************************************
     *Post:
     *      - an edge strength has been computed between
     *        each region of the image and placed as a
     *        weight in the RAM to be used during transi-
     *        tive closure.
     */
    void ComputeEdgeStrengths()
    {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    /**
     *Prune
     ******************************************************
     *Prunes regions from the image whose pixel density
     *is less than a specified threshold.
     ******************************************************
     *Pre:
     *      - minRegion is the minimum allowable pixel de-
     *        nsity a region may have without being pruned
     *        from the image
     *Post:
     *      - regions whose pixel density is less than
     *        or equal to minRegion have been pruned from
     *        the image.
     */
    void Prune(int minRegion)
    {

        //Allocate Memory for temporary buffers...

        //allocate memory for mode and point count temporary buffers...
        float modes_buffer[] = new float[N * regionCount];
        int MPC_buffer[] = new int[regionCount];

        //allocate memory for label buffer
        int label_buffer[] = new int[regionCount];

        //Declare variables
        int i, k, candidate, iCanEl, neighCanEl, iMPC, label, oldRegionCount, minRegionCount;
        double minSqDistance, neighborDistance;
        RAList neighbor;

        //Apply pruning algorithm to classification structure, removing all regions whose area
        //is under the threshold area minRegion (pixels)
        do
        {
            //Assume that no region has area under threshold area  of
            minRegionCount = 0;

            //Step (1):

            // Build RAM using classifiction structure originally
            // generated by the method GridTable::Connect()
            BuildRAM();

            // Step (2):

            // Traverse the RAM joining regions whose area is less than minRegion (pixels)
            // with its respective candidate region.

            // A candidate region is a region that displays the following properties:

            //	- it is adjacent to the region being pruned

            //  - the distance of its mode is a minimum to that of the region being pruned
            //    such that or it is the only adjacent region having an area greater than
            //    minRegion

            for (i = 0; i < regionCount; i++)
            {
                //if the area of the ith region is less than minRegion
                //join it with its candidate region...

                //*******************************************************************************

                //Note: Adjust this if statement if a more sophisticated pruning criterion
                //      is desired. Basically in this step a region whose area is less than
                //      minRegion is pruned by joining it with its "closest" neighbor (in color).
                //      Therefore, by placing a different criterion for fusing a region the
                //      pruning method may be altered to implement a more sophisticated algorithm.

                //*******************************************************************************

                if (modePointCounts[i] < minRegion)
                {
                    //update minRegionCount to indicate that a region
                    //having area less than minRegion was found
                    minRegionCount++;

                    //obtain a pointer to the first region in the
                    //region adjacency list of the ith region...
                    neighbor = raList[i].next;

                    //calculate the distance between the mode of the ith
                    //region and that of the neighboring region...
                    candidate = neighbor.label;
                    minSqDistance = SqDistance(i, candidate);

                    //traverse region adjacency list of region i and select
                    //a candidate region
                    neighbor = neighbor.next;
                    while (neighbor != null)
                    {

                        //calculate the square distance between region i
                        //and current neighbor...
                        neighborDistance = SqDistance(i, neighbor.label);

                        //if this neighbors square distance to region i is less
                        //than minSqDistance, then select this neighbor as the
                        //candidate region for region i
                        if (neighborDistance < minSqDistance)
                        {
                            minSqDistance = neighborDistance;
                            candidate = neighbor.label;
                        }

                        //traverse region list of region i
                        neighbor = neighbor.next;

                    }

                    //join region i with its candidate region:

                    // (1) find the canonical element of region i
                    iCanEl = i;
                    while (raList[iCanEl].label != iCanEl)
                    {
                        iCanEl = raList[iCanEl].label;
                    }

                    // (2) find the canonical element of neighboring region
                    neighCanEl = candidate;
                    while (raList[neighCanEl].label != neighCanEl)
                    {
                        neighCanEl = raList[neighCanEl].label;
                    }

                    // if the canonical elements of are not the same then assign
                    // the canonical element having the smaller label to be the parent
                    // of the other region...
                    if (iCanEl < neighCanEl)
                    {
                        raList[neighCanEl].label = iCanEl;
                    } else
                    {
                        //must replace the canonical element of previous
                        //parent as well
                        raList[raList[iCanEl].label].label = neighCanEl;

                        //re-assign canonical element
                        raList[iCanEl].label = neighCanEl;
                    }
                }
            }

            // Step (3):

            // Level binary trees formed by canonical elements
            for (i = 0; i < regionCount; i++)
            {
                iCanEl = i;
                while (raList[iCanEl].label != iCanEl)
                {
                    iCanEl = raList[iCanEl].label;
                }
                raList[i].label = iCanEl;
            }

            // Step (4):

            //Traverse joint sets, relabeling image.

            // Accumulate modes and re-compute point counts using canonical
            // elements generated by step 2.

            //initialize buffers to zero
            for (i = 0; i < regionCount; i++)
            {
                MPC_buffer[i] = 0;
            }
            for (i = 0; i < N * regionCount; i++)
            {
                modes_buffer[i] = 0;
            }

            //traverse raList accumulating modes and point counts
            //using canoncial element information...
            for (i = 0; i < regionCount; i++)
            {

                //obtain canonical element of region i
                iCanEl = raList[i].label;

                //obtain mode point count of region i
                iMPC = modePointCounts[i];

                //accumulate modes_buffer[iCanEl]
                for (k = 0; k < N; k++)
                {
                    modes_buffer[(N * iCanEl) + k] += iMPC * modes[(N * i) + k];
                }

                //accumulate MPC_buffer[iCanEl]
                MPC_buffer[iCanEl] += iMPC;

            }

            // (b)

            // Re-label new regions of the image using the canonical
            // element information generated by step (2)

            // Also use this information to compute the modes of the newly
            // defined regions, and to assign new region point counts in
            // a consecute manner to the modePointCounts array

            //initialize label buffer to -1
            for (i = 0; i < regionCount; i++)
            {
                label_buffer[i] = -1;
            }

            //traverse raList re-labeling the regions
            label = -1;
            for (i = 0; i < regionCount; i++)
            {
                //obtain canonical element of region i
                iCanEl = raList[i].label;
                if (label_buffer[iCanEl] < 0)
                {
                    //assign a label to the new region indicated by canonical
                    //element of i
                    label_buffer[iCanEl] = ++label;

                    //recompute mode storing the result in modes[label]...
                    iMPC = MPC_buffer[iCanEl];
                    for (k = 0; k < N; k++)
                    {
                        modes[(N * label) + k] = (modes_buffer[(N * iCanEl) + k]) / (iMPC);
                    }

                    //assign a corresponding mode point count for this region into
                    //the mode point counts array using the MPC buffer...
                    modePointCounts[label] = MPC_buffer[iCanEl];
                }
            }

            //re-assign region count using label counter
            oldRegionCount = regionCount;
            regionCount = label + 1;

            // (c)

            // Use the label buffer to refinalruct the label map, which specified
            // the new image given its new regions calculated above

            for (i = 0; i < height * width; i++)
            {
                labels[i] = label_buffer[raList[labels[i]].label];
            }


        } while (minRegionCount > 0);

        //de-allocate memory
//	//delete [] modes_buffer;
//	//delete [] MPC_buffer;
//	//delete [] label_buffer;

        //done.
        return;

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
    void DefineBoundaries()
    {

        //declare and allocate memory for boundary map and count
        int boundaryMap[], boundaryCount[] = null;
        if ((null == (boundaryMap = new int[L])) || (null == (boundaryCount = new int[regionCount])))
        {
            ErrorHandler("msImageProcessor", "DefineBoundaries", "Not enough memory.");
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
            ErrorHandler("msImageProcessor", "DefineBoundaries", "Not enough memory.");
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

    /**
     *In Window
     ******************************************************
     *Returns true if the two specified data points are
     *within rR of each other.
     ******************************************************
     *Pre:
     *      - mode1 and mode2 are indeces into msRawData
     *        specifying the modes of the pixels having
     *        these indeces.
     *Post:
     *      - true is returned if mode1 and mode2 are wi-
     *        thin rR of one another, false is returned
     *        otherwise.
     */
    boolean InWindow(int mode1, int mode2)
    {
        int k = 1, s = 0, p;
        double diff = 0, el;
        while ((diff < 0.25) && (k != kp)) // Partial Distortion Search
        {
            //Calculate distance squared of sub-space s
            diff = 0;
            for (p = 0; p < P[k]; p++)
            {
                el = (modes[mode1 * N + p + s] - modes[mode2 * N + p + s]) / (h[k] * offset[k]);
                if ((0 == p) && (k == 1) && (modes[mode1 * N] > 80))
                {
                    diff += 4 * el * el;
                } else
                {
                    diff += el * el;
                }
            }

            //next subspace
            s += P[k];
            k++;
        }
        return (diff < 0.25);
    }

    /**
     *Square Distance
     ******************************************************
     *Computs the normalized square distance between two
     *modes.
     ******************************************************
     *Pre:
     *      - mode1 and mode2 are indeces into the modes
     *        array specifying two modes of the image
     *Post:
     *      - the normalized square distance between modes
     *        indexed by mode1 and mode2 has been calc-
     *        ulated and the result has been returned.
     */
    float SqDistance(int mode1, int mode2)
    {

        int k = 1, s = 0, p;
        float dist = 0, el;
        for (k = 1; k < kp; k++)
        {
            //Calculate distance squared of sub-space s
            for (p = 0; p < P[k]; p++)
            {
                el = (modes[mode1 * N + p + s] - modes[mode2 * N + p + s]) / (h[k] * offset[k]);
                dist += el * el;
            }

            //next subspace
            s += P[k];
            k++;
        }

        //return normalized square distance between modes
        //1 and 2
        return dist;

    }

    /**
     *Initialize Output
     ******************************************************
     *Allocates memory needed by the mean shift image pro-
     *cessor class output storage data structure.
     ******************************************************
     *Post:
     *      - the memory needed by the output storage
     *        structure of this class has been (re-)allo-
     *        cated.
     */
    void InitializeOutput()
    {

        //De-allocate memory if output was defined for previous image
        DestroyOutput();

        //Allocate memory for msRawData (filtered image output)
        if (null == (msRawData = new float[L * N]))
        {
            ErrorHandler("msImageProcessor", "Allocate", "Not enough memory.");
            return;
        }

        //Allocate memory used to store image modes and their corresponding regions...
        if ((null == (modes = new float[L * (N + 2)])) || (null == (labels = new int[L])) || (null == (modePointCounts = new int[L])) || (null == (indexTable = new int[L])))
        {
            ErrorHandler("msImageProcessor", "Allocate", "Not enough memory");
            return;
        }

        //Allocate memory for integer modes used to perform connected components
        //(image labeling)...
//	if(null==(LUV_data = new	int [N*L]))
        if (null == (LUV_data = new float[N * L]))
        {
            ErrorHandler("msImageProcessor", "Allocate", "Not enough memory");
            return;
        }

        //indicate that the class output storage structure has been defined
        class_state.OUTPUT_DEFINED = true;

    }

    /**
     *Destroy Output
     ******************************************************
     *De-allocates memory needed by the mean shift image
     *processor class output storage data structure.
     ******************************************************
     *Post:
     *      - the memory needed by the output storage
     *        structure of this class has been de-alloc-
     *        ated.
     *      - the output storage structure has been init-
     *        ialized for re-use.
     */
    void DestroyOutput()
    {

        //de-allocate memory for msRawData (filtered image output)
//	if (msRawData)			//delete [] msRawData;

        //de-allocate memory used by output storage and image
        //classification structure
//	if (modes)				//delete [] modes;
//	if (labels)				//delete [] labels;
//	if (modePointCounts)	//delete [] modePointCounts;
//	if (indexTable)			//delete [] indexTable;

        //de-allocate memory for LUV_data
//	if (LUV_data)			//delete [] LUV_data;

        //initialize data members for re-use...

        //initialize output structures...
        msRawData = null;

        //re-initialize classification structure
        modes = null;
        labels = null;
        modePointCounts = null;
        regionCount = 0;

        //indicate that the output has been destroyed
        class_state.OUTPUT_DEFINED = false;

        //done.
        return;

    }

// NEW
    void NewOptimizedFilter1(float sigmaS, float sigmaR)
    {
        // Declare Variables
        int iterationCount, i, j, k, modeCandidateX, modeCandidateY, modeCandidate_i;
        double mvAbs, diff, el;

        //make sure that a lattice height and width have
        //been defined...
        if (0 == height)
        {
            ErrorHandler("msImageProcessor", "LFilter", "Lattice height and width are undefined.");
            return;
        }

        //re-assign bandwidths to sigmaS and sigmaR
        if (((h[0] = sigmaS) <= 0) || ((h[1] = sigmaR) <= 0))
        {
            ErrorHandler("msImageProcessor", "Segment", "sigmaS and/or sigmaR is zero or negative.");
            return;
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
        float[] sdata;
        sdata = new float[lN * L];

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
                sdata[idxs++] = (i / width) / sigmaS;
                for (j = 0; j < N; j++)
                {
                    sdata[idxs++] = data[idxd++] / sigmaR;
                }
            }
        }
        // index the data in the 3d buckets (x, y, L)
        int buckets[];
        int slist[];
        slist = new int[L];
        int bucNeigh[] = new int[27];

        float sMins; // just for L
        float sMaxs[] = new float[3]; // for all
        sMaxs[0] = width / sigmaS;
        sMaxs[1] = height / sigmaS;
        sMins = sMaxs[2] = sdata[2];
        idxs = 2;
        float cval;
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
        double wsuml, weight;
        double hiLTr = 80.0 / sigmaR;
        // done indexing/hashing


        // Initialize mode table used for basin of attraction
        //memset(modeTable, 0, width*height);

        for (int ii = 0; ii < width * height; ii++)
        {
            modeTable[ii] = 0;

        }

        // proceed ...

        msSys.Prompt("done.\nApplying mean shift (Using Lattice) ... ");

        msSys.Prompt("\n 0%%");




        for (i = 0; i < L; i++)
        {
            // if a mode was already assigned to this data point
            // then skip this point, otherwise proceed to
            // find its mode by applying mean shift...
            if (modeTable[i] == 1)
            {
                continue;
            }

            // initialize point list...
            pointCount = 0;

            // Assign window center (window centers are
            // initialized by createLattice to be the point
            // data[i])
            idxs = i * lN;
            for (j = 0; j < lN; j++)
            {
                yk[j] = sdata[idxs + j];
            }

            // Calculate the mean shift vector using the lattice
            // LatticeMSVector(Mh, yk); // modify to new
            /*****************************************************/
            // Initialize mean shift vector
            for (j = 0; j < lN; j++)
            {
                Mh[j] = 0;
            }
            wsuml = 0;
            // kernelType.UniformLSearch(Mh, yk_ptr); // modify to new
            // find bucket of yk
            cBuck1 = (int) yk[0] + 1;
            cBuck2 = (int) yk[1] + 1;
            cBuck3 = (int) (yk[2] - sMins) + 1;
            cBuck = cBuck1 + nBuck1 * (cBuck2 + nBuck2 * cBuck3);
            for (j = 0; j < 27; j++)
            {
                idxd = buckets[cBuck + bucNeigh[j]];
                // list parse, crt point is cHeadList
                while (idxd >= 0)
                {
                    idxs = lN * idxd;
                    // determine if inside search window
                    el = sdata[idxs + 0] - yk[0];
                    diff = el * el;
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
                            weight = 1 - weightMap[idxd];
                            for (k = 0; k < lN; k++)
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
                for (j = 0; j < lN; j++)
                {
                    Mh[j] = Mh[j] / wsuml - yk[j];
                }
            } else
            {
                for (j = 0; j < lN; j++)
                {
                    Mh[j] = 0;
                }
            }
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


            // Keep shifting window center until the magnitude squared of the
            // mean shift vector calculated at the window center location is
            // under a specified threshold (Epsilon)

            // NOTE: iteration count is for speed up purposes only - it
            //       does not have any theoretical importance
            iterationCount = 1;
            while ((mvAbs >= EPSILON) && (iterationCount < LIMIT))
            {

                // Shift window location
                for (j = 0; j < lN; j++)
                {
                    yk[j] += Mh[j];
                }

                // check to see if the current mode location is in the
                // basin of attraction...

                // calculate the location of yk on the lattice
                modeCandidateX = (int) (sigmaS * yk[0] + 0.5);
                modeCandidateY = (int) (sigmaS * yk[1] + 0.5);
                modeCandidate_i = modeCandidateY * width + modeCandidateX;

                // if mvAbs != 0 (yk did indeed move) then check
                // location basin_i in the mode table to see if
                // this data point either:

                // (1) has not been associated with a mode yet
                //     (modeTable[basin_i] = 0), so associate
                //     it with this one
                //
                // (2) it has been associated with a mode other
                //     than the one that this data point is converging
                //     to (modeTable[basin_i] = 1), so assign to
                //     this data point the same mode as that of basin_i

                if ((modeTable[modeCandidate_i] != 2) && (modeCandidate_i != i))
                {
                    // obtain the data point at basin_i to
                    // see if it is within h*TC_DIST_FACTOR of
                    // of yk
                    diff = 0;
                    idxs = lN * modeCandidate_i;
                    for (k = 2; k < lN; k++)
                    {
                        el = sdata[idxs + k] - yk[k];
                        diff += el * el;
                    }

                    // if the data point at basin_i is within
                    // a distance of h*TC_DIST_FACTOR of yk
                    // then depending on modeTable[basin_i] perform
                    // either (1) or (2)
                    if (diff < TC_DIST_FACTOR)
                    {
                        // if the data point at basin_i has not
                        // been associated to a mode then associate
                        // it with the mode that this one will converge
                        // to
                        if (modeTable[modeCandidate_i] == 0)
                        {
                            // no mode associated yet so associate
                            // it with this one...
                            pointList[pointCount++] = modeCandidate_i;
                            modeTable[modeCandidate_i] = 2;

                        } else
                        {

                            // the mode has already been associated with
                            // another mode, thererfore associate this one
                            // mode and the modes in the point list with
                            // the mode associated with data[basin_i]...

                            // store the mode info into yk using msRawData...
                            for (j = 0; j < N; j++)
                            {
                                yk[j + 2] = msRawData[modeCandidate_i * N + j] / sigmaR;
                            }

                            // update mode table for this data point
                            // indicating that a mode has been associated
                            // with it
                            modeTable[i] = 1;

                            // indicate that a mode has been associated
                            // to this data point (data[i])
                            mvAbs = -1;

                            // stop mean shift calculation...
                            break;
                        }
                    }
                }

                // Calculate the mean shift vector at the new
                // window location using lattice
                // Calculate the mean shift vector using the lattice
                // LatticeMSVector(Mh, yk); // modify to new
                /*****************************************************/
                // Initialize mean shift vector
                for (j = 0; j < lN; j++)
                {
                    Mh[j] = 0;
                }
                wsuml = 0;
                // kernelType.UniformLSearch(Mh, yk_ptr); // modify to new
                // find bucket of yk
                cBuck1 = (int) yk[0] + 1;
                cBuck2 = (int) yk[1] + 1;
                cBuck3 = (int) (yk[2] - sMins) + 1;
                cBuck = cBuck1 + nBuck1 * (cBuck2 + nBuck2 * cBuck3);
                for (j = 0; j < 27; j++)
                {
                    idxd = buckets[cBuck + bucNeigh[j]];
                    // list parse, crt point is cHeadList
                    while (idxd >= 0)
                    {
                        idxs = lN * idxd;
                        // determine if inside search window
                        el = sdata[idxs + 0] - yk[0];
                        diff = el * el;
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
                                weight = 1 - weightMap[idxd];
                                for (k = 0; k < lN; k++)
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
                    for (j = 0; j < lN; j++)
                    {
                        Mh[j] = Mh[j] / wsuml - yk[j];
                    }
                } else
                {
                    for (j = 0; j < lN; j++)
                    {
                        Mh[j] = 0;
                    }
                }
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

                // Increment iteration count
                iterationCount++;

            }

            // if a mode was not associated with this data point
            // yet associate it with yk...
            if (mvAbs >= 0)
            {
                // Shift window location
                for (j = 0; j < lN; j++)
                {
                    yk[j] += Mh[j];
                }

                // update mode table for this data point
                // indicating that a mode has been associated
                // with it
                modeTable[i] = 1;

            }

            for (k = 0; k < N; k++)
            {
                yk[k + 2] *= sigmaR;
            }

            // associate the data point indexed by
            // the point list with the mode stored
            // by yk
            for (j = 0; j < pointCount; j++)
            {
                // obtain the point location from the
                // point list
                modeCandidate_i = pointList[j];

                // update the mode table for this point
                modeTable[modeCandidate_i] = 1;

                //store result into msRawData...
                for (k = 0; k < N; k++)
                {
                    msRawData[N * modeCandidate_i + k] = (float) (yk[k + 2]);
                }
            }

            //store result into msRawData...
            for (j = 0; j < N; j++)
            {
                msRawData[N * i + j] = (float) (yk[j + 2]);
            }

            // Prompt user on progress

            percent_complete = (float) (i / (float) (L)) * 100;
            msSys.Prompt("\r%2d%%", (int) (percent_complete + 0.5));


            // Check to see if the algorithm has been halted
            if ((i % PROGRESS_RATE == 0) && ((ErrorStatus = msSys.Progress((float) (i / (float) (L)) * (float) (0.8)))) == ErrorLevel.EL_HALT)
            {
                break;
            }
        }

        // Prompt user that filtering is completed


        msSys.Prompt("\r");

        msSys.Prompt("done.");

        // de-allocate memory
        //delete [] buckets;
        //delete [] slist;
        //delete [] sdata;

        //delete [] yk;
        //delete [] Mh;

        // done.
        return;

    }

// NEW
    void NewOptimizedFilter2(float sigmaS, float sigmaR)
    {
        // Declare Variables
        int iterationCount, i, j, k, modeCandidateX, modeCandidateY, modeCandidate_i;
        double mvAbs, diff, el;

        //make sure that a lattice height and width have
        //been defined...
        if (0 == height)
        {
            ErrorHandler("msImageProcessor", "LFilter", "Lattice height and width are undefined.");
            return;
        }

        //re-assign bandwidths to sigmaS and sigmaR
        if (((h[0] = sigmaS) <= 0) || ((h[1] = sigmaR) <= 0))
        {
            ErrorHandler("msImageProcessor", "Segment", "sigmaS and/or sigmaR is zero or negative.");
            return;
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
        float[] sdata;
        sdata = new float[lN * L];

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
                sdata[idxs++] = (i / width) / sigmaS;
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

        float sMins; // just for L
        float sMaxs[] = new float[3]; // for all
        sMaxs[0] = width / sigmaS;
        sMaxs[1] = height / sigmaS;
        sMins = sMaxs[2] = sdata[2];
        idxs = 2;
        float cval;
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
        double wsuml, weight;
        double hiLTr = 80.0 / sigmaR;
        // done indexing/hashing


        // Initialize mode table used for basin of attraction
        //memset(modeTable, 0, width*height);

        for (int ii = 0; ii < width * height; ii++)
        {
            modeTable[ii] = 0;

        }

        // proceed ...

        msSys.Prompt("done.\nApplying mean shift (Using Lattice) ... ");

        msSys.Prompt("\n 0%%");




        for (i = 0; i < L; i++)
        {
            // if a mode was already assigned to this data point
            // then skip this point, otherwise proceed to
            // find its mode by applying mean shift...
            if (modeTable[i] == 1)
            {
                continue;
            }

            // initialize point list...
            pointCount = 0;

            // Assign window center (window centers are
            // initialized by createLattice to be the point
            // data[i])
            idxs = i * lN;
            for (j = 0; j < lN; j++)
            {
                yk[j] = sdata[idxs + j];
            }

            // Calculate the mean shift vector using the lattice
            // LatticeMSVector(Mh, yk); // modify to new
            /*****************************************************/
            // Initialize mean shift vector
            for (j = 0; j < lN; j++)
            {
                Mh[j] = 0;
            }
            wsuml = 0;
            // kernelType.UniformLSearch(Mh, yk_ptr); // modify to new
            // find bucket of yk
            cBuck1 = (int) yk[0] + 1;
            cBuck2 = (int) yk[1] + 1;
            cBuck3 = (int) (yk[2] - sMins) + 1;
            cBuck = cBuck1 + nBuck1 * (cBuck2 + nBuck2 * cBuck3);
            for (j = 0; j < 27; j++)
            {
                idxd = buckets[cBuck + bucNeigh[j]];
                // list parse, crt point is cHeadList
                while (idxd >= 0)
                {
                    idxs = lN * idxd;
                    // determine if inside search window
                    el = sdata[idxs + 0] - yk[0];
                    diff = el * el;
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
                            weight = 1 - weightMap[idxd];
                            for (k = 0; k < lN; k++)
                            {
                                Mh[k] += weight * sdata[idxs + k];
                            }
                            wsuml += weight;

                            //set basin of attraction mode table
                            if (diff < speedThreshold)
                            {
                                if (modeTable[idxd] == 0)
                                {
                                    pointList[pointCount++] = idxd;
                                    modeTable[idxd] = 2;
                                }
                            }
                        }
                    }
                    idxd = slist[idxd];
                }
            }
            if (wsuml > 0)
            {
                for (j = 0; j < lN; j++)
                {
                    Mh[j] = Mh[j] / wsuml - yk[j];
                }
            } else
            {
                for (j = 0; j < lN; j++)
                {
                    Mh[j] = 0;
                }
            }
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


            // Keep shifting window center until the magnitude squared of the
            // mean shift vector calculated at the window center location is
            // under a specified threshold (Epsilon)

            // NOTE: iteration count is for speed up purposes only - it
            //       does not have any theoretical importance
            iterationCount = 1;
            while ((mvAbs >= EPSILON) && (iterationCount < LIMIT))
            {

                // Shift window location
                for (j = 0; j < lN; j++)
                {
                    yk[j] += Mh[j];
                }

                // check to see if the current mode location is in the
                // basin of attraction...

                // calculate the location of yk on the lattice
                modeCandidateX = (int) (sigmaS * yk[0] + 0.5);
                modeCandidateY = (int) (sigmaS * yk[1] + 0.5);
                modeCandidate_i = modeCandidateY * width + modeCandidateX;

                // if mvAbs != 0 (yk did indeed move) then check
                // location basin_i in the mode table to see if
                // this data point either:

                // (1) has not been associated with a mode yet
                //     (modeTable[basin_i] = 0), so associate
                //     it with this one
                //
                // (2) it has been associated with a mode other
                //     than the one that this data point is converging
                //     to (modeTable[basin_i] = 1), so assign to
                //     this data point the same mode as that of basin_i

                if ((modeTable[modeCandidate_i] != 2) && (modeCandidate_i != i))
                {
                    // obtain the data point at basin_i to
                    // see if it is within h*TC_DIST_FACTOR of
                    // of yk
                    diff = 0;
                    idxs = lN * modeCandidate_i;
                    for (k = 2; k < lN; k++)
                    {
                        el = sdata[idxs + k] - yk[k];
                        diff += el * el;
                    }

                    // if the data point at basin_i is within
                    // a distance of h*TC_DIST_FACTOR of yk
                    // then depending on modeTable[basin_i] perform
                    // either (1) or (2)
                    if (diff < speedThreshold)
                    {
                        // if the data point at basin_i has not
                        // been associated to a mode then associate
                        // it with the mode that this one will converge
                        // to
                        if (modeTable[modeCandidate_i] == 0)
                        {
                            // no mode associated yet so associate
                            // it with this one...
                            pointList[pointCount++] = modeCandidate_i;
                            modeTable[modeCandidate_i] = 2;

                        } else
                        {

                            // the mode has already been associated with
                            // another mode, thererfore associate this one
                            // mode and the modes in the point list with
                            // the mode associated with data[basin_i]...

                            // store the mode info into yk using msRawData...
                            for (j = 0; j < N; j++)
                            {
                                yk[j + 2] = msRawData[modeCandidate_i * N + j] / sigmaR;
                            }

                            // update mode table for this data point
                            // indicating that a mode has been associated
                            // with it
                            modeTable[i] = 1;

                            // indicate that a mode has been associated
                            // to this data point (data[i])
                            mvAbs = -1;

                            // stop mean shift calculation...
                            break;
                        }
                    }
                }

                // Calculate the mean shift vector at the new
                // window location using lattice
                // Calculate the mean shift vector using the lattice
                // LatticeMSVector(Mh, yk); // modify to new
                /*****************************************************/
                // Initialize mean shift vector
                for (j = 0; j < lN; j++)
                {
                    Mh[j] = 0;
                }
                wsuml = 0;
                // kernelType.UniformLSearch(Mh, yk_ptr); // modify to new
                // find bucket of yk
                cBuck1 = (int) yk[0] + 1;
                cBuck2 = (int) yk[1] + 1;
                cBuck3 = (int) (yk[2] - sMins) + 1;
                cBuck = cBuck1 + nBuck1 * (cBuck2 + nBuck2 * cBuck3);
                for (j = 0; j < 27; j++)
                {
                    idxd = buckets[cBuck + bucNeigh[j]];
                    // list parse, crt point is cHeadList
                    while (idxd >= 0)
                    {
                        idxs = lN * idxd;
                        // determine if inside search window
                        el = sdata[idxs + 0] - yk[0];
                        diff = el * el;
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
                                weight = 1 - weightMap[idxd];
                                for (k = 0; k < lN; k++)
                                {
                                    Mh[k] += weight * sdata[idxs + k];
                                }
                                wsuml += weight;

                                //set basin of attraction mode table
                                if (diff < speedThreshold)
                                {
                                    if (modeTable[idxd] == 0)
                                    {
                                        pointList[pointCount++] = idxd;
                                        modeTable[idxd] = 2;
                                    }
                                }

                            }
                        }
                        idxd = slist[idxd];
                    }
                }
                if (wsuml > 0)
                {
                    for (j = 0; j < lN; j++)
                    {
                        Mh[j] = Mh[j] / wsuml - yk[j];
                    }
                } else
                {
                    for (j = 0; j < lN; j++)
                    {
                        Mh[j] = 0;
                    }
                }
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

                // Increment iteration count
                iterationCount++;

            }

            // if a mode was not associated with this data point
            // yet associate it with yk...
            if (mvAbs >= 0)
            {
                // Shift window location
                for (j = 0; j < lN; j++)
                {
                    yk[j] += Mh[j];
                }

                // update mode table for this data point
                // indicating that a mode has been associated
                // with it
                modeTable[i] = 1;

            }

            for (k = 0; k < N; k++)
            {
                yk[k + 2] *= sigmaR;
            }

            // associate the data point indexed by
            // the point list with the mode stored
            // by yk
            for (j = 0; j < pointCount; j++)
            {
                // obtain the point location from the
                // point list
                modeCandidate_i = pointList[j];

                // update the mode table for this point
                modeTable[modeCandidate_i] = 1;

                //store result into msRawData...
                for (k = 0; k < N; k++)
                {
                    msRawData[N * modeCandidate_i + k] = (float) (yk[k + 2]);
                }
            }

            //store result into msRawData...
            for (j = 0; j < N; j++)
            {
                msRawData[N * i + j] = (float) (yk[j + 2]);
            }

            // Prompt user on progress

            percent_complete = (float) (i / (float) (L)) * 100;
            msSys.Prompt("\r%2d%%", (int) (percent_complete + 0.5));


            // Check to see if the algorithm has been halted
            if ((i % PROGRESS_RATE == 0) && ((ErrorStatus = msSys.Progress((float) (i / (float) (L)) * (float) (0.8)))) == ErrorLevel.EL_HALT)
            {
                break;
            }
        }

        // Prompt user that filtering is completed


        msSys.Prompt("\r");

        msSys.Prompt("done.");

        // de-allocate memory
        //delete [] buckets;
        //delete [] slist;
        //delete [] sdata;

        //delete [] yk;
        //delete [] Mh;

        // done.
        return;

    }

    /**
     *
     */
    void NewNonOptimizedFilter(float sigmaS, float sigmaR)
    {

        // Declare Variables
        int iterationCount, i, j, k;
        double mvAbs, diff, el;

        //make sure that a lattice height and width have
        //been defined...
        if (0 == height)
        {
            ErrorHandler("msImageProcessor", "LFilter", "Lattice height and width are undefined.");
            return;
        }

        //re-assign bandwidths to sigmaS and sigmaR
        if (((h[0] = sigmaS) <= 0) || ((h[1] = sigmaR) <= 0))
        {
            ErrorHandler("msImageProcessor", "Segment", "sigmaS and/or sigmaR is zero or negative.");
            return;
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

        msSys.Prompt("done.\nApplying mean shift (Using Lattice)... ");

        msSys.Prompt("\n 0%%");

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

            //store result into msRawData...
            for (j = 0; j < N; j++)
            {
                msRawData[N * i + j] = (float) (yk[j + 2] * sigmaR);
            }

            // Prompt user on progress

            percent_complete = (float) (i / (float) (L)) * 100;
            msSys.Prompt("\r%2d%%", (int) (percent_complete + 0.5));


            // Check to see if the algorithm has been halted
            if ((i % PROGRESS_RATE == 0) && ((ErrorStatus = msSys.Progress((float) (i / (float) (L)) * (float) (0.8)))) == ErrorLevel.EL_HALT)
            {
                break;
            }
        }

        // Prompt user that filtering is completed


        msSys.Prompt("\r");

        msSys.Prompt("done.");


        // de-allocate memory
        //delete [] buckets;
        //delete [] slist;
        //delete [] sdata;

        //delete [] yk;
        //delete [] Mh;

        // done.
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
            
            System.out.println("ii");
            
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
                    double weight = 1 - weightMap[idxd];
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
                            double weight = 1 - weightMap[idxd];
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

    
    public void SetSpeedThreshold(float speedUpThreshold)
    {
        speedThreshold = speedUpThreshold;
    }

    private double calcmvAbs(double[] Mh, float sigmaS, double mvAbs, float sigmaR)
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
}
