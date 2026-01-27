package me.schf.aws.common.config.sns;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import me.schf.aws.common.config.client.RemoteClientProvider;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Configuration
public class AwsSnsConfig {
	
    private SnsClient snsClient;
	
	@Bean("snsClientProvider")
	RemoteClientProvider<SnsClient> snsClientProvider() {
        this.snsClient = SnsClient.create();
        return () -> snsClient;
	}

    @Bean
	TextSender awsTextSender(@Qualifier("snsClientProvider") RemoteClientProvider<SnsClient> snsClientProvider) {
        return (phoneNumber, message) -> {
            SnsClient sns = snsClientProvider.getClient();

            PublishRequest request = PublishRequest.builder()
                    .phoneNumber(phoneNumber)
                    .message(message)
                    .build();

            sns.publish(request);
        };
    }
    
    @PreDestroy
    public void shutdownSnsClient() {
        if (snsClient != null) {
            snsClient.close();
        }
    }
}