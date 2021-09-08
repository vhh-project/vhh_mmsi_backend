#!/bin/bash - 
#===============================================================================
# 
#   DESCRIPTION: Script for generating frames to be used as the basis for a 
#                frame counter video
# 
#===============================================================================

# set -o nounset                              # Treat unset variables as an error

START_FRAME=1
END_FRAME=100

[ ! -z $1 ] && START_FRAME=$1
[ ! -z $2 ] && END_FRAME=$2

echo $START_FRAME
echo $END_FRAME

for ((i=START_FRAME;i<=END_FRAME;i++));
do
  LABEL=$(printf %06d $i)
  convert -background grey -fill black -size 640x480 -font Bitstream-Vera-Sans-Mono -pointsize 72 -gravity center label:$LABEL frame-$LABEL.png

  if (( $i % 100 == 0 ));
  then
    echo "Iteration $i"
  fi

done

