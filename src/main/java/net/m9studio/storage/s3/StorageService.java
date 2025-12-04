package net.m9studio.storage.s3;

import java.io.InputStream;

/**
 * Generic storage abstraction.
 * Implemented for S3-compatible storage by {@link S3StorageServiceImpl}.
 */
public interface StorageService {

    /**
     * Store a new object by key.
     *
     * @param key           object key (for example: "00000.png" or "users/123/avatar.png")
     * @param content       data stream
     * @param contentLength content length in bytes (if known)
     * @param contentType   MIME type (for example "image/png")
     */
    void save(String key, InputStream content, long contentLength, String contentType);

    /**
     * Update existing object by key.
     * For S3 this is effectively the same as overwriting {@link #save(String, InputStream, long, String)}.
     */
    default void update(String key, InputStream content, long contentLength, String contentType) {
        save(key, content, contentLength, contentType);
    }

    /**
     * Load an object by key.
     *
     * @param key object key
     * @return InputStream with object data (caller is responsible for closing it)
     */
    InputStream load(String key);

    /**
     * Delete an object by key.
     *
     * @param key object key
     */
    void delete(String key);
}
