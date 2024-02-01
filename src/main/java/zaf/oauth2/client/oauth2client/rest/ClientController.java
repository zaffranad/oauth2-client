package zaf.oauth2.client.oauth2client.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
public class ClientController {

    private final RestTemplate restTemplate;

    @Value("${app.game-server.url}")
    private String gameServerUrl;

    @GetMapping("/games")
    public ResponseEntity<String> get() {
        String result = restTemplate.getForObject(gameServerUrl + "/games", String.class);
        return ok("c'est good %s".formatted(result));
    }

    @GetMapping("/games/game/add")
    public ResponseEntity<String> getAfterAdd() {

        Game gameAdded = restTemplate.postForObject(
                URI.create(gameServerUrl + "/games/game/add"),
                new Game("100", "nouveau", "studio", LocalDate.parse("2020-12-11")),
                Game.class
        );

        return ok("c'est good %s".formatted(gameAdded));
    }

    public record Game(String id, String name, String studio, LocalDate releaseDate) {
    }
}
