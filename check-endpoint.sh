#!/bin/bash

echo $@

echo "Statement - $0"
echo "Arg #1 - ingress name - "$1""
echo "Arg #2 - namespace - "$2""
echo "Arg #3 - number of attempts in secs - "$3""
echo "Arg #4 - wait time between attempts in secs - "$4""
echo "Arg #5 - provider (i.e. AWS or local) - "$5""

if [ "$#" -ne 5 ]; then
  echo "Error: Incorrect number of parameters."
  echo "The following parameters are required:"
  echo "Arg #1 - ingress name (i.e. - my-service-ingress)"
  echo "Arg #2 - namespace (i.e. - app-namespace)"
  echo "Arg #3 - number of attempts in secs"
  echo "Arg #4 - wait time between attempts in secs"
  echo "Arg #5 - provider (i.e. AWS or local)"
  echo "Example: > check-endpoint.sh country-service-ingress kube-system 10 10 aws"
  exit 2
fi

  
echo "Checking availability of end point "$1" in namespace "$2" on provider "$5""
namespace="$2"
attempts="$3"
waitsecs="$4"
provider="$5"
count=1
external_ip=""

while [ -z $external_ip ]; do

  count=$count+1

  if [ $provider == "AWS" ] || [ $provider == "aws" ]
  then
    external_ip=$(kubectl get ingress $1 -n $namespace --output jsonpath="{.status.loadBalancer.ingress[*].hostname}")
  elif [ $provider == "LOCAL" ] || [ $provider == "local" ]
  then
    external_ip=$(kubectl get ingress $1 -n $namespace --output jsonpath="{.status.loadBalancer.ingress[*].ip}")
  fi
  
  if [[ $count -gt $attempts ]]; then
    echo "Limit of "$attempts" has been exceeded."
    exit 1
  fi

  if [[ -z "$external_ip" ]]; then
    echo "End point for "$1" is not ready. Checking again in "$waitsecs" sec(s)..."
    sleep $waitsecs
  fi
done
echo 'End point ready:' && echo $external_ip
exit 0
