package zaf.oauth2.client.oauth2client.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import zaf.oauth2.client.oauth2client.MyClientRestTemplate;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class ClientController {

    private final MyClientRestTemplate restTemplate;


    @GetMapping("/games")
    public ResponseEntity<String> get() {
        String result = restTemplate.getForObject("http://localhost:8080/games", String.class);
        return ResponseEntity.ok("c'est good %s".formatted(result));

    }

    @GetMapping("/games/game/add")
    public ResponseEntity<String> getAfterAdd() {

        Game gameAdded = restTemplate.postForObject(
                URI.create("http://localhost:8080/games/game/add"),
                new Game("100", "nouveau", "studio", LocalDate.parse("2020-12-11")),
                Game.class
        );

        return ResponseEntity.ok("c'est good %s".formatted(gameAdded));
    }

    public record Game(String id, String name, String studio, LocalDate releaseDate) {
    }
}
