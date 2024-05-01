package com.github.msundlana.warehousemanagementapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
@NoArgsConstructor
public class JWTResponse {
    private String username;
    private String jwtToken;
    private List<String> roles;
}
