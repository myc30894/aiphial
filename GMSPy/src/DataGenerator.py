#! /usr/bin/python
# -*- coding: utf8 -*-

from ru.nickl.meanshift.general.dataStore import DataStore, SimpleNDimPoint
from ru.nickl.meanshift.general.meanShift import MeanShiftClusterer, MeanShiftClusterer1, FastMeanShiftClusterer

from ru.nickl.meanshift.general.datagenerator import DataGenerator

import time

def doClustering(dim, centers, points_count, deviation, maxValue, window_size, clusterer):
    
    dg = DataGenerator(dim)
    dg.deviation = deviation
    dg.maxValue = maxValue

    points = dg.generate(centers,points_count)

    ds = DataStore(dim)

    ds.addAll(points)

    ds.window=[window_size]*dim


    

    msc = clusterer
    msc.setDataStore(ds)
    msc.setMinDistance(10)
    start_time = time.time()
    msc.doClustering()
    clustering_time = time.time() - start_time

    clusters = msc.getClusters()
#
#    for cluster in clusters:
#        for point in cluster:
#            print point

    return clustering_time, len(clusters)

def avr(list):
    """Вычисляет среднее значение из численного иписка"""
    return sum(list)/float(len(list))



dim = 10

centers = 10
deviation = 20
maxValue = 500

points_count = 600

window_size = 60

#clusterer = MeanShiftClusterer

clusterer = FastMeanShiftClusterer(speedUpFactor=2)

for points_count in range(100, 2000, 100):
    etime_list = []
    clustersc = []
    for i in range(0, 3):
        etime,cc = doClustering(dim, centers, points_count, deviation, maxValue, window_size, clusterer)
        etime_list.append(etime)
        clustersc.append(cc)


    print points_count,avr(etime_list),(avr(clustersc)-centers)/centers


        #print "-"
    #print "-----\n"

#print "clusters=",len(clusters)

#print "time=",time.time()-start