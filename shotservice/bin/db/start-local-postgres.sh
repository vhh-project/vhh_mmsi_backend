#!/bin/bash

echo "Starting PostgreSQL container"
docker run --name vhh-postgres-shotservice-local --rm -p"10270:5432" -e POSTGRES_PASSWORD=secret -e POSTGRES_USER=shotservice -d postgres:12.2 

echo "Starting PostgreSQL test container"
docker run --name vhh-postgres-shotservice-test-local --rm -p"10271:5432" -e POSTGRES_PASSWORD=secret -e POSTGRES_USER=shotservice_test -d postgres:12.2 
