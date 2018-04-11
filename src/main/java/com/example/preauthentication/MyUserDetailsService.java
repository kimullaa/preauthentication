package com.example.preauthentication;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@RequiredArgsConstructor
public class MyUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    private final JdbcDaoImpl jdbcDao;

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        UserDetails userDetails = jdbcDao.loadUserByUsername((String) token.getPrincipal());
        return userDetails;
    }
}
