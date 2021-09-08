#!/bin/bash -
#===============================================================================
#
#   DESCRIPTION: Get av manifestations and object representations from CA
#
#===============================================================================

set -o nounset                              # Treat unset variables as an error

[ -f ".env" ] && . ".env" || { echo "Environment file .env not found."; exit 1; }

[ -z "$CA_SERVICE_URL" ] && { echo "Required variable CA_SERVICE_URL is empty. Put it in an .env file."; exit 1; }

curl -X POST "${CA_SERVICE_URL}/browse/ca_objects" -d '
{
	"criteria": {
		"type_facet": [
			24
		]
	},
	"bundles": {
		"ca_object_representations.representation_id": true,
		"ca_object_representations.media.original": {
			"returnURL": true
		}
	}
}'
