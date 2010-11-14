#!/usr/bin/env python
# -*- coding: utf-8 -*-

# this file is originally from http://code.google.com/p/gaboratfer/ project
# but was modified. this file is not a part of Aiphial. It's just used to generate Gabor filtres
#
# The MIT License
#
# Copyright (c) 2010 http://code.google.com/p/gaboratfer/
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:#
#
# The above copyright notice and this permission notice shall be included in
# all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
# THE SOFTWARE.





import numpy
from numpy.fft import fft2, ifft2
import math
from PIL import Image
from scipy import signal

def gaborFunction(x, y, Lambda, theta, psi, sigma, gamma):
	"""	Gaborova funkcija.
		psi - faza kosinusa, u radijanima
		theta - orijentacija, u radijanima
	"""
	
	cosTheta = math.cos(theta)
	sinTheta = math.sin(theta)
	xTheta = x * cosTheta  + y * sinTheta
	yTheta = -x * sinTheta + y * cosTheta
	e = math.exp( -(xTheta**2 + yTheta**2 * gamma**2) / (2 * sigma**2) )
	cos = math.cos( 2 * math.pi * xTheta / Lambda + psi )
	return e * cos

def calcSigma(bandwidth, Lambda):
	return (Lambda / math.pi) * math.sqrt(math.log(2, math.e)/2)*(2**bandwidth + 1)/(2**bandwidth - 1)
	
def gaborFilter(height, width, minx, miny, maxx, maxy, Lambda, theta, psi, bandwidth, gamma):
	sigma = calcSigma(bandwidth, Lambda)
	filter = numpy.empty( (height, width) )
	xFactor = 1.0 * (maxx - minx) / height
	yFactor = 1.0 * (maxy - miny) / width
	for i in xrange(0, height):
		for j in xrange(0, width):
			filter[i, j] = gaborFunction(minx + i * xFactor, miny + j * yFactor, Lambda, theta, psi, sigma, gamma)
	return filter

def gaborFilterSimplified(Lambda, theta, psi, bandwidth, gamma):
	
	sigma = calcSigma(bandwidth, Lambda)
	n = int(6*sigma)
	#zelimo da filter uvijek bude neparne velicine (simetricnost)
	if not n%2:
		++n
	
	filter = numpy.empty( (n,n) )
	
	for i in xrange(0, n):
		for j in xrange(0, n):
			filter[i, j] = gaborFunction(j-n/2, i-n/2, Lambda, theta, psi, sigma, gamma)
	
	# normalizacija
	filter = filter - filter.sum() / (n*n)
	
	return filter

def gaborFilterImage(height, width, minx, miny, maxx, maxy, Lambda, theta, psi, bandwidth, gamma):
	filter = gaborFilter(height, width, minx, miny, maxx, maxy, Lambda, theta, psi, bandwidth, gamma)
	fmax = filter.max() 
	fmin = filter.min()
	image = Image.new("L", (height, width))
	canvas = image.load()
	for i in xrange(0, height):
		for j in xrange(0, width):
			canvas[i, j] = (filter[i, j]  - fmin) * 255 / (fmax - fmin)
	image.save("filter.png")

def degToRad(deg):
	return 2 * math.pi * deg / 360.0

def applyScipyConv(filter, image):
	r2 = image.size[0]
	c2 = image.size[1]
	r1 = len(filter)
	c1 = len(filter[0])
	image.shape = (r2, c2)
	image.dtype = numpy.uint8
	filter.shape = (r1, c1)
	result = signal.fftconvolve(image, filter,'same')
	#result = signal.correlate(image, filter)
	#result = convolve.convolve2d(filter, image, fft=1)

	resultImage = Image.new("L", (r2, c2))
	resultCanvas = resultImage.load()
	valueMax = result.max()
	valueMin = result.min()
	
	for x in xrange(0, r2):
		for y in xrange(0, c2):
			resultCanvas[y, x] = (result[x, y] - valueMin) * 255 / (valueMax - valueMin)
	return resultImage

# Konvolucija koja vraca normalizirani numpy.array, a ne sliku
def apply(filter, image):
	r2 = image.size[0]
	c2 = image.size[1]
	r1 = len(filter)
	c1 = len(filter[0])
	image.shape = (r2, c2)
	image.dtype = numpy.uint8
	filter.shape = (r1, c1)
	result = signal.fftconvolve(image, filter, 'same')
	
	return result

