#!/bin/bash - 
#===============================================================================
#
#   DESCRIPTION: Load the shotservice schema into the shotservice-db docker container
#                Warning: will completely drop the exiting database
# 
#===============================================================================

[ -f ".env" ] && . ".env" || { echo "Environment file .env not found."; exit 1; }

[ -z "$SHOT_SERVICE_SRC_BASE_PATH" ] && { echo "Required variable SHOT_SERVICE_SRC_BASE_PATH is empty. Put it in an .env file."; exit 1; }
[ -z "$SHOT_SERVICE_DOCKER_DB" ] && { echo "Required variable SHOT_SERVICE_DOCKER_DB is empty. Put it in an .env file."; exit 1; }

docker cp "${SHOT_SERVICE_SRC_BASE_PATH}/src/main/resources/db/shotservice-schema.sql" "${SHOT_SERVICE_DOCKER_DB}:/"
docker exec -e PGPASSWORD=secret "${SHOT_SERVICE_DOCKER_DB}" psql -U shotservice -f /shotservice-schema.sql -d postgres

