package me.schf.aws.common.config.ssm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;

@Configuration
public class AwsSsmConfig {
	
	@FunctionalInterface
	public interface SsmClientProvider {
	    SsmClient getClient();
	}

    @Bean
    SsmClientProvider ssmClientProvider() {
        return SsmClient::create;
    }
    
    public interface ParameterRetriever {
        String getParameter(String parameterName);
        Map<String, String> getParametersByPath(String path, boolean recursive);
    }

    @Bean("awsParameterRetriever")
    ParameterRetriever awsParameterRetriever(SsmClientProvider ssmClientProvider) {
        return new ParameterRetriever() {
            @Override
            public String getParameter(String parameterName) {
                var ssmClient = ssmClientProvider.getClient();
                var request = GetParameterRequest.builder()
                        .name(parameterName)
                        .withDecryption(true)
                        .build();

                return ssmClient.getParameter(request).parameter().value();
            }

            @Override
            public Map<String, String> getParametersByPath(String path, boolean recursive) {
                var ssmClient = ssmClientProvider.getClient();
                Map<String, String> parameters = new HashMap<>();
                String nextToken = null;

                do {
                    var request = GetParametersByPathRequest.builder()
                            .path(path)
                            .withDecryption(true)
                            .recursive(recursive)
                            .nextToken(nextToken)
                            .build();

                    var response = ssmClient.getParametersByPath(request);

                    response.parameters().forEach(param -> parameters.put(param.name(), param.value()));
                    nextToken = response.nextToken();

                } while (nextToken != null);

                return parameters;
            }
        };
    }

}