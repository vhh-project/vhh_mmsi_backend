#!/bin/bash - 
#===============================================================================
#
#          FILE: dump-shotservice-db.sh
# 
#         USAGE: ./dump-shotservice-db.sh 
# 
#   DESCRIPTION: Dump the shotservice database running in the local postgres container.
# 
#===============================================================================

set -o nounset                              # Treat unset variables as an error

docker exec vhh-postgres-shotservice-local pg_dump -C -c -s -U shotservice > src/main/resources/db/shotservice-schema.sql

