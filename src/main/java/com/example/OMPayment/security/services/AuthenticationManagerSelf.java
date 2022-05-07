package com.example.OMPayment.security.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@AllArgsConstructor
public class AuthenticationManagerSelf {

    public Authentication authenticate(UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
        Authentication authenticate = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return usernamePasswordAuthenticationToken.getCredentials();
            }

            @Override
            public Object getDetails() {
                return usernamePasswordAuthenticationToken.getDetails();
            }

            @Override
            public Object getPrincipal() {
                return usernamePasswordAuthenticationToken.getPrincipal();
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return usernamePasswordAuthenticationToken.getName();
            }
        };

        return authenticate;
    }
}
