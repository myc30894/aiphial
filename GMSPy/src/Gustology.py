
__author__="Nickl"
__date__ ="$13.01.2009 13:27:01$"


from ru.nickl.meanShift.direct import LUVConverter,LUV, LuvData
#from ru.nickl.meanShift.direct.filter import *
from ru.nickl.meanshift.general.dataStore import DataStore, SimpleNDimPoint
from ru.nickl.meanshift.general.meanShift import MeanShiftClusterer, MeanShiftClusterer1, FastMeanShiftClusterer




from tools import writeImage
from tools import *


class LuvPointPy(SimpleNDimPoint):
    """
    Documentation
    """



#srcImage = readImage(r"/home/nickl/biotecnical/Gistology/Emphisclr2_200.bmp");
srcImage = readImage(r"Emphisclrr.bmp");

lc = LUVConverter()
luvarray = lc.toLUVDArray(srcImage)

ds = DataStore(3)
for i,line in enumerate(luvarray):
    for j,p in enumerate(line):
        sp = LuvPointPy([p.l,p.u,p.v])
        sp.x=j
        sp.y=i
        ds.add(sp)

ds.window=[4,4,4]
print "data added, clustering...\n"
msc = FastMeanShiftClusterer()
msc.setDataStore(ds)
msc.setMinDistance(30)

msc.doClustering()


resultarr = [[None] * len(luvarray[0]) for i in range(len(luvarray))]


print "clustersnumber=", len(msc.getClusters()),"\n"

for cluster in msc.getClusters():
    cl = cluster.get(0)
    cp = LUV(cl.get(0),cl.get(1),cl.get(2))
    for lp in cluster:
        resultarr[lp.y][lp.x] = cp


resultImage = lc.LUVArrayToBufferedImage(resultarr)

writeImage(resultImage, "outg.bmp")

# Сounters
print "uniting regions and building countours...\n"
from ru.nickl.meanShift.direct.segmentator import  RegionGrowingAndAbsorbtionPU

for mrs in range(10,201,6):
    pu = RegionGrowingAndAbsorbtionPU()
    srcImage = readImage(r"Emphisclr.bmp");

    lc = LUVConverter()
    luvarray = lc.toLUVDArray(srcImage)

    pu.setMinRegionSize(mrs);
    pu.setData(LuvData(resultarr))
    pu.setEqualityRange(1)
    pu.formRegions()
    regions = pu.getRegions()
    i=0

    for region in regions:
        i=i+1
        for point in region.getCountour():
                luvarray[point.y][point.x] = LUV(100,0,0)
    print "regionssnumber=", i,"\n"

    writeImage(lc.LUVArrayToBufferedImage(luvarray), "outcon2%d.bmp" % mrs)
print "done\n"





