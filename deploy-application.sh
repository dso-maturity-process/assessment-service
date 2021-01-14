#!/bin/bash

kubectl apply -f minikube-deployment.yaml

kubectl rollout restart deployment/assessment-service-app -n dmp
