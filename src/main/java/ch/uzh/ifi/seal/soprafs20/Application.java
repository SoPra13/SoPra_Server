package ch.uzh.ifi.seal.soprafs20;

import ch.uzh.ifi.seal.soprafs20.constant.LobbyStatus;
import ch.uzh.ifi.seal.soprafs20.constant.LobbyType;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.UUID;

@RestController
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String helloWorld() {
        return "The application is running.";
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };
    }

}


@Transactional
@Service
class init {

    @Autowired
    private ChatService chatService;

    private LobbyRepository lobbyRepository;


    private UserRepository userRepository;

    @Autowired
    init(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.userRepository = userRepository;
        this.lobbyRepository = lobbyRepository;
    }

    @PostConstruct
    void postConstruct() {
        System.out.println("dora");
        for (int i = 0; i < 250; i++) {
            System.out.println("ree");
            userRepository.saveAndFlush(createRandomUser());
        }
    }

    User createRandomUser() {
        Random rnd = new Random();
        User user = new User();
        user.setPassword("hihigeheim");
        user.setUsername(getRandomName());
        user.setToken(UUID.randomUUID().toString());
        user.setStatus(UserStatus.OFFLINE);
        user.setTotalClues(rnd.nextInt(250));
        user.setDuplicateClues(rnd.nextInt(250));
        user.setTotalScore(rnd.nextInt(250));
        user.setGuessesMade(rnd.nextInt(250));
        user.setGamesPlayed(rnd.nextInt(250));
        user.setInvalidClues(rnd.nextInt(250));
        user.setGuessesCorrect(rnd.nextInt(250));
        return user;
    }

    String getRandomName() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String username = "";
        for (int i = 0; i < 15; i++) {
            username = username + alphabet.charAt(new Random().nextInt(alphabet.length()));
        }
        return username;
    }
}

