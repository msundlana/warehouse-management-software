package com.github.msundlana.warehousemanagementapi.models;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
public class ExceptionResponse {
    private Date timestamp;
    private String message;
    private String details;
}
