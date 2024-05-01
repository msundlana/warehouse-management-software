package com.github.msundlana.warehousemanagementapi.security;

import com.github.msundlana.warehousemanagementapi.models.JWTResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableMethodSecurity(prePostEnabled = true)
public class InMemoryUserDetailsService implements UserDetailsService {

    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    private final Map<String, JWTResponse> users = new ConcurrentHashMap<>();

    public InMemoryUserDetailsService(@Value("${user.username}") String username,
                                      @Value("${user.password}") String password,
                                      @Value("${admin.username}") String adminUsername,
                                      @Value("${admin.password}") String adminPassword) {
        users.put(username, new JWTResponse(username, "{noop}"+password, List.of(USER_ROLE)));
        users.put(adminUsername, new JWTResponse(adminUsername, "{noop}"+adminPassword, List.of(USER_ROLE,ADMIN_ROLE)));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(users.get(username))
                .map(this::getUser)
                .orElseThrow(() -> new RuntimeException(String.format("user = %s not present ", username)));
    }

    private UserDetails getUser(JWTResponse user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getJwtToken())
                .roles(user.getRoles().toArray(new String[0])).build();
    }
}