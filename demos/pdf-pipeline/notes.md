
## Configure the GCS events notification for Pub/Sub
```
gsutil notification create -t gcs-notification -p pdf/ \
-e OBJECT_FINALIZE -f json gs://zhong-gcp
```

See the [options](https://cloud.google.com/storage/docs/gsutil/commands/notification#options)

