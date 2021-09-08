#!/bin/bash - 
#===============================================================================
#
#          FILE: test-exceptions.sh
# 
#         USAGE: ./test-exceptions.sh 
# 
#   DESCRIPTION: Helper script to test various exception conditions
# 
#===============================================================================

set -o nounset                              # Treat unset variables as an error

echo "Test: Forced checked exception"

curl -s http://localhost:8080/videos/1/shots/auto/exception | jq .

echo ""
echo "Test: Forced runtime exception"

curl -s http://localhost:8080/videos/1/shots/auto/runtime | jq .

echo ""
echo "Test: Path conversion exception"

curl -s http://localhost:8080/videos/xxx/shots/auto | jq .

echo ""
echo "Test: Validation exception during persist time"

curl -s -XPOST localhost:8080/videos/1/shots/auto -H "Content-Type:application/json" -d '[{}]' | jq . 

echo ""
echo "Test: Malformed json"

curl -s -XPOST localhost:8080/videos/1/shots/auto -H "Content-Type:application/json" -d '[{}'  | jq .

echo ""
echo "Test: 404"

curl -s localhost:8080/abcdefgh | jq .

