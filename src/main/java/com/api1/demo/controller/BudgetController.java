package com.api1.demo.controller;

import com.api1.demo.dto.request.BudgetRequest;
import com.api1.demo.dto.response.BudgetResponse;
import com.api1.demo.entity.User;
import com.api1.demo.mappers.BudgetMapper;
import com.api1.demo.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    // Ej: GET /api/budgets?month=2026-07
    @GetMapping
    public List<BudgetResponse> list(@AuthenticationPrincipal User currentUser,
                                     @RequestParam String month) {
        return BudgetMapper.toResponseList(budgetService.listForMonth(currentUser.getId(), month));
    }

    // Crea o actualiza el presupuesto de una categoría para un mes (setBudget ya
    // resuelve internamente si existe o hay que crearlo).
    @PostMapping
    public ResponseEntity<BudgetResponse> setBudget(@AuthenticationPrincipal User currentUser,
                                                    @Valid @RequestBody BudgetRequest request) {
        var budget = budgetService.setBudget(
                currentUser, request.categoryId(), request.monthlyLimit(), request.month());
        return ResponseEntity.status(HttpStatus.CREATED).body(BudgetMapper.toResponse(budget));
    }
}
