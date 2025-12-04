package net.m9studio.storage.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

/**
 * Spring Boot auto-configuration for S3-based {@link StorageService}.
 *
 * Enabled when:
 *  - S3Client is on the classpath
 *  - property "storage.s3.enabled" is true or not set
 */
@Configuration
@ConditionalOnClass(S3Client.class)
@EnableConfigurationProperties(S3StorageProperties.class)
@ConditionalOnProperty(prefix = "storage.s3", name = "enabled", havingValue = "true", matchIfMissing = true)
public class S3StorageAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client(S3StorageProperties props) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                props.getAccessKey(),
                props.getSecretKey()
        );

        S3ClientBuilder builder = S3Client.builder()
                                          .region(Region.of(props.getRegion()))
                                          .credentialsProvider(StaticCredentialsProvider.create(credentials));

        if (props.getEndpoint() != null && !props.getEndpoint().isBlank()) {
            builder = builder.endpointOverride(URI.create(props.getEndpoint()));
        }

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(StorageService.class)
    public StorageService storageService(S3Client s3Client, S3StorageProperties props) {
        return new S3StorageServiceImpl(s3Client, props);
    }
}
