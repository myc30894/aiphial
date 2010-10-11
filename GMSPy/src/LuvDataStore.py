
__author__="nickl"
__date__ ="$18.04.2009 23:11:33$"


from ru.nickl.meanshift.general.dataStore import DataStore, SimpleNDimPoint,MultiDimMapDataStore
from ru.nickl.meanshift.general.basic import MeanShiftClusterer, MeanShiftClusterer1, FastMeanShiftClusterer, SimpleBandwidthSelector
from ru.nickl.meanShift.direct import LuvData,LUVConverter,LUV, Point
#from ru.nickl.meanShift.direct.segmentator import *
#from ru.nickl.meanShift.direct.filter import *

from tools import *

from math import *

from java.util import ArrayList

def luvPointtoPoint(luvpoint):
    return Point(int(luvpoint.get(0)),int(luvpoint.get(1)),LUV(luvpoint.get(2),luvpoint.get(3),luvpoint.get(4)))

def pointtoLuvPoint(point):
    return SimpleNDimPoint([point.x, point.y, point.c.l, point.c.u, point.c.v])



class LuvDataStore0(DataStore):
    def __init__(self, luvarray):
        self._luvData = LuvData(luvarray) 
        self._nearestFinder = SpartialNearestFinder()
        self._nearestFinder.setData(self._luvData)
        
        w = self
        class criteria(SpartialNearestFinder.Criteria):
            def isNear(self,a,b):
                return abs(a.c.l-b.c.l)<w._window[2] and abs(a.c.u-b.c.u)<w._window[3] and abs(a.c.v-b.c.v)<w._window[4]
                    
                
        self._nearestFinder.additionalCriteria= criteria()
        
    def add(self, v):
        raise RuntimeError('not impl')

    def addAll(self, all):
        raise RuntimeError('not impl')

    def addOrGet(self, v):
        raise RuntimeError('not impl')

    def asList(self):
        result = []
        for x in range(self._luvData.width):           
            for y in range(self._luvData.height):                
                c = self._luvData.getLUV(x, y);
                result.append(SimpleNDimPoint([x,y,c.l,c.u,c.v]))
         
        return ArrayList(result)                 
            
    
    def clone(self):
        raise RuntimeError('not impl')

    def getDim(self):
        return 5
    
    def getFirst(self):
        raise RuntimeError('not impl')

    def getNearest(self, v):
        raise RuntimeError('not impl')

    def getWindow(self):
        return self._window

    def getWithinWindow(self, v):
        return [pointtoLuvPoint(x) for x in self._nearestFinder.getNearest(luvPointtoPoint(v))]

    def getWithinWindows(self, window, v):
        oldwindow = self._window
        self.setWindow(window)
        result = self.getWithinWindow(v)
        self.setWindow(oldwindow)
        return result

    def isEmpty(self):
        raise RuntimeError('not impl')

    def iterator(self):        
        return self.asList().iterator()
    
    def remove(self, v):
        raise RuntimeError('not impl')

    def removeWithinWindow(self, v):
        raise RuntimeError('not impl')

    def removeWithinWindow(self, window, v):
        raise RuntimeError('not impl')

    def setWindow(self, window):
        self._nearestFinder.setSquareRange(max(int(window[0]),int(window[1]))) 
        self._window = window  
 
def luvPointtoTuple(luvpoint):
    return int(luvpoint.get(0)),int(luvpoint.get(1)),luvpoint.get(2),luvpoint.get(3),luvpoint.get(4)

class LuvDataStorePy(DataStore):
    def __init__(self,luvarray):
        self._luvarray = luvarray


    def add(self, v):
        raise RuntimeError('not impl')

    def addAll(self, all):
        raise RuntimeError('not impl')

    def addOrGet(self, v):
        raise RuntimeError('not impl')

    def asList(self):
        result = []
        for y in range(len(self._luvarray)):
            for x in range(len(self._luvarray[0])):
                c=self._luvarray[y][x]
                result.append(LuvPointPy([y,x,c.l,c.u,c.v]))

        return ArrayList(result)


    def clone(self):
        raise RuntimeError('not impl')

    def getDim(self):
        return 5

    def getFirst(self):
        raise RuntimeError('not impl')

    def getNearest(self, v):
        raise RuntimeError('not impl')

    def getWindow(self):
        raise RuntimeError('not impl')

    def getWithinWindow(self, window, v):
        
        result = []
        y0,x0,l0,u0,v0 = luvPointtoTuple(v)
        yW,xW,lW,uW,vW = luvPointtoTuple(window)

        for x in range(max([x0-xW,0]), min([x0+xW+1,len(self._luvarray[0])])):
            for y in range(max([y0-yW,0]), min([y0+yW+1,len(self._luvarray)])):
                c = self._luvarray[y][x]
                if(l0-lW <= c.l <= l0+lW and u0-uW <= c.u <= u0+uW and v0-vW <=c.v <= v0+vW):                    
                    point = LuvPointPy([y,x,c.l,c.u,c.v])
                    
                    result.append(point)
        


        return ArrayList(result)

    def isEmpty(self):
        raise RuntimeError('not impl')

    def iterator(self):
        return self.asList().iterator()

    def remove(self, v):
        raise RuntimeError('not impl')

    def removeWithinWindow(self, v):
        raise RuntimeError('not impl')

    def removeWithinWindow(self, window, v):
        raise RuntimeError('not impl')

    def setWindow(self, window):
        raise RuntimeError('not impl')


if __name__ == "__main__":
    l = LuvDataStorePy(LUVConverter().toLUVDArray(readImage("DSCN4909s100.bmp")))
    print "Hello";
