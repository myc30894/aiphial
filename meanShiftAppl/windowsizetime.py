import time

from ru.nickl.meanShift.direct import *
from ru.nickl.meanShift.direct.filter import *
from tools import *

def frange(start,end,step):
    result = []
    k=start
    while k<end:
        result.append(k)
        k=k+step    
    return result



counts = 5

img=readImage("DSCN4909s100.bmp")


for k in frange(0.5,5.,0.1):

    times = []
    for i in range(counts):
        startime = time.time();

        msp = LuvFilterImageProcessor(
            SimpleMSFilter(
                colorRange = 7*k,
                squareRange =int(5*k),
            )
        )
        msp.sourceImage = img
        msp.process()
        times.append(time.time()-startime)

    elapsed = (sum(times)-max(times))/(counts-1)

    writeImage(msp.processedImage, "out%f.bmp" % k)
    print "%f\t%d" % (k, (elapsed)*1000)


print "done"
