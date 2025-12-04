package net.m9studio.storage.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * S3 storage configuration properties.
 *
 * Example (application.yml):
 *
 * storage:
 *   s3:
 *     enabled: true
 *     bucket: your-bucket
 *     endpoint: https://s3.example.com
 *     region: us-east-1
 *     access-key: YOUR_ACCESS_KEY
 *     secret-key: YOUR_SECRET_KEY
 *     prefix: ""   # optional
 */
@ConfigurationProperties(prefix = "storage.s3")
public class S3StorageProperties {

    /**
     * Enables or disables S3 storage autoconfiguration.
     */
    private boolean enabled = true;

    /**
     * Bucket name.
     */
    private String bucket;

    /**
     * S3 endpoint (optional for some providers).
     */
    private String endpoint;

    /**
     * S3 region (for example "us-east-1").
     */
    private String region;

    /**
     * Access key (key id).
     */
    private String accessKey;

    /**
     * Secret key.
     */
    private String secretKey;

    /**
     * Optional key prefix inside the bucket.
     * If empty or null, keys are used as-is.
     */
    private String prefix;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Returns prefix, never null.
     */
    public String getPrefix() {
        return prefix != null ? prefix : "";
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
