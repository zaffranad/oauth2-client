package zaf.oauth2.client.oauth2client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static java.util.Objects.requireNonNull;

@Slf4j
@Configuration
public class RestTemplateConfiguration {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    @Bean
    RestTemplate loadRestTemplate(final OAuth2AuthorizedClientManager authorizedClientServiceAndManager) {
        var restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            log.debug("checking authorization");

            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("client-example")
                    .principal("client-example RestTemplate Oauth2")
                    .build();

            OAuth2AuthorizedClient authorizedClient = authorizedClientServiceAndManager.authorize(authorizeRequest);
            final var token = requireNonNull(authorizedClient).getAccessToken().getTokenValue();
            request.getHeaders().put(AUTHORIZATION, Collections.singletonList(BEARER.concat(token)));
            log.debug("token : {}", token);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
