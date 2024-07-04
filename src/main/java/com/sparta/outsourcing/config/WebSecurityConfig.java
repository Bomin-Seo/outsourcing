package com.sparta.outsourcing.config;

import com.sparta.outsourcing.enums.UserRoleEnum;
import com.sparta.outsourcing.exception.CustomAccessDeniedHandler;
import com.sparta.outsourcing.exception.CustomAuthenticationEntryPoint;
import com.sparta.outsourcing.repository.UserRepository;
import com.sparta.outsourcing.security.JwtAuthenticationFilter;
import com.sparta.outsourcing.security.JwtAuthorizationFilter;
import com.sparta.outsourcing.service.AuthService;
import com.sparta.outsourcing.security.JwtProvider;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig
 */
@Configuration
@EnableJpaAuditing
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * SecurityConfig 필드값
     */
    private final JwtProvider jwtProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserRepository userRepository;


    /**
     * 생성자 매서드
     */
    public WebSecurityConfig(JwtProvider jwtProvider,
                             AuthenticationConfiguration authenticationConfiguration, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.authenticationConfiguration = authenticationConfiguration;
        this.userRepository = userRepository;
    }


    /**
     * 빈주입
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, userRepository,authenticationManager(authenticationConfiguration),bCryptPasswordEncoder());
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtProvider);
    }


    /**
     * Security 인증, 인가 설정
     * 커스텀 필터 설정
     * 서버단 에러 status 핸들러
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthService authService) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/api/user/signup/**","/api/auth/login","/api/auth/reissue").permitAll()
                        .requestMatchers("/api/restaurant/menu/","/api/restaurant").hasAuthority(UserRoleEnum.ROLE_ADMIN.name())
                        // 서버 단에서 에러가 발생시 아래 url이 에러창을 띄워준다
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
        );

        http.exceptionHandling(auth -> {
            auth.accessDeniedPage("/forbidden");
            auth.accessDeniedHandler(new CustomAccessDeniedHandler());
            auth.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
        });

        http.logout(auth -> auth
                .logoutUrl("/api/auth/logout")
                .addLogoutHandler(authService)
                .logoutSuccessHandler(
                        (((request, response, authentication) -> SecurityContextHolder.clearContext()))));

        // 필터 관리
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        return http.build();
    }
}
