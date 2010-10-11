#! /usr/bin/python

__author__="nickl"
__date__ ="$07.02.2009 15:57:03$"

#! /usr/bin/python
# -*- coding: utf8 -*-

from ru.nickl.meanshift.general.dataStore import DataStore, SimpleNDimPoint
from ru.nickl.meanshift.general.meanShift import MeanShiftClusterer, MeanShiftClusterer1, FastMeanShiftClusterer


class Client(SimpleNDimPoint):
    def __init__(self, *parameters):
        SimpleNDimPoint.__init__(self, parameters)




data = [
Client(25,60,30,34),
Client(46,90,32,28),
Client(29,50,32,33),
Client(22,30,28,19),
Client(23,35,24,34),
Client(48,80,31,32),
Client(46,30,31,32),
Client(46,30,32,32),
Client(46,30,33,32),
Client(46,30,34,32),
Client(46,30,35,32),
Client(27,60,35,34),
Client(28,17,30,34),
Client(29,70,39,38),
]

ds = DataStore(4)

ds.addAll(data)

ds.window=[15,30,15,15]

msc = FastMeanShiftClusterer(speedUpFactor=1)
msc.setDataStore(ds)
msc.setMinDistance(3)

msc.doClustering()

clusters = msc.getClusters()

for i, cluster in enumerate(clusters):
    print "----%d----" % i
    for point in cluster:
        print point.toString()

