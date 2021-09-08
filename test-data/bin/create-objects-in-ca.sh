#!/bin/bash -
#===============================================================================
#
#   DESCRIPTION: Create object test data in CA
#
#===============================================================================

set -o nounset                              # Treat unset variables as an error

[ -f ".env" ] && . ".env" || { echo "Environment file .env not found."; exit 1; }

[ -z "$CA_SERVICE_URL" ] && { echo "Required variable CA_SERVICE_URL is empty. Put it in an .env file."; exit 1; }

for JSON_OBJECT in $(ls objects/*.json);
do
  curl -k -XPUT "${CA_SERVICE_URL}/item/ca_objects" -d "@$JSON_OBJECT"
done
