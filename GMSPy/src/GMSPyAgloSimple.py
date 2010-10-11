
__author__="Nickl"
__date__ ="$13.01.2009 13:27:01$"


from tools import image_to_luvarray
from ru.nickl.meanShift.direct import LUVConverter,LUV
#from ru.nickl.meanShift.direct.filter import *
from ru.nickl.meanshift.general.dataStore import DataStore, SimpleNDimPoint,MultiDimMapDataStore, DefaultDataStoreFactory,KdTreeDataStoreFactory
from ru.nickl.meanshift.general.basic import MeanShiftClusterer, MeanShiftClustererOld, FastMeanShiftClusterer, SimpleBandwidthSelector

from ru.nickl.meanshift.general.aglomerative import AglomerativeMeanShift

from ru.nickl.meanshift.general.imaging import *

import time

from tools import *

starttime = time.time()

import java.lang.Compiler
import com.savarese.spatial.KDTree
java.lang.Compiler.compileClass(com.savarese.spatial.KDTree)
java.lang.Compiler.compileClass(LuvDataStore)

DefaultDataStoreFactory.setPrototype(KdTreeDataStoreFactory())
srcimg = readImage(r"../images/DSC00104s400.bmp")
#srcimg = readImage(r"../images/DSCN4909s100.bmp")
#srcimg = readImage(r"../images/DSC00272s800.JPG")
#srcimg = readImage(r"../images/DSC00104.bmp")
h = srcimg.getHeight();
w = srcimg.getWidth();
#ds = dataStoreFromImage(srcimg,MultiDimMapDataStore(5))
ds = LuvDataStore(image_to_luvarray(srcimg))
del srcimg

#ds.window=[15,15,12,12,12]
#ds.window=[3,3,3,3,3]
window = [ 0.3*x for x in SimpleBandwidthSelector().getBandwidth(ds)];
#ds.window= window

for i in range(len(window)):
    print window[i]

print "\n"

#msc = AglomerativeMeanShift(FastMeanShiftClusterer(minDistance = 3, speedUpFactor=2))
msc = AglomerativeMeanShift(MeanShiftClusterer(minDistance = 3))
msc.setDataStore(ds)
msc.window = window
msc.maxIterations = 1000
msc.windowMultiplier = 0.5
#msc.setMinDistance(10)

class IterationPrinter(AglomerativeMeanShift.IterationListener):
    def __init__(self):
        self._i=0
        self._lt=time.time()

    def IterationDone(self,x):
        print len(x),"%3.3f" % (time.time()-self._lt)
        self._lt=time.time()
        paintclusters(w,h,x,"out"+str(self._i)+".bmp")
        self._i=self._i+1



msc.addIterationListener(IterationPrinter())

print "inittime: %3.3f" % (time.time()-starttime)

msc.doClustering()

paintclusters(w,h,msc.getClusters(),"out.bmp")

