package me.schf.aws.common.config.s3;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import me.schf.aws.common.config.client.RemoteClientProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Configuration
public class AwsS3Config {
	
	private S3Client s3Client;

	@Bean("s3ClientProvider")
	RemoteClientProvider<S3Client> s3ClientProvider() {
		s3Client = S3Client.create();
		return () -> s3Client;
	}

	@Bean("awsS3FileWriter")
	RemoteFileWriter<S3RemoteFile> awsS3FileWriter(
			@Qualifier("s3ClientProvider") RemoteClientProvider<S3Client> s3ClientProvider) {
		return (s3RemoteFile, contents) -> {
			var s3 = s3ClientProvider.getClient();
			String bucket = s3RemoteFile.getBucket();
			String key = s3RemoteFile.getBucket();

			var request = PutObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.build();

			s3.putObject(request, RequestBody.fromBytes(contents));
		};
	}

	@Bean("awsS3FileRetriever")
	RemoteFileRetriever<S3RemoteFile> awsS3FileRetriever(
			@Qualifier("s3ClientProvider") RemoteClientProvider<S3Client> s3ClientProvider) {
		return s3RemoteFile -> {
			var s3 = s3ClientProvider.getClient();
			String bucket = s3RemoteFile.getBucket();
			String key = s3RemoteFile.getKey();

			var request = GetObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.build();

			try (ResponseInputStream<GetObjectResponse> response = s3.getObject(request)) {
				return response.readAllBytes();
			} catch (IOException e) {
				throw new IllegalStateException("Failed to read S3 object: s3://" + bucket + "/" + key, e);
			}
		};

	}

    @PreDestroy
    public void shutdownS3Client() {
        if (s3Client != null) {
        	s3Client.close();
        }
    }
}
