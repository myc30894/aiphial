import sys
#sys.path.append("meanShift.jar")
print sys.path

from ru.nickl.meanShift.direct import *
from ru.nickl.meanShift.direct.filter import *
from tools import *

a = FastMSFilter(colorRange = 7, squareRange = 30)

a.diffColor = 7;
a.diffSquare = 30;

msp = LuvFilterImageProcessor(a)

msp.sourceImage = readImage("DSC00104s400.bmp")


msp.process()

writeImage(msp.processedImage, "out.bmp")


print "done"
