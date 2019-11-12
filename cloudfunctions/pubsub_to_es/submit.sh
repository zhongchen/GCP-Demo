#!/usr/bin/env bash

gcloud functions deploy pubsub_to_es --runtime python37 \
	--trigger-topic streaming_topic \
	--update-labels env=dev \
	--entry-point=pubsub_to_es \
	--memory=512MB \
	--service-account=cloud-function-pubsub-to-es@testdhaval.google.com.iam.gserviceaccount.com \
	--source=. \
	--region=us-central1