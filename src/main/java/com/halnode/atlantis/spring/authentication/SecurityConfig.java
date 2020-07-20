package com.halnode.atlantis.spring.authentication;

import com.halnode.atlantis.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
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

/**
 * Configuration class for spring security.
 *
 * @author gowtham
 */
@RequiredArgsConstructor
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Qualifier("userDetailsServiceImpl")
    @NonNull
    public UserDetailsService userDetailsService;


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                .antMatcher("/api/admin/**")
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/admin/organization").permitAll()
                .antMatchers(HttpMethod.POST, "/auth/obtain-token").permitAll()
                .antMatchers("/api/admin/**").access("hasAnyRole('ADMIN')")
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout().deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .rememberMe()
                .key(Constants.REMEMBER_ME_SECRET_KEY).userDetailsService(userDetailsService)
                .tokenValiditySeconds(86400)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
