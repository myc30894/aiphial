import time

from ru.nickl.meanShift.direct import *
from ru.nickl.meanShift.direct.filter import *
from tools import *

from ru.nickl.meanShift.direct.segmentator import *

from java.awt import Graphics2D,Image,Toolkit
from java.awt.image import AreaAveragingScaleFilter,BufferedImage,FilteredImageSource,ReplicateScaleFilter
from javax.imageio import ImageIO
from java.io import File
#import java.io.File;
#import java.io.IOException;




def scale(imgname, k):

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

imgname = "DSC00104s400";

counts = 5;
for k in frange(0.1,1,0.01):

    #str = ("../ImageScaler/out/DSC00104s400x%.2f.bmp" % k).replace(".", ",", 3).replace(",,", "..", 1)
    img=scale(imgname,0.5)

    times = []
    for i in range(counts):
        startime = time.time();

#        msp = RegionGrowingSegmentator();
        msp = SimpleSegmentator(
            FastMSFilter(
                colorRange = 14,
                squareRange =int(8*k*2),
            )
        )
        msp.sourceImage = img
        msp.process()
        times.append(time.time()-startime)

    elapsed = (sum(times)-max(times))/(counts-1)

    writeImage(msp.processedImage, "scale/out%f.bmp" % k)
    print ("%f\t%d" % (k, (elapsed)*1000)).replace(".", ",")


print "done"
