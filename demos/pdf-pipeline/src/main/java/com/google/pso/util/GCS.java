package com.google.pso.util;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GCS {
    private static final Storage storage =
            StorageOptions.getDefaultInstance().getService();

    private GCS() {}

    public static byte[] download(String bucket, String filename) {
        BlobId blobId = BlobId.of(bucket, filename);
        return storage.get(blobId).getContent();
    }

    public static void upload(String bucket, String filename,
                              byte[] content, String contentType) {
        BlobId blobId = BlobId.of(bucket, filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).
                setContentType(contentType).
                build();
        storage.create(blobInfo, content);
    }
}
