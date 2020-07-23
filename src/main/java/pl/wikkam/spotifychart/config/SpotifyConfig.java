package pl.wikkam.spotifychart.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class SpotifyConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/**").permitAll();
    }

    @Primary
    @Bean
    public UserInfoTokenServices tokenService() {
        final UserInfoTokenServices tokenService = new UserInfoTokenServices("/user", "");
        return tokenService;
    }

}
