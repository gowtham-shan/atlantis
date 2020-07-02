package com.halnode.atlantis.spring;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@EnableSpringHttpSession
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Qualifier("userDetailsServiceImpl")
    @NonNull
    public UserDetailsService userDetailsService;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public MapSessionRepository sessionRepository() {
        MapSessionRepository repository = new MapSessionRepository(new ConcurrentHashMap<>());
        repository.setDefaultMaxInactiveInterval(1800);
        return repository;
    }


    @NonNull
    private final JwtRequestFilter jwtRequestFilter;

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
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/admin/organization").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/", "/auth/obtain-token").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout().logoutUrl("/auth/logout").permitAll().deleteCookies("SESSION")
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
