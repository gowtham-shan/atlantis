package com.halnode.atlantis.spring.authentication;

import com.halnode.atlantis.spring.authentication.jwt.JwtRequestFilter;
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.halnode.atlantis.util.Constants.DEFAULT_LOGIN_PROCESSING_URL;
import static com.halnode.atlantis.util.Constants.DEFAULT_LOGOUT_PROCESSING_URL;
import static org.springframework.security.config.Customizer.withDefaults;

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

    @Bean
    public AuthFilter authFilter() throws Exception {
        AuthFilter authFilter = new AuthFilter();
        authFilter.setAuthenticationManager(this.authenticationManager());
        authFilter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(DEFAULT_LOGIN_PROCESSING_URL, "POST"));
        return authFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @NonNull
    private final JwtRequestFilter jwtRequestFilter;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
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
                .formLogin().loginProcessingUrl(DEFAULT_LOGIN_PROCESSING_URL)
                .defaultSuccessUrl("/")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(DEFAULT_LOGOUT_PROCESSING_URL))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                .and()
                .rememberMe()
                .key(Constants.REMEMBER_ME_SECRET_KEY).userDetailsService(userDetailsService)
                .tokenValiditySeconds(Constants.REMEMBER_ME_TOKEN_VALIDITY)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).httpBasic();

    }

}
