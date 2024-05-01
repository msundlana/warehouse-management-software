package com.github.msundlana.warehousemanagementapi.controllers;

import com.github.msundlana.warehousemanagementapi.models.JWTRequest;
import com.github.msundlana.warehousemanagementapi.models.JWTResponse;
import com.github.msundlana.warehousemanagementapi.services.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.base}/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JWTService jwtService;
    @PostMapping("/login")
    public ResponseEntity<JWTResponse> login(@RequestBody JWTRequest request) {

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var userDetails = (UserDetails) authentication.getPrincipal();
        var token = jwtService.generateToken(userDetails);
        var roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        var response = JWTResponse.builder()
                .jwtToken(token)
                .username(userDetails.getUsername())
                .roles(roles).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
