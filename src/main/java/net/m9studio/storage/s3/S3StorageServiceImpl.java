package net.m9studio.storage.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;

/**
 * S3-based implementation of {@link StorageService}.
 */
public class S3StorageServiceImpl implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageServiceImpl.class);

    private final S3Client s3Client;
    private final S3StorageProperties props;

    public S3StorageServiceImpl(S3Client s3Client, S3StorageProperties props) {
        this.s3Client = s3Client;
        this.props = props;
    }

    private String keyWithPrefix(String key) {
        String prefix = props.getPrefix();
        if (prefix == null) {
            prefix = "";
        }
        if (!prefix.isEmpty() && !prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        if (key.startsWith("/")) {
            key = key.substring(1);
        }
        return prefix + key;
    }

    @Override
    public void save(String key, InputStream content, long contentLength, String contentType) {
        String fullKey = keyWithPrefix(key);

        PutObjectRequest putReq = PutObjectRequest.builder()
                                                  .bucket(props.getBucket())
                                                  .key(fullKey)
                                                  .contentType(contentType)
                                                  .build();

        try {
            s3Client.putObject(putReq, RequestBody.fromInputStream(content, contentLength));
            log.info("Stored object in S3: bucket={}, key={}", props.getBucket(), fullKey);
        } catch (Exception e) {
            log.error("Failed to store object in S3: bucket={}, key={}", props.getBucket(), fullKey, e);
            throw new RuntimeException("Failed to save object to storage", e);
        }
    }

    @Override
    public void update(String key, InputStream content, long contentLength, String contentType) {
        String fullKey = keyWithPrefix(key);

        PutObjectRequest putReq = PutObjectRequest.builder()
                                                  .bucket(props.getBucket())
                                                  .key(fullKey)
                                                  .contentType(contentType)
                                                  .build();

        try {
            s3Client.putObject(putReq, RequestBody.fromInputStream(content, contentLength));
            log.info("Updated object in S3: bucket={}, key={}", props.getBucket(), fullKey);
        } catch (Exception e) {
            log.error("Failed to update object in S3: bucket={}, key={}", props.getBucket(), fullKey, e);
            throw new RuntimeException("Failed to update object in storage", e);
        }
    }

    @Override
    public InputStream load(String key) {
        String fullKey = keyWithPrefix(key);

        GetObjectRequest getReq = GetObjectRequest.builder()
                                                  .bucket(props.getBucket())
                                                  .key(fullKey)
                                                  .build();

        try {
            ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getReq);
            log.debug("Loaded object from S3: bucket={}, key={}", props.getBucket(), fullKey);
            return response;
        } catch (NoSuchKeyException e) {
            log.warn("Object not found in S3: bucket={}, key={}", props.getBucket(), fullKey);
            throw new RuntimeException("Object not found in storage", e);
        } catch (Exception e) {
            log.error("Failed to load object from S3: bucket={}, key={}", props.getBucket(), fullKey, e);
            throw new RuntimeException("Failed to load object from storage", e);
        }
    }

    @Override
    public void delete(String key) {
        String fullKey = keyWithPrefix(key);

        DeleteObjectRequest delReq = DeleteObjectRequest.builder()
                                                        .bucket(props.getBucket())
                                                        .key(fullKey)
                                                        .build();

        try {
            s3Client.deleteObject(delReq);
            log.info("Deleted object from S3: bucket={}, key={}", props.getBucket(), fullKey);
        } catch (Exception e) {
            log.error("Failed to delete object from S3: bucket={}, key={}", props.getBucket(), fullKey, e);
            throw new RuntimeException("Failed to delete object from storage", e);
        }
    }
}
