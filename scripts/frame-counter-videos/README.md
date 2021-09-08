# README

This directory contains scripts for generating frame counter test videos.

The test videos are created from single images using ``ffmpeg``. The images 
are placed in the folder ``individual-frames``. The images are not checked 
into version control (as they total about 1 GB).

The images can be generated using the ``generate-frames.sh`` script.

The script can be executed in parallel:

```
parallel < generate-frames-commands.txt
```

The commands for creating the test videos are found in file: ``generate-videos-commands.txt``

They can be executed in parallel:

```
parallel < generate-videos-commands.txt
```

## Issues

``ffmpeg`` was segfaulting unpredictably on some machines during generation of the 
test videos (on a local Ubuntu 20.04 and on research02). It was only possible 
to generate the 24 and 30 fps videos there.

The other versions could be generated on a Mac (macOS Catalina). Some searching 
suggested that it might have to do with the version of lib x246 against 
which ffmpeg was compiled.
