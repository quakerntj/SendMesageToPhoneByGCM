#!/bin/bash

api_key=AIzaSyD_e8YpgV0tv8geFIAK7YVCe2WfoBkOkUs
token=PUT_YOUR_TOKEN_HERE

if [ "$1" != "" ]; then
    LITERAL=`literal $1`
    curl --header "Authorization: key=$api_key" --header Content-Type:"application/json" https://gcm-http.googleapis.com/gcm/send -d '{ "to" : "'"$token"'", "data" : { "message" : "'"$1"'" }  }'
fi

