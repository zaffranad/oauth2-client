package zaf.oauth2.client.oauth2client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    MyClientRestTemplate rest(final OAuth2AuthorizedClientManager authorizedClientServiceAndManager) {
        return new MyClientRestTemplate(authorizedClientServiceAndManager);
    }
}
