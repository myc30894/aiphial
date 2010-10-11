#! /usr/bin/python

__author__="Nickl"
__date__ ="$19.01.2009 19:58:57$"


from ru.nickl.meanShift.direct import LUVConverter,LUV
from tools import *

srcImage = readImage(r"C:\Users\Nickl\Documents\biotecnical\Programs\nbProjects\GMSPy\DSCN4909s100.bmp");

lc = LUVConverter()
luvarray = lc.toLUVDArray(srcImage)

file = open("Luv.arff","w")
file.write("""
@relation 'Luv'
@attribute x real
@attribute y real
@attribute L real
@attribute U real
@attribute V real
@data
""")

for i,line in enumerate(luvarray):
    for j,p in enumerate(line):
        file.write("%d,%d,%f,%f,%f\n" % (i,j,p.l,p.u,p.v))

        
file.close()

if __name__ == "__main__":
    print "Hello World";
