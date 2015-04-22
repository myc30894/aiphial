# Mean-shift filter #
This sample creates a mean-shift filter and processes an image with it. Result image consists of original pixels colored with colors of basins of attractions. ([full source](http://code.google.com/p/aiphial/source/browse/utls/ScalaRunner/src/main/java/scalarunner/NaiveSegmentationSampleJava.java))([Scala version](http://code.google.com/p/aiphial/source/browse/utls/ScalaRunner/src/main/scala/scalarunner/NaiveSegmentationSample.scala))
```
        // read a buffered image from file
        BufferedImage srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"));
        // then create a Clusterer, FastMatrixMS is a simple Mean Shift Clusterer for images
        FastMatrixMS a = new FastMatrixMS(Tools.matrixFromImage(srcimg));

        // setup filter parametrs
        a.setColorRange(7f);
        a.setSquareRange((short)20);

        // process
        a.doClustering();

        // paint clusters on image
        BufferedImage img = Tools.paintClusters(srcimg.getWidth(), srcimg.getHeight(), a.getClusters(), false);

        // write results to file
        ImageIO.write(img, "bmp", new File("./out_.bmp"));
```

# Agglomerative mean-shift segmentation #
This is a sample to create an agglomerative clusterer and use it to segmentate an image ([full source](http://code.google.com/p/aiphial/source/browse/utls/ScalaRunner/src/main/java/scalarunner/AggloSegmentationSampleJava.java))([Scala version](http://code.google.com/p/aiphial/source/browse/utls/ScalaRunner/src/main/scala/scalarunner/AggloSegmentationSample.scala))

```
        // read a buffered image from file
        final BufferedImage srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"));
        // then create a mean-shift clusterer which would be used
        // at each step of agglomareative clustering
        MeanShiftClusterer<NDimPoint> msc0 = new MeanShiftClusterer<NDimPoint>();

        msc0.setMinDistance(3);
        // create an agglomerative clusterer which is based on created mean-shift clusterer
        AglomerativeMeanShift<LuvPoint> amsc = new AglomerativeMeanShift<LuvPoint>(msc0);

        amsc.setAutostopping(false);
        amsc.setMaxIterations(1000);
        amsc.setWindowMultiplier(0.2f);
        // an iteration listener that would increment window multiplier on each step
        // to provide additional agglomeretivity :)
        amsc.setWindowMultiplierStep(0.1f);
        
        // create a datastore and add all points from image to it
        KdTreeDataStore<LuvPoint> datastore = new KdTreeDataStore<LuvPoint>(5);

        for (LuvPoint p : ImgUtls.imageAsLUVPointCollection(srcimg))
        {
            datastore.add(p);
        }

        // set created datasrote as data source for agglomerative clusterer
        amsc.setDataStore(datastore);


        // add an iteration listener that would write to image file
        // results of each step of the agglomerative clustering
        amsc.addIterationListener(new IterationListener<LuvPoint>()
        {            
            int s = 0;
            
            public void IterationDone(Collection<? extends Cluster<LuvPoint>> a)
            {
                try
                {
                    ImageIO.write(
                            Tools.paintClusters(srcimg.getWidth(), srcimg.getHeight(), a, false),
                            "bmp",
                            new File("../out_" + s + ".bmp"));
                    s = s + 1;
                } catch (IOException iOException)
                {
                    throw new RuntimeException(iOException);
                }
            }
        });

        // start the clustering process
        amsc.doClustering();

```

# Complex agglomerative mean-shift segmentation #
This sample uses fast MatrixMS clustering on the first step, so segmentation becomes faster.([full source](http://code.google.com/p/aiphial/source/browse/utls/ScalaRunner/src/main/java/scalarunner/ComplexAggloSegmentationSampleJava.java))
```
        final BufferedImage srcimg = ImageIO.read(new File("../../images/DSCN4909s400.bmp"));
        
        // create a "stack" of clusterers, which allows to use sppecific clusterer on first step
        AglomerativeClustererStack<LuvPoint> msc = new AglomerativeClustererStack<LuvPoint>();

        // create Image Matrix using Matrix API
        Matrix<LUV> srcmt = Tools.matrixFromImage(srcimg);

        // create comparebly fast MatrixMS segmentator to use on the first step
        MatrixMS matMS = new MatrixMS(srcmt);
        matMS.setColorRange(7);
        matMS.setSquareRange((short) 2);
        matMS.setMinRegionSize(0);

        // setup matMS as first step clusterer
        msc.setInitialClusterer(matMS);

        // create clusterers for futher steps
        MeanShiftClusterer<NDimPoint> msc0 = new MeanShiftClusterer<NDimPoint>();
        msc0.setMinDistance(3);
        AglomerativeMeanShift<LuvPoint> amsc = new AglomerativeMeanShift<LuvPoint>(msc0);
        amsc.setAutostopping(false);
        amsc.setMaxIterations(1000);
        amsc.setWindowMultiplier(0.2f);
        amsc.setWindowMultiplierStep(0.1f);

        // add them to stack; 
        msc.addExtendingClustererToQueue(amsc);

        // add an iteration listener that would write to image file
        // results of each step of the agglomerative clustering
        msc.addIterationListener(new IterationListener<LuvPoint>()
        {

            int s = 0;

            public void IterationDone(Collection<? extends Cluster<LuvPoint>> a)
            {
                try
                {
                    ImageIO.write(
                            Tools.paintClusters(srcimg.getWidth(), srcimg.getHeight(), a, false),
                            "bmp",
                            new File("../out_" + s + ".bmp"));
                    s = s + 1;
                } catch (IOException iOException)
                {
                    throw new RuntimeException(iOException);
                }
            }
        });

        // start the clustering process
        msc.doClustering();
```

# Arbitrary data clustering #
Clustering arbitrary spatial data

Sample to be added...

# Arbitrary agglomerative data clustering #
Sample to be added...

# "Matrix" API #
Image processing via "Matrix"-API

Sobel filter: ([full scala source](http://code.google.com/p/aiphial/source/browse/utls/ScalaRunner/src/main/scala/scalarunner/MatrixSobel.scala))
```
    val imagemtx = Matrix(ImgUtls.readImageAsLuvArray("../../images/DSCN4909s400.bmp"))

    val gray = imagemtx.map(p => new LUV(p.l,0.,0.))
    
    ImageIO.write(matrixToImage(gray),"bmp",new File("out.bmp"));

    val xsobel = Matrix(Array(
        Array(-1,0,1),
        Array(-2,0,2),
        Array(-1,0,1)
      ))

    val ysobel = xsobel.rotateClockwise

    val gradient = gray.windowingMap(3,3)(
      m=>
      {
        val s1 = m.join(xsobel)(_.l*_).reduce(_+_)
        val s2 = m.join(ysobel)(_.l*_).reduce(_+_)

        math.sqrt(s1*s1+s2*s2)
      }
    )

    ImageIO.write(matrixToImage(
        gradient.map(p => new LUV(p*2,0.,0.))
      ),"bmp",new File("out_grad.bmp"));

```