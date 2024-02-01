package zaf.oauth2.client.oauth2client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Configuration
public class OAuth2ClientConfiguration {
    
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${app.oauth2.audience}") String audience
    ) {
        var responseClient = getDefaultClientCredentialsTokenResponseClient(audience);

        var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .refreshToken()
                .clientCredentials(builder -> builder.accessTokenResponseClient(responseClient).build())
                .build();

        var authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    private static DefaultClientCredentialsTokenResponseClient getDefaultClientCredentialsTokenResponseClient(String audience) {
        DefaultClientCredentialsTokenResponseClient responseClient = new DefaultClientCredentialsTokenResponseClient();
        OAuth2ClientCredentialsGrantRequestEntityConverter converter = new OAuth2ClientCredentialsGrantRequestEntityConverter();
        converter.addParametersConverter((grantRequest) -> {
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.set("audience", audience);
            return parameters;
        });

        responseClient.setRequestEntityConverter(converter);
        return responseClient;
    }

}
