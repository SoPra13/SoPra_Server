package ch.uzh.ifi.seal.soprafs20;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@SpringBootApplication
public class Application {
    private static final String SANITIZER = "[\n|\r|\t]";

    public static void main(String[] args) {
        String[] sanetized = new String[args.length];
        for(int i = 0; i < args.length; i++) sanetized[i] = args[i].replaceAll(SANITIZER, "_");
<<<<<<< HEAD
        SpringApplication.run(Application.class, sanetized);
=======
        SpringApplication.run(Application.class, args);
>>>>>>> master
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("*");
            }
        };
    }
}