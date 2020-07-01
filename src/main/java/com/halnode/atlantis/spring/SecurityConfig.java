package com.halnode.atlantis.spring;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@EnableSpringHttpSession
@EnableWebSecurity
public class SecurityConfig {

    @Qualifier("userDetailsServiceImpl")
    @NonNull
    public UserDetailsService userDetailsService;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Configuration
    @Order(1)
    @RequiredArgsConstructor
    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {
        @NonNull
        private final JwtRequestFilter jwtRequestFilter;

        @NonNull
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public AuthenticationManager customAuthenticationManager() throws Exception {
            return authenticationManager();
        }

        @Override
        public void configure(WebSecurity web) {
            assert web != null;
            web
                    .ignoring()
                    .antMatchers("/resources/**", "/static/**", "/css/**", "/plugins/**", "/images/**", "/js/**");
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .antMatchers("/authenticate").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .httpBasic();
            http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

    @Configuration
    @Order(2)
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Bean
        public MapSessionRepository sessionRepository() {
            MapSessionRepository repository = new MapSessionRepository(new ConcurrentHashMap<>());
            repository.setDefaultMaxInactiveInterval(1800);
            return repository;
        }

        @Override
        public void configure(WebSecurity web) {
            assert web != null;
            web
                    .ignoring()
                    .antMatchers("/resources/**", "/static/**", "/css/**", "/plugins/**", "/images/**", "/js/**");
        }


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .and()
                    .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/admin/organization").permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .antMatchers("/", "/authenticate").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .permitAll()
                    .and()
                    .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll().deleteCookies("SESSION")
                    .and()
                    .httpBasic();
        }
    }
}