
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

def teestSpeed(genClusters):
    """Documentation"""

    imgname = (r"DSC00104s200")
    counts = 3;

    for k in frange(0.1,0.3,0.1):
        srcimg=scale(imgname,k)
        h = srcimg.getHeight();
        w = srcimg.getWidth();
        del srcimg

        times = []
        for i in range(counts):
            startime = time.time();

            clusters = genClusters(srcimg)

            times.append(time.time()-startime)

        elapsed = (sum(times)-max(times))/(counts-1)
        print ("%f %d" % (k*k, (elapsed)*1000)).replace(".", ",")

        paintclusters(w,h,clusters,"out/out%f.bmp" % k)

    print "done"

def genClustersAglomerative(srcimg):
    ds = dataStoreFromImage(srcimg)

    #ds.window=[15,15,12,12,12]
    #ds.window=[3,3,3,3,3]
    #ds.window=[ 0.3*x for x in SimpleBandwidthSelector().getBandwidth(ds)];

    msc = AglomerativeMeanShift(FastMeanShiftClusterer(minDistance = 3))
    msc.window = [ 0.7*x for x in SimpleBandwidthSelector().getBandwidth(ds)];
    msc.setDataStore(ds)
    msc.windowMultiplier = 0.5

    msc.doClustering()

    return msc.getClusters()

def genClustersFast(srcimg):
    ds = dataStoreFromImage(srcimg)

    #ds.window=[15,15,12,12,12]
    #ds.window=[3,3,3,3,3]
    #ds.window=[ 0.7*x for x in SimpleBandwidthSelector().getBandwidth(ds)];

    msc = FastMeanShiftClusterer(minDistance = 3)
    msc.window = [ 0.7*x for x in SimpleBandwidthSelector().getBandwidth(ds)];
    msc.setDataStore(ds)

    msc.doClustering()

    return msc.getClusters()


DefaultDataStoreFactory.setPrototype(KdTreeDataStoreFactory())
imgname = (r"DSC00104s400")
counts = 3;

for k in frange(0.1,1.,0.1):
    srcimg=scale(imgname,k)
    h = srcimg.getHeight();
    w = srcimg.getWidth();    

    #ds = dataStoreFromImage(srcimg)
    ds = LuvDataStore(image_to_luvarray(srcimg))
    del srcimg
    #ds.window=[15,15,12,12,12]
    #ds.window=[3,3,3,3,3]
    window=[ 0.3*x for x in SimpleBandwidthSelector().getBandwidth(ds)];

    times = []
    for i in range(counts):
        startime = time.time();

        msc = AglomerativeMeanShift(MeanShiftClusterer(minDistance = 3))
        msc.window = window
        msc.setDataStore(ds)
        msc.windowMultiplier = 0.5

        msc.doClustering()       
        clusters =msc.getClusters();
        times.append(time.time()-startime)

    elapsed = (sum(times)-max(times))/(counts-1)
    print ("%f %d" % (k*k, (elapsed)*1000)).replace(".", ",")

    del ds

    paintclusters(w,h,clusters,"out/out%f.bmp" % k)

print "done"


