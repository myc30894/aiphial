
__author__="Nickl"
__date__ ="$13.01.2009 13:27:01$"


from tools import image_to_luvarray
from ru.nickl.meanShift.direct import LUVConverter,LUV
#from ru.nickl.meanShift.direct.filter import *
from ru.nickl.meanshift.general.dataStore import DataStore, SimpleNDimPoint,MultiDimMapDataStore, DefaultDataStoreFactory,KdTreeDataStoreFactory
from ru.nickl.meanshift.general.basic import MeanShiftClusterer, MeanShiftClustererOld, FastMeanShiftClusterer, SimpleBandwidthSelector

from ru.nickl.meanshift.general.aglomerative import AglomerativeMeanShift,IterationListener ;

from ru.nickl.meanshift.general.imaging import *
from ru.nickl.meanshift.searching import HistogramClusterComparer
import time

from tools import *

starttime = time.time()

import java.lang.Compiler
import com.savarese.spatial.KDTree
java.lang.Compiler.compileClass(com.savarese.spatial.KDTree)
java.lang.Compiler.compileClass(LuvDataStore)

#DefaultDataStoreFactory.setPrototype(KdTreeDataStoreFactory())
#srcimg = readImage(r"../images/Rocks_and_Water_640.jpg")
#imgtosearch = readImage(r"../images/stone.gif")
srcimg = readImage(r"../images/smallgisto.jpg")
imgtosearch = readImage(r"../images/bluehren.png")


#srcimg = readImage(r"../images/DSCN4909s100.bmp")
#srcimg = readImage(r"../images/DSC00272s800.JPG")
#srcimg = readImage(r"../images/DSC00104.bmp")
h = srcimg.getHeight();
w = srcimg.getWidth();

ds = LuvDataStore(image_to_luvarray(srcimg))


window = [ 0.3*x for x in SimpleBandwidthSelector().getBandwidth(ds)];

#msc = AglomerativeMeanShift(FastMeanShiftClusterer(minDistance = 3, speedUpFactor=2))
msc = AglomerativeMeanShift(MeanShiftClusterer(minDistance = 3))
msc.setDataStore(ds)
msc.setAutostopping(False)
msc.window = window
msc.maxIterations = 1000
msc.windowMultiplier = 0.5
#msc.setMinDistance(10)

class IterationPrinter(IterationListener):
    def __init__(self):
        self._i=0
        self._lt=time.time()

    def IterationDone(self,x):
        print len(x),"%3.3f" % (time.time()-self._lt)
        self._lt=time.time()
        paintclusters(w,h,x,"out"+str(self._i)+".bmp")
        self._i=self._i+1

msc.addIterationListener(IterationPrinter())

cc = HistogramClusterComparer()
cc.setPattern(imgtosearch)
#cc.setOriginalImage(srcimg)

vals = []

class ObjectSearcher(IterationListener):
    def __init__(self):
        self._i=0
    def IterationDone(self,x):
        for cluster in x:
           val = cc.compareCluster(cluster)
           vals.append(val)
           if 0<=val<190:
               img = ImgUtls.getClusterImage(srcimg, cluster)
               if not img is None:
                    writeImage(img, "match/match_"+str(self._i)+"_"+str(val)+".png", "png")
           #paintclusters(w,h,[cluster],"match_"+str(self._i)+"_"+str(val)+".bmp")
           self._i=self._i+1

msc.addIterationListener(ObjectSearcher())

class WindowMultiplierCorrector(IterationListener):
    def __init__(self,gen):
        # @type gen xrange
        self._i=0
        self._gen = gen

    def IterationDone(self,x):
        n = self._gen.next()
        print n
        msc.windowMultiplier = n

def gen():
    i = 0.5
    while(True):
        yield i
        i=i+0.01

msc.addIterationListener(WindowMultiplierCorrector(gen()))

print "inittime: %3.3f" % (time.time()-starttime)

msc.doClustering()

print "vals:", sorted(vals)

paintclusters(w,h,msc.getClusters(),"out.bmp")

