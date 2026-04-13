# storage-s3

Lightweight S3 storage abstraction for Spring Boot based on AWS SDK v2.

This library provides a simple `StorageService` interface and auto-configuration for any S3-compatible object storage (AWS S3, MinIO, custom S3 endpoints, etc.).

- ✅ Save / update / load / delete objects by key  
- ✅ Spring Boot auto-configuration  
- ✅ Configurable endpoint, region, bucket and key prefix  
- ✅ Works with any S3-compatible provider

---

## Requirements

- Java 17+ (recommended: 21)
- Spring Boot 3.x
- AWS SDK for Java v2 (S3 module)

---

## Installation

Add the dependency to your Spring Boot application.

### Maven

```xml
<dependency>
    <groupId>net.m9studio</groupId>
    <artifactId>storage-s3</artifactId>
    <version>1.0.1</version>
</dependency>
```

### Gradle (Groovy)

```groovy
implementation 'net.m9studio:storage-s3:1.0.1'
```

### Gradle (Kotlin)

```kotlin
implementation("net.m9studio:storage-s3:1.0.1")
```

---

## Configuration

The library is configured via `storage.s3.*` properties and uses Spring Boot auto-configuration.

Example (`application.yml`):

```yaml
storage:
  s3:
    enabled: true               # optional, default: true
    bucket: your-bucket-name    # required
    endpoint: https://s3.example.com   # optional for some providers
    region: us-east-1           # required
    access-key: YOUR_ACCESS_KEY
    secret-key: YOUR_SECRET_KEY
    prefix: ""                  # optional, e.g. "content" or "uploads/images"
```

If `endpoint` is not set, the AWS SDK will use its default endpoint resolution for the given region.

If `prefix` is set (for example `content`), all keys will be stored as `content/<key>`.

---

## Usage

The library exposes a single abstraction:

```java
package net.m9studio.storage.s3;

import java.io.InputStream;

public interface StorageService {

    void save(String key, InputStream content, long contentLength, String contentType);

    default void update(String key, InputStream content, long contentLength, String contentType) {
        save(key, content, contentLength, contentType);
    }

    InputStream load(String key);

    void delete(String key);
}
```

### Injecting the service

```java
import net.m9studio.storage.s3.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class FileStorageService {

    private final StorageService storageService;

    public FileStorageService(StorageService storageService) {
        this.storageService = storageService;
    }

    public void uploadFile(MultipartFile file, String key) throws IOException {
        storageService.save(
                key,
                file.getInputStream(),
                file.getSize(),
                file.getContentType() != null ? file.getContentType() : "application/octet-stream"
        );
    }

    public void updateFile(MultipartFile file, String key) throws IOException {
        storageService.update(
                key,
                file.getInputStream(),
                file.getSize(),
                file.getContentType() != null ? file.getContentType() : "application/octet-stream"
        );
    }

    public InputStream downloadFile(String key) {
        return storageService.load(key);
    }

    public void deleteFile(String key) {
        storageService.delete(key);
    }
}
```

### Example controller: streaming a file

```java
import net.m9studio.storage.s3.StorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/files")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/{key}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String key) {
        InputStream in = storageService.load(key);
        InputStreamResource resource = new InputStreamResource(in);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(resource);
    }
}
```

---

## How it works

The library provides:

- `S3StorageProperties` – configuration properties (`storage.s3.*`)
- `StorageService` – abstraction for basic storage operations
- `S3StorageServiceImpl` – S3-based implementation
- `S3StorageAutoConfiguration` – Spring Boot auto-configuration that:
  - creates an `S3Client`
  - exposes a `StorageService` bean

Auto-configuration is registered via:

`src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```text
net.m9studio.storage.s3.S3StorageAutoConfiguration
```

---

## License

MIT License (or any other license you prefer — adjust this section and add a LICENSE file accordingly).
