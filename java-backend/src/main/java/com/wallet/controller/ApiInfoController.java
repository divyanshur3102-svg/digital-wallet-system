package com.wallet.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ApiInfoController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        return ResponseEntity.ok(Map.of(
                "name", "Digital Wallet API",
                "status", "running",
                "version", "1.0",
                "docs", "/swagger-ui.html",
                "auth", "Use /auth/register or /auth/login"
        ));
    }
}
