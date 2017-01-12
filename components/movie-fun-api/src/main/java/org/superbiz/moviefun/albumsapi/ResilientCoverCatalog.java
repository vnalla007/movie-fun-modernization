package org.superbiz.moviefun.albumsapi;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.superbiz.moviefun.blobstore.Blob;
import org.superbiz.moviefun.blobstore.BlobStore;

import java.io.IOException;
import java.io.InputStream;

public class ResilientCoverCatalog extends CoverCatalog {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResilientCoverCatalog(BlobStore blobStore) {
        super(blobStore);
    }

    @Override
    @Bulkhead(name = "thumbnail", fallbackMethod = "getThumbnailFallback")
    Blob getThumbnail(long albumId) throws IOException, InterruptedException {
        return super.getThumbnail(albumId);
    }

    Blob getThumbnailFallback(long albumId, Throwable throwable) {
        logger.warn("Thumbnail fallback for album " + albumId + ": " + throwable);

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream input = classLoader.getResourceAsStream("empty.png");

        return new Blob("empty", input, MediaType.IMAGE_PNG_VALUE);
    }
}
