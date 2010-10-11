
__author__="Nickl"
__date__ ="$13.01.2009 13:27:01$"

from ru.nickl.meanShift.direct import LUVConverter,LUV
#from ru.nickl.meanShift.direct.filter import *
from ru.nickl.meanshift.general.dataStore import DataStore, SimpleNDimPoint,MultiDimMapDataStore
from ru.nickl.meanshift.general.meanShift import MeanShiftClusterer, MeanShiftClusterer1, FastMeanShiftClusterer, SimpleBandwidthSelector

from LuvDataStore import *

from tools import writeImage
from tools import *


class LuvPointPy(SimpleNDimPoint):
    """
    Documentation
    """



srcImage = readImage(r"DSCN4909s100.bmp");





lc = LUVConverter()
luvarray = lc.toLUVDArray(srcImage)

ds = LuvDataStorePy(luvarray)

#ds = MultiDimMapDataStore(5)
#for i,line in enumerate(luvarray):
#    for j,p in enumerate(line):
#        sp = LuvPoint([j,i,p.l,p.u,p.v])
#        sp.x=j
#        sp.y=i
#        ds.add(sp)



ds.window=[ x*0.6 for x in SimpleBandwidthSelector().getBandwidth(ds)];

for i in range(len(ds.window)):
    print ds.window[i]

print "\n"


#ds.window=[15,15,12,12,12]

msc = MeanShiftClusterer()
msc.setDataStore(ds)
msc.setMinDistance(10)

msc.doClustering()


resultarr = [[None] * len(luvarray[0]) for i in range(len(luvarray))]

for cluster in msc.getClusters():
    cl = cluster.getBasinOfAttraction()
    cp = LUV(cl.get(2),cl.get(3),cl.get(4))
    for lp in cluster:        
        resultarr[int(lp.get(1))][int(lp.get(0))] = cp

resultImage = lc.LUVArrayToBufferedImage(resultarr)

writeImage(resultImage, "out.bmp")
print "done"