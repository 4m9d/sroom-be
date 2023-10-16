package com.m9d.sroom.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().build();
    }
}
