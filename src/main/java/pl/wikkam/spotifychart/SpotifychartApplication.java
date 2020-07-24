package pl.wikkam.spotifychart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@EnableResourceServer
@SpringBootApplication
public class SpotifychartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotifychartApplication.class, args);
    }

}
