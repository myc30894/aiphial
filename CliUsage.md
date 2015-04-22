To test segmentation algorithm you must download **aiphial-cmd** from [download page](http://code.google.com/p/aiphial/downloads/list) (or build it from source).

After downloading and archive extracting you can type in command line:
```
java -jar aiphial-cmd.jar agglosegm -i <path-to-image>
```

it would start segmentation process for the image. Resulting images would be written in current directory.

there are some other options:
```
Usage: agglosegm [options]
  Options:    -cr   color range (default: 7)
  * -i     input file name
    -md   minimum distance (default: 3)
    -mr   minimum region size (default: 0)
    -o     output file name (default: out.bmp)
    -sr   square range (default: 2)
    -wm   windows multiplier (default: 0.2)
    -wms  windows multiplier step (default: 0.1)
```
You could try them.

Also other commands besides **agglosegm** are available. You could list them by typing:
```
java -jar aiphial-cmd.jar -h
```