package me.schf.aws.common.config.sns;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Configuration
public class AwsSnsConfig {

    @FunctionalInterface
    public interface SnsClientProvider {
        SnsClient getClient();
    }

    @Bean
    SnsClientProvider snsClientProvider() {
        return SnsClient::create;
    }

    public interface TextSender {
        void sendText(String phoneNumber, String message);
    }

    @Bean
    TextSender awsTextSender(SnsClientProvider snsClientProvider) {
        return (phoneNumber, message) -> {
            var client = snsClientProvider.getClient();
            var request = PublishRequest.builder()
                    .phoneNumber(phoneNumber)
                    .message(message)
                    .build();
            client.publish(request);
        };
    }
}