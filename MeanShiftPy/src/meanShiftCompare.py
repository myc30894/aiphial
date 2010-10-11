from ru.nickl.meanShift.direct import *

import javax.imageio
import java.io

a = filter.SimpleMSFilter()
a.setColorRange(7)
a.setSquareRange(10)

msp = filter.MeanShiftFilterImageProcessor(a)

msp.setSourceImage(javax.imageio.ImageIO.read(java.io.File("/home/nickl/NetBeansProjects/ImageProcessing/meanShift/DSC00104s200.bmp")))
msp.process()
javax.imageio.ImageIO.write(msp.getProcessedImage(), "bmp", java.io.File("/home/nickl/out.bmp"))


print "HelloWorld"