package com.halnode.atlantis.spring.authentication;

import com.halnode.atlantis.util.JwtUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>This is performs JWT TOKEN VALIDATION/AUTHENTICATION if jwt token is present in the request.</p>
 * <p>If the JWT TOKEN is not present in the header
 * then it will pass on the request to the next filter in the spring security.</p>
 * <p>Every request will pass through this class</p>
 *
 * @author gowtham
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class JwtRequestFilter extends OncePerRequestFilter {

    @NonNull
    private final JwtUtil jwtUtil;

    @NonNull
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional(readOnly = true)
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");
        String username;
        String authToken;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
            authToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtil.extractUserName(authToken);
                if (!ObjectUtils.isEmpty(username)) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    if (jwtUtil.validateToken(authToken, username)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    } else {
                        logger.info("JWT TOKEN IS EXPIRED FOR THE USER " + username);
                    }
                }
            } catch (Exception e) {
                logger.error("Error occurred while validating the auth token");
            }
        }
        filterChain.doFilter(request, response);
    }
}
