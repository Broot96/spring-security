package com.example.springsecurity.config;


import com.example.springsecurity.jwt.JwtAccessDeniedHandler;
import com.example.springsecurity.jwt.JwtAuthenticationEntryPoint;
import com.example.springsecurity.jwt.JwtFilter;
import com.example.springsecurity.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // PasswordEncoder는 BCryptPasswordEncoder를 사용
    @Value("${frontDomain}")
    private String frontDomain;
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws Exception {
        httpSecurity
                .httpBasic(
                        basic -> basic.disable()
                )
                .csrf(
                        csrf -> csrf.disable()
                )
                .logout(
                        it -> it
                                .clearAuthentication(true)
                                .deleteCookies("JSESSIONID")
                )
                .exceptionHandling( exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .headers( headers ->
                        headers.frameOptions(
                                it -> it.sameOrigin()
                        )
                )
                .sessionManagement( sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        new JwtFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                .authorizeHttpRequests( request -> {
                    request.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                            //TEST
                            .requestMatchers(new MvcRequestMatcher(introspector, "/role/**")).hasRole("ADMIN")
                            .requestMatchers(new MvcRequestMatcher(introspector, "/favicon.ico")).permitAll()
                            .requestMatchers(new MvcRequestMatcher(introspector, "/image/**")).permitAll()
                            .requestMatchers(new MvcRequestMatcher(introspector, "/auth/**")).permitAll()
                            .requestMatchers(new MvcRequestMatcher(introspector, "/**")).permitAll()
                            .anyRequest().authenticated();
                })
                .cors(
                        cors -> cors.configurationSource(corsConfigurationSource())
                );
        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin(frontDomain);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setMaxAge(3600l);
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
