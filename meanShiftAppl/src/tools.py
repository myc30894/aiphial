from javax.imageio.ImageIO import write , read 
from java.io import File

import sys
sys.path.append("meanShift.jar")

def writeImage(image,filename,type="bmp"):
    write(image,type,File(filename))
def readImage(filename):
    return read(File(filename))
    

