#!/bin/bash - 
#===============================================================================
# 
#   DESCRIPTION: Load the shot test data found in src/test/resources into
#                a running instance of the shot service.
#
#                Excepts an .env file to be found in the execution directory 
#                which contains the required variables.
# 
#===============================================================================

[ -f ".env" ] && . ".env" || { echo "Environment file .env not found."; exit 1; } 

[ -z "$SHOT_SERVICE_HOST" ] && { echo "Required variable SHOT_SERVICE_HOST is empty. Put it in an .env file."; exit 1; }
[ -z "$SHOT_SERVICE_PORT" ] && { echo "Required variable SHOT_SERVICE_PORT is empty. Put it in an .env file."; exit 1; }
[ -z "$SHOT_SERVICE_SRC_BASE_PATH" ] && { echo "Required variable SHOT_SERVICE_SRC_BASE_PATH is empty. Put it in an .env file."; exit 1; }

declare -A VIDEOS_ID_MAP

# A bit cumbersome having to enter the IDs manually here
# Could be improved by querying the CA API and programmatically extracting 
# the corresponding object representation IDs
VIDEOS_ID_MAP[RG601322_5521]=9
VIDEOS_ID_MAP[RG601328_5529]=10
VIDEOS_ID_MAP[eyeland]=17
VIDEOS_ID_MAP[videos-counter-24fps]=18

i=1

for file in "${SHOT_SERVICE_SRC_BASE_PATH}"/src/test/resources/api/shots/shots-auto-create-*;
do
  echo "Processing file $file"

  VIDEO_NAME=$(basename $file | sed -E 's/^.*shots-auto-create-//' | sed -e 's/.json//') 

  ID=${VIDEOS_ID_MAP["$VIDEO_NAME"]}

  if [ -z $ID ]; then
    echo "No object representation ID defined for this file. Skipping the file."
    echo ""
    continue
  fi

  curl -XPOST -H 'Content-Type:application/json' -d "@$file" $SHOT_SERVICE_HOST:$SHOT_SERVICE_PORT/videos/$ID/shots/auto
  echo -e "\n"

  i=$(( i + 1 ))
done