def applyFFtConvImg(filter, image, MinPad=True, pad=True):
	""" Not so simple convolution """

	#Just for comfort:
	FFt = fft2
	iFFt = ifft2

	#The size of the images:
	r2 = image.size[0]
	c2 = image.size[1]
	r1 = len(filter)
	c1 = len(filter[0])
	
	#MinPad results simpler padding,smaller images:
	if MinPad:
		r = r1+r2
		c = c1+c2
	else:
		#if the Numerical Recipies says so:
		r = 2*max(r1,r2)
		c = 2*max(c1,c2)

	#For nice FFT, we need the power of 2:
	if pad:
		pr2 = int(math.log(r)/math.log(2.0) + 1.0 )
		pc2 = int(math.log(c)/math.log(2.0) + 1.0 )
		rOrig = r
		cOrig = c
		r = 2**pr2
		c = 2**pc2
	#end of if pad
	
	#numpy fft has the padding built in, which can save us some steps
	#here. The thing is the s(hape) parameter:
	fftimage = FFt(filter, s=(r,c))*FFt(image,s=(r,c))

	if pad:
		result = (iFFt(fftimage))[:rOrig,:cOrig].real
	else:
		result = (iFFt(fftimage)).real

	resultImage = Image.new("L", (r2, c2))
	resultCanvas = resultImage.load()
	valueMax = result.max()
	valueMin = result.min()
	
	for x in xrange(0, r2):
		for y in xrange(0, c2):
			resultCanvas[y, x] = (result[x, y] - valueMin) * 255 / (valueMax - valueMin)
	return resultImage

def applyDirectConvolution(filter, image):
	imageHeight = image.size[0]
	imageWidth = image.size[1]
	filterHeight = len(filter)
	filterWidth = len(filter[0])
	result = numpy.zeros ( (imageHeight, imageWidth) )
	imageCanvas = image.load()
	for x in xrange(0, imageHeight):
		for y in xrange(0, imageWidth):
			for i in xrange(0, filterHeight):
				for j in xrange(0, filterWidth):
					imageValue = 0
					xTrans = x - filterHeight / 2 + i
					yTrans = y - filterWidth / 2 + i
					if xTrans >= 0 and xTrans < imageHeight:
						if yTrans >= 0 and yTrans < imageWidth:
							imageValue = imageCanvas[xTrans, yTrans]					
					result[x, y] += filter[i, j] * imageValue
	resultImage = Image.new("L", (imageHeight, imageWidth))
	resultCanvas = resultImage.load()
	valueMax = result.max()
	valueMin = result.min()
	
	for x in xrange(0, imageHeight):
		for y in xrange(0, imageWidth):
			resultCanvas[x, y] = (result[x, y] - valueMin) * 255 / (valueMax - valueMin)
	return resultImage

if __name__ == "__main__":
	def readImage(inputImage):
		file = open(inputImage, mode='rb')
		data = file.read()
		image = Image.fromstring("L", (64, 64), data, "raw", "L", 0, 1)
		return image

	#filtr = gaborFilter(8, 8, -4, -4, 4, 4, 2.5, math.pi/2, 0, 1, 1)
	#filtr = gaborFilterSimplified(2.5, 0, 0, 1, 1)

	#img = readImage("../face/000_1_1.nrm")
	#result = applyDirectConvolution(filtr, img)
	#result = apply(filtr, img)
	#result = applyScipyConv(filtr, img)

	#result.save("../test.png")

	filters = []
	for i in xrange(0,8):
		filters.append(gaborFilterSimplified(2.7, (math.pi/4.0)*i, 0, 1, 1))


	print "Array(" + ",\n\n".join([
		 "Array(\n" +  ",\n".join([					   
					   "Array(" + ",".join([str(l) for l in s ]) + ")"
					   for s in f					   
					   ]) +"\n)"
		 for f in filters
		 ]) +"\n)"
			
			
			
			#print "Array(" + ",".join([str(l) for l in s ]) + "),\n"
#			for l in s:
#			    print l,
			
			
		
		
	
	
#	combined = Image.new("L", (128, 128))
#	canvas = combined.load()
#	
#	for n in [0, 1]:
#		leftFilter = filtered[n].load()
#		rightFilter = filtered[n*2+1].load()
#		for x in xrange(0,64):
#			for y in xrange(0,64):
#				canvas[x,y+64*n] = leftFilter[x,y]
#				canvas[x+64,y+64*n] = rightFilter[x,y]
#
#	def stretch(im, size, filter=Image.NEAREST):
#		im.load()
#		im = im._new(im.im.stretch(size, filter))
#		return im
#
#	combined = stretch(combined, (64,64))
#	combined.save("../testCombined.png")
