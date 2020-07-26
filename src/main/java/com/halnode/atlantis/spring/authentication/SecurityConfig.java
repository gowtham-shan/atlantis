package com.halnode.atlantis.spring.authentication;

import com.halnode.atlantis.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Configuration class for spring security.
 * Order must be of HIGHEST_PRIORITY since this is base framework
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
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
                        FilterInvocationSecurityMetadataSource metadataSource = new CustomFilterInvocationSecurityMetadataSource();
                        fsi.setSecurityMetadataSource(metadataSource);
                        fsi.setAccessDecisionManager(new CustomAccessDecisionManager());
                        return fsi;
                    }
                })
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout().deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .rememberMe()
                .key(Constants.REMEMBER_ME_SECRET_KEY).userDetailsService(userDetailsService)
                .tokenValiditySeconds(Constants.REMEMBER_ME_TOKEN_VALIDITY)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    }

}
