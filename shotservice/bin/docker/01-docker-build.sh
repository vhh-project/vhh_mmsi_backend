#!/bin/bash

[ -f ".env" ] && . ".env" || { echo "Environment file .env not found."; exit 1; } 

[ -z "$ARTIFACT_ID" ] && { echo "Required variable ARTIFACT_ID is empty. Put it in an .env file."; exit 1; }
[ -z "$LATEST_TAG" ] && { echo "Required variable LATEST_TAG is empty. Put it in an .env file."; exit 1; }
[ -z "$PROJECT_VERSION" ] && { echo "Required variable PROJECT_VERSION is empty. Put it in an .env file."; exit 1; }

# build
./mvnw -U clean package -DskipTests dockerfile:build | tr -cd '\11\12\15\40-\176'

# tag image with project version
docker tag docker.max-recall.com/vhh/mmsi/${ARTIFACT_ID}:${LATEST_TAG} docker.max-recall.com/vhh/mmsi/${ARTIFACT_ID}:${PROJECT_VERSION}
