package com.api1.demo.controller;

import com.api1.demo.dto.request.TransactionRequest;
import com.api1.demo.dto.response.TransactionResponse;
import com.api1.demo.mappers.TransactionMapper;
import com.api1.demo.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.api1.demo.entity.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Ej: GET /api/transactions?from=2026-07-01&to=2026-07-31&categoryId=...&page=0&size=20
    @GetMapping
    public Page<TransactionResponse> list(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {

        return transactionService
                .list(currentUser.getId(), categoryId, from, to, pageable)
                .map(TransactionMapper::toResponse);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody TransactionRequest request) {

        var transaction = transactionService.create(
                currentUser, request.categoryId(), request.amount(), request.description(),
                request.date(), request.type(), request.recurring(), request.tagIds());

        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionMapper.toResponse(transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User currentUser, @PathVariable UUID id) {
        transactionService.delete(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
