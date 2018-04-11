package com.example.preauthentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    DataSource ds;

    public AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService() {
        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
        jdbcDao.setDataSource(ds);
        return new MyUserDetailsService(jdbcDao);
    }

    public PreAuthenticatedAuthenticationProvider preAuthenticatedAuthenticationProvider() {
        PreAuthenticatedAuthenticationProvider provider
                = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(authenticationUserDetailsService());
        return provider;
    }

    public RequestHeaderAuthenticationFilter authenticationFilter() throws Exception {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setPrincipalRequestHeader("USER");
        return filter;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(authenticationFilter())
                .authorizeRequests()
                .mvcMatchers("/admin")
                .hasRole("ADMIN")
                .mvcMatchers("/user")
                .hasRole("USER")
                .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(preAuthenticatedAuthenticationProvider())
                .jdbcAuthentication().dataSource(ds)
                // passwordは使わないけどめんどくさいのでデフォルトスキーマを使う
                .withDefaultSchema()
                .withUser("user").password("xxx").roles("USER").disabled(false)
                .and()
                .withUser("admin").password("xxx").roles("ADMIN").disabled(false);
    }
}
