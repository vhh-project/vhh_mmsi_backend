#!/bin/bash

[ -f ".env" ] && . ".env" || { echo "Environment file .env not found."; exit 1; } 

[ -z "$ARTIFACT_ID" ] && { echo "Required variable ARTIFACT_ID is empty. Put it in an .env file."; exit 1; }
[ -z "$LATEST_TAG" ] && { echo "Required variable LATEST_TAG is empty. Put it in an .env file."; exit 1; }
[ -z "$PROJECT_VERSION" ] && { echo "Required variable PROJECT_VERSION is empty. Put it in an .env file."; exit 1; }

docker push docker.max-recall.com/vhh/mmsi/${ARTIFACT_ID}:${LATEST_TAG}
docker push docker.max-recall.com/vhh/mmsi/${ARTIFACT_ID}:${PROJECT_VERSION}

