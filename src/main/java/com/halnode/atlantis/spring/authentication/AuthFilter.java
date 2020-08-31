package com.halnode.atlantis.spring.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;

public class AuthFilter extends UsernamePasswordAuthenticationFilter {

    private String jsonUsername;
    private String jsonPassword;


    @Override
    protected String obtainPassword(HttpServletRequest request) {
        String password = null;
        if ("application/json".equals(request.getHeader("Content-Type"))) {
            password = this.jsonPassword;
        } else {
            password = super.obtainPassword(request);
        }

        return password;
    }

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username = null;

        if ("application/json".equals(request.getHeader("Content-Type"))) {
            username = this.jsonUsername;
        } else {
            username = super.obtainUsername(request);
        }

        return username;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token;
        if ("application/json".equals(request.getHeader("Content-Type"))) {

            try {
                StringBuffer sb = new StringBuffer();
                String line = null;

                BufferedReader reader = request.getReader();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                ObjectMapper mapper = new ObjectMapper();
                LoginRequest loginRequest = mapper.readValue(sb.toString(), LoginRequest.class);

                this.jsonUsername = loginRequest.getUsername();
                this.jsonPassword = loginRequest.getPassword();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //form
            return super.attemptAuthentication(request, response);
        }
        token = new UsernamePasswordAuthenticationToken(jsonUsername, jsonPassword);
        return this.getAuthenticationManager().authenticate(token);
    }

}
