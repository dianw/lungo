package xyz.mainapi.dashboard.core.data;

import java.net.MalformedURLException;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

/**
 * Minio S3 client config
 */
@Configuration
public class MinioConfig {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    public MinioClient minioClient(MinioConfigProperties properties) throws MalformedURLException {
        return MinioClient.builder()
            .endpoint(URI.create(properties.getEndpoint()).toURL())
            .credentials(properties.getAccessKey(), properties.getSecretKey())
            .build();
    }

    @Bean
    @ConfigurationProperties("minio")
    public MinioConfigProperties minioConfigProperties() {
        return ModifiableMinioConfigProperties.create();
    }

}
