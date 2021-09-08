#!/bin/bash - 
#===============================================================================
#
#          FILE: generate-shot-test-data.sh
# 
#         USAGE: ./generate-shot-test-data.sh 
# 
#   DESCRIPTION: Generate test data for shots. 
#
#                Generates shots with increasing in/out points and random 
#                camera movement and shot type.
# 
#===============================================================================

set -o nounset                              # Treat unset variables as an error

STS=( NA MS LS ELS CU null)
CAS=( PAN TILT TRACK NA null)

rand() { echo $(( ( RANDOM % $1 ) )); }

i=0
NUM_FRAMES=${1:-10000}

echo "["

while [ $i -lt $NUM_FRAMES ]; 
do
  ST=${STS[$(rand ${#STS[@]})]}
  CA=${CAS[$(rand ${#CAS[@]})]}
  
  [[ "$ST" != "null" ]] && ST="\"$ST\""
  [[ "$CA" != "null" ]] && CA="\"$CA\""

  [ ! $i -eq 0 ] && echo ","

  IN_POINT=$(( i + 1))
  i=$(( i + $(rand 400) ))
  i=$(($i>$NUM_FRAMES ? $NUM_FRAMES : $i))

  echo -n "{ \"inPoint\" : $IN_POINT, \"outPoint\" : $i, \"shotType\" : $ST, \"cameraMovement\" :  $CA }"
done

echo "]"
