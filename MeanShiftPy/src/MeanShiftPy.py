from ru.nickl.meanShift.direct import *

import javax.imageio
import java.io

a = filter.SimpleMSFilter()
a.setColorRange(7)
a.setSquareRange(10)

msp = filter.MeanShiftFilterImageProcessor(a)

msp.setSourceImage(javax.imageio.ImageIO.read(java.io.File("/media/disk-1/Nickl/photoes/avas/ava1smallbwbg.bmp")))
msp.process()
javax.imageio.ImageIO.write(msp.getProcessedImage(), "bmp", java.io.File("/home/nickl/out.bmp"))


print "HelloWorld"
