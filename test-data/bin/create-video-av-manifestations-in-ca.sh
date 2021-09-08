#!/bin/bash -
#===============================================================================
#
#   DESCRIPTION: Create video manifestations in CA for the different test videos
#
#===============================================================================

set -o nounset                              # Treat unset variables as an error

[ -f ".env" ] && . ".env" || { echo "Environment file .env not found."; exit 1; }

[ -z "$CA_SERVICE_URL" ] && { echo "Required variable CA_SERVICE_URL is empty. Put it in an .env file."; exit 1; }

# use ls to sort by filename
#   if only globbing is used, mp4 files will be output before m4v files
#   (irrelevant at the moment, since there are only mp4 files)
for TEST_VIDEO in $(ls videos/*.mp4); #${TEST_VIDEOS[@]};
do
  FILE_NAME=$(basename $TEST_VIDEO)
  FILE_NAME=${FILE_NAME%.*}
  curl -k -XPUT "${CA_SERVICE_URL}/item/ca_objects" -d '
    {
      "intrinsic_fields": {
        "idno": "VHH-AVM-'$FILE_NAME'",
        "type_id": 24
      },
      "preferred_labels": [
        {
          "locale": "en_US",
          "name": "'$FILE_NAME'"
        }
      ],
      "attributes": {
        "vhh_Identifier": [
          {
            "locale": "en_US",
            "IdentifierScheme": "Filename",
            "IdentifierValue": "'$FILE_NAME'"
          }
        ],
        "vhh_Title": [
          {
            "locale": "en_US",
            "TitleText": "'$FILE_NAME'"
          }
        ]
      }
    }'
done
