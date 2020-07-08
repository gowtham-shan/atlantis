package com.halnode.atlantis.spring.authentication;

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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Qualifier("userDetailsServiceImpl")
    @NonNull
    public UserDetailsService userDetailsService;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Bean
//    public MapSessionRepository sessionRepository() {
//        MapSessionRepository repository = new MapSessionRepository(new ConcurrentHashMap<>());
//        repository.setDefaultMaxInactiveInterval(1800);
//        return repository;
//    }

    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception {
        return authenticationManager();
    }

    @NonNull
    private final JwtRequestFilter jwtRequestFilter;


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
                .httpBasic()
                .and()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/admin/organization").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/obtain-token").permitAll()
                .antMatchers("/api/admin/**").access("hasAnyRole('ADMIN')")
                //.antMatchers("/api/admin/users").hasAnyRole("USER", "ADMIN")
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

//        http.sessionManagement().maximumSessions(1).expiredUrl("/login?expired=true");
    }

}
