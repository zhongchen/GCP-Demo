#!/usr/bin/env bash

if [ $# != 1 ]; then
  echo "Usage start-dataproc.sh cluster-name"
fi

gcloud beta dataproc clusters create $1 --enable-component-gateway --bucket zhong-gcp \
  --region us-central1 --subnet default --zone us-central1-b --master-machine-type n1-standard-4 \
  --master-boot-disk-type pd-ssd --master-boot-disk-size 200 --num-workers 3 \
  --worker-machine-type n1-standard-4 --worker-boot-disk-type pd-ssd --worker-boot-disk-size 200 \
  --image-version 1.3-deb9 --optional-components ANACONDA,JUPYTER,PRESTO --project zhong-gcp
