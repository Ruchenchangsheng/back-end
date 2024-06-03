package com.hualiang.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.hualiang.security.filter.TokenAuthenticationFilter;
import com.hualiang.security.filter.TokenLoginFilter;
import com.hualiang.security.filter.TokenLogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private TokenLogoutHandler tokenLogoutHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        http.csrf().disable(); // 关闭csrf

        http.headers().contentSecurityPolicy("frame-ancestors 'self'");
        http.authorizeHttpRequests()
                .requestMatchers("/", "/user/login", "/register", "/user/register", "/user/email", "/error", "/static/**").permitAll()
                .requestMatchers("/user/create").hasAuthority("user.add")
                .requestMatchers("/hotel/registeration").hasAuthority("registeration.add")
                .anyRequest().authenticated()
                .and().addFilterBefore(new TokenAuthenticationFilter(redisTemplate), UsernamePasswordAuthenticationFilter.class)
                .addFilter(new TokenLoginFilter(authenticationConfiguration.getAuthenticationManager(), redisTemplate));
        
        http.formLogin()
                .loginPage("/") // 登录页
                .loginProcessingUrl("/user/login") // 登录接口
                .and().logout().logoutUrl("/user/logout").logoutSuccessHandler(tokenLogoutHandler).permitAll();



        http.exceptionHandling().accessDeniedPage("/unauth");

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/favicon.ico","/swagger-resources/**", "/webjars/**", "/v3/**", "/swagger-ui/**", "/doc.html");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
