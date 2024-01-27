package zaf.oauth2.client.oauth2client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Configuration
public class OAuth2ClientConfiguration {

    @Bean
    ClientRegistration myClientRegistration(
            @Value("${spring.security.oauth2.client.provider.my-client.token-uri}") String token_uri,
            @Value("${spring.security.oauth2.client.registration.my-client.client-id}") String client_id,
            @Value("${spring.security.oauth2.client.registration.my-client.client-secret}") String client_secret,
            @Value("${spring.security.oauth2.client.registration.my-client.authorization-grant-type}") String authorizationGrantType
    ) {
        return ClientRegistration
                .withRegistrationId("my-client")
                .tokenUri(token_uri)
                .clientId(client_id)
                .clientSecret(client_secret)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration oktaClientRegistration) {
        return new InMemoryClientRegistrationRepository(oktaClientRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }


    private static Converter<OAuth2ClientCredentialsGrantRequest, MultiValueMap<String, String>> parametersConverter(String audience) {
        return (grantRequest) -> {
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.set("audience", audience);
            return parameters;
        };
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${app.oauth2.audience}") String audience
    ) {
        DefaultClientCredentialsTokenResponseClient responseClient = new DefaultClientCredentialsTokenResponseClient();
        OAuth2ClientCredentialsGrantRequestEntityConverter converter = new OAuth2ClientCredentialsGrantRequestEntityConverter();
        converter.addParametersConverter(parametersConverter(audience));
        responseClient.setRequestEntityConverter(converter);


        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .refreshToken()
                        .clientCredentials(clientCredentialsGrantBuilder ->
                                clientCredentialsGrantBuilder.accessTokenResponseClient(responseClient)
                                        .build())
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
