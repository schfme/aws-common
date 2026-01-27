package me.schf.aws.common.config.ssm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;
import me.schf.aws.common.config.client.RemoteClientProvider;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParametersByPathRequest;

@Configuration
public class AwsSsmConfig {
	
    private SsmClient ssmClient;

	@Bean("ssmClientProvider")
	RemoteClientProvider<SsmClient> ssmClientProvider() {
        this.ssmClient = SsmClient.create();
        return () -> ssmClient;
	}

    public interface ParameterRetriever {
        String getParameter(String parameterName);
        Map<String, String> getParametersByPathRecursive(String path);
        Map<String, String> getParametersByPathNonRecursive(String path);
    }

    @Bean("awsParameterRetriever")
    ParameterRetriever awsParameterRetriever(@Qualifier("ssmClientProvider") RemoteClientProvider<SsmClient> ssmClientProvider) {
        return new ParameterRetriever() {
            @Override
            public String getParameter(String parameterName) {
                var ssm = ssmClientProvider.getClient();
                var request = GetParameterRequest.builder()
                        .name(parameterName)
                        .withDecryption(true)
                        .build();

                return ssm.getParameter(request).parameter().value();
            }

            @Override
            public Map<String, String> getParametersByPathRecursive(String path) {
                return fetchParametersByPath(path, true);
            }

            @Override
            public Map<String, String> getParametersByPathNonRecursive(String path) {
                return fetchParametersByPath(path, false);
            }

            private Map<String, String> fetchParametersByPath(String path, boolean recursive) {
                var ssm = ssmClientProvider.getClient();
                Map<String, String> parameters = new HashMap<>();
                String nextToken = null;

                do {
                    var request = GetParametersByPathRequest.builder()
                            .path(path)
                            .withDecryption(true)
                            .recursive(recursive)
                            .nextToken(nextToken)
                            .build();

                    var response = ssm.getParametersByPath(request);
                    response.parameters().forEach(param -> parameters.put(param.name(), param.value()));
                    nextToken = response.nextToken();
                } while (nextToken != null);

                return parameters;
            }
        };
    }
    
    @PreDestroy
    public void shutdownSsmClient() {
        if (ssmClient != null) {
        	ssmClient.close();
        }
    }
}
