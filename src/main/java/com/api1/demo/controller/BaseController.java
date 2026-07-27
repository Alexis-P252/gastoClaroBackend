package com.api1.demo.controller;

import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    protected <T> ResponseEntity<T> created(T body) {
        return ResponseEntity.status(201).body(body);
    }

    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    protected ResponseEntity<String> badRequest(String message) {
        return ResponseEntity.badRequest().body(message);
    }

    protected ResponseEntity<String> notFound(String message) {
        return ResponseEntity.status(404).body(message);
    }
}
