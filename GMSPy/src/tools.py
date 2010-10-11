from java.io import File
from javax.imageio.ImageIO import read
from javax.imageio.ImageIO import write
from ru.nickl.meanShift.direct import LUVConverter,LUV
from ru.nickl.meanshift.general.dataStore import MultiDimMapDataStore,SimpleNDimPoint ,DefaultDataStoreFactory
#import sys
#sys.path.append("meanShift.jar")

def writeImage(image, filename, type="bmp"):
    write(image, type, File(filename))
def readImage(filename):
    return read(File(filename))
def image_to_luvarray(srcImage):
    lc = LUVConverter()
    return lc.toLUVDArray(srcImage)

def frange(start,end,step):
    result = []
    k=start
    while k<end:
        result.append(k)
        k=k+step
    return result

def scale(imgname, k):

    from java.awt import Graphics2D,Image,Toolkit
    from java.awt.image import AreaAveragingScaleFilter,BufferedImage,FilteredImageSource,ReplicateScaleFilter
    from javax.imageio import ImageIO
    from java.io import File

    img = ImageIO.read(File(imgname + ".bmp"));

    origh = img.getHeight();
    origw = img.getWidth();

    sf = AreaAveragingScaleFilter(int(origw * k), int(origh * k));

    res = Toolkit.getDefaultToolkit().createImage(FilteredImageSource(img.getSource(), sf));

    w = res.getWidth(None);
    h = res.getHeight(None);
    type = BufferedImage.TYPE_INT_RGB;
    dest = BufferedImage(w, h, type);
    g2 = dest.createGraphics();
    g2.drawImage(res, 0, 0, None);
    g2.dispose();

    return dest

class LuvPointPy(SimpleNDimPoint):
    def __init__(self, parameters):
        SimpleNDimPoint.__init__(self,parameters)
        self.y=parameters[0]
        self.x=parameters[1]



     
def dataStoreFromImage(srcImage, ds = None):
    """Fils datastore ds with data from given BUfferedImage"""

    if(ds==None):
        #ds = MultiDimMapDataStore(5)
        ds = DefaultDataStoreFactory.get().createDataStore(5)


    lc = LUVConverter()
    luvarray = lc.toLUVDArray(srcImage)

    for i,line in enumerate(luvarray):
        for j,p in enumerate(line):
            sp = LuvPointPy([i,j,p.l,p.u,p.v])
            #sp.x=j
            #sp.y=i
            ds.add(sp)

    ds.optimize()

    return ds


def paintclusters(width,height, clusters,imagename="out.bmp"):

    array = [[None] * width for i in range(height)]

    for cluster in clusters:
        cl = cluster.getBasinOfAttraction()
        cp = LUV(cl.getCoord(2),cl.getCoord(3),cl.getCoord(4))
        for lp in cluster:
            array[lp.y][lp.x] = cp


    resultImage = LUVConverter().LUVArrayToBufferedImage(array)


    writeImage(resultImage, imagename)

