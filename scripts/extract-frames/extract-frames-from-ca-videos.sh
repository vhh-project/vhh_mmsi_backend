#!/bin/bash - 
#===============================================================================
#
#   DESCRIPTION: Bash script generating images / thumbnails from a video file
# 
#===============================================================================

set -o nounset                              # Treat unset variables as an error

CA_BASE_DIR="/var/www/html/providence"

# check if CA dir exists
[ ! -d "$CA_BASE_DIR" ] && { echo "CA_BASE_DIR: '${CA_BASE_DIR}' does not exist or ist not a directory."; exit 1; }

INPUT_DIR="$CA_BASE_DIR/media/collectiveaccess/quicktime"
OUTPUT_DIR="$CA_BASE_DIR/media/mmsi/videos"
PREFIX="thumb_"

# ffmpeg

FFMPEG="/usr/bin/ffmpeg"
FFPROBE="/usr/bin/ffprobe"


# Thumbnail dimensions 

X="120"
Y="-1"

# Some more ffmpeg options 

HB="-hide_banner"
#FFMPEG_LOGLEVEL="-loglevel panic"
FFMPEG_LOGLEVEL="-loglevel error"

# Functions

TS() { date "+%Y-%m-%d %H:%M:%S"; }


START_TS=$(date -d"$(TS)" "+%s")
echo "[$(TS)] Frame extraction process is starting up."

NO_OF_FILES=$(find $INPUT_DIR -not -type d | wc -l | sed "s/ *//g")

echo "[$(TS)] $NO_OF_FILES files about to be processed..."
i=0
for INPUT_FILE in $(find $INPUT_DIR -not -type d)
do
	let "i=i+1"
	echo "[$(TS)] Processing file $i of $NO_OF_FILES: '$INPUT_FILE'"
	SHORT=$(echo $INPUT_FILE | sed "s/.*\/\(.*\)$/\1/g")
	REP_ID=$(echo $INPUT_FILE | sed -n "s/.*\/.*_media_\([0-9]\+\)_.*/\1/p" )
	EXPECTED_NO_OF_FRAMES=$($FFPROBE -v error -select_streams v:0 -show_entries stream=nb_frames -of default=nokey=1:noprint_wrappers=1 "$INPUT_FILE")
	FRAME_RATE_AS_FRACTION=$($FFPROBE -v error -select_streams v:0 -of default=noprint_wrappers=1:nokey=1 -show_entries stream=r_frame_rate "$INPUT_FILE")
	FRAME_RATE_AS_REAL=$(perl -e 'print '"$FRAME_RATE_AS_FRACTION"'')
	FRAME_RATE_AS_INTEGER=$(perl -e 'use POSIX; print ceil('"$FRAME_RATE_AS_FRACTION"')')
	if [ ! -d "$OUTPUT_DIR/$REP_ID/" ]
	then
		echo "[$(TS)] New CA Representation ID $REP_ID found. Creating directory." 
		mkdir -p "$OUTPUT_DIR/$REP_ID/frames/json"
	
		FFMPEG_RES=$($FFMPEG -i "$INPUT_FILE" $FFMPEG_LOGLEVEL -vf "scale=$X:$Y" $HB "$OUTPUT_DIR/$REP_ID/frames/${PREFIX}${SHORT}_%09d.jpg" 2>&1)
		if [ "$FFMPEG_RES" = "" ]
		then
			echo "[$(TS)] Thumbs created for '$INPUT_FILE'"	
		else
			echo "[$(TS)] ERROR/WARNING THROWN BY FFMPEG: $FFMPEG_RES"	
		fi
	else
		echo "[$(TS)] ERROR: CA REPRESENTATION ID ALREADY EXISTS: $REP_ID. Ignoring file '$INPUT_FILE'"
	fi	
	GENERATED_NO_OF_FRAMES=$(find $OUTPUT_DIR/$REP_ID/frames/ -not -type d | grep -v "json" | wc -l)
	if [ "$EXPECTED_NO_OF_FRAMES" = "$GENERATED_NO_OF_FRAMES" ]
	then
		echo "[$(TS)] INFO: Expected number of frames matches generated number of thumbs: $EXPECTED_NO_OF_FRAMES"
		echo "{ \"frames\": $GENERATED_NO_OF_FRAMES, \"fps_fraction\" : \"$FRAME_RATE_AS_FRACTION\", \"fps_real\" : $FRAME_RATE_AS_REAL, \"fps_integer\" : $FRAME_RATE_AS_INTEGER }" > "$OUTPUT_DIR/$REP_ID/frames/json/${PREFIX}${SHORT}.json"
	else
		echo "[$(TS)] ERROR: $EXPECTED_NO_OF_FRAMES frames expected : $GENERATED_NO_OF_FRAMES thumbs generated."
	fi
done

ENDDATE=$(date "+%Y-%m-%d %H:%M:%S")
END_TS=$(date -d"$ENDDATE" "+%s")

DURATION=$((END_TS-START_TS))

echo "[$(TS)] Done. Processing time $DURATION s"


