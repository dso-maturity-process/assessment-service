#!/bin/bash

echo $@

echo "Statement - $0"
echo "Arg #1 - service name - "$1""
echo "Arg #2 - number of attempts in secs - "$2""
echo "Arg #3 - wait time between attempts in secs - "$3""

if [ "$#" -ne 3 ]; then
  echo "Error: Incorrect number of parameters."
  echo "The following parameters are required:"
  echo "Arg #1 - service name (i.e. - http://gov-cio.test/myservice)"
  echo "Arg #2 - number of attempts in secs"
  echo "Arg #3 - wait time between attempts in secs"
  echo "Example: > check-service.sh http://gov-cio.test/country/all 10 10"
  exit 2
fi

echo "Checking availability of service "$1" "
servicename="$1"
attempts="$2"
waitsecs="$3"
count=1
response=500

while [ $response -ne 200 ]; do

  count=$count+1

  response=$(curl -X POST --write-out "%{http_code}\n" --silent --output /dev/null $servicename)

  if [[ $count -gt $attempts ]]; then
    echo "Limit of "$attempts" has been exceeded."
    exit 1
  fi

  if [[ $response -ne 200 ]]; then
    echo "Service "$1" is not ready. Response code = "$response". Checking again in "$waitsecs" sec(s)..."
    sleep $waitsecs
  fi
done
echo "Service "$1" is ready: Response code = "$response"."
exit 0
