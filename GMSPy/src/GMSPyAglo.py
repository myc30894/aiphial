
__author__="Nickl"
__date__ ="$13.01.2009 13:27:01$"


from tools import image_to_luvarray
from ru.nickl.meanShift.direct import LUVConverter,LUV
#from ru.nickl.meanShift.direct.filter import *
from ru.nickl.meanshift.general.dataStore import DataStore, SimpleNDimPoint,MultiDimMapDataStore
from ru.nickl.meanshift.general.basic import MeanShiftClusterer, MeanShiftClusterer1, FastMeanShiftClusterer, SimpleBandwidthSelector

from ru.nickl.meanshift.general.aglomerative import AglomerativeMeanShift

from LuvDataStore import LuvDataStorePy

from ru.nickl.meanshift.general.imaging import *

import time

from tools import *


#srcimg = readImage(r"../images/DSC00104s400.bmp")
srcimg = readImage(r"../images/DSCN4909s100.bmp")
#srcimg = readImage(r"../images/DSC00272s800.JPG")
h = srcimg.getHeight();
w = srcimg.getWidth();
#ds = dataStoreFromImage(srcimg)
ds = LuvDataStore(image_to_luvarray(srcimg))
del srcimg

#ds.window=[15,15,12,12,12]
#ds.window=[3,3,3,3,3]
window = [ 0.3*x for x in SimpleBandwidthSelector().getBandwidth(ds)];
#ds.window= window

for i in range(len(window)):
    print window[i]

print "\n"

msc = AglomerativeMeanShift(FastMeanShiftClusterer(minDistance = 3, speedUpFactor=3))
#msc = AglomerativeMeanShift(MeanShiftClusterer(minDistance = 3))
msc.setDataStore(ds)
msc.window = window
msc.maxIterations = 1000
msc.windowMultiplier = 0.5
msc.autostopping = False
#msc.setMinDistance(10)

class IterationPrinter(AglomerativeMeanShift.IterationListener):
    def __init__(self):
        self._i=0
        self._lt=time.time()

    def IterationDone(self,x):
        print len(x),"%3.3f" % (time.time()-self._lt),
        self._lt=time.time()
        paintclusters(w,h,x,"out"+str(self._i)+".bmp")
        self._i=self._i+1

class WindowMultiplierCorrector(AglomerativeMeanShift.IterationListener):
    def __init__(self,gen):
        # @type gen xrange
        self._i=0
        self._gen = gen

    def IterationDone(self,x):
        n = self._gen.next()
        print n
        msc.windowMultiplier = n


msc.addIterationListener(IterationPrinter())

def gen():
    i = 0.2
    while(True):
        yield i
        i=i+0.01

msc.addIterationListener(WindowMultiplierCorrector(gen()))


msc.doClustering()

paintclusters(w,h,msc.getClusters(),"out.bmp")

