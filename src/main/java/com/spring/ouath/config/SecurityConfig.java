package com.spring.ouath.config;

import com.spring.ouath.config.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http.antMatcher("/**")
            .httpBasic().disable()
            .csrf().disable()
            .formLogin().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
                .authorizeRequests()
                .antMatchers("/v2/api-docs/**", "/swagger-ui.html", "/webjars/swagger-ui/**", "/swagger-ui/**", "/api/v1/auth/**","/exception/**").permitAll()
            .and()
                .authorizeRequests()
                .antMatchers("/api/v1/**").authenticated()
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .oauth2Login()
            .userInfoEndpoint()
            .userService(principalOauth2UserService);

         return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        val configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(false);

        val source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
