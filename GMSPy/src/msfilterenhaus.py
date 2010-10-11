from ru.nickl.meanShift.direct import *
from tools import *

import javax.imageio
import java.io

a = filter.SimpleMSFilter()

for cr in frange(1,2.5,0.1):
    for sr in range(2,10):
        a.setColorRange(cr)
        a.setSquareRange(10)

        msp = filter.MeanShiftFilterImageProcessor(a)

        msp.setSourceImage(javax.imageio.ImageIO.read(java.io.File("/media/disk-1/Nickl/photoes/avas/ava1smallbwbg.jpg")))
        msp.process()
        javax.imageio.ImageIO.write(msp.getProcessedImage(), "bmp", java.io.File("/home/nickl/out_"+str(cr)+"_"+str(sr)+".bmp"))


print "HelloWorld"