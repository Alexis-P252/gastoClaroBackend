package com.api1.demo.controller;

import com.api1.demo.dto.response.BudgetStatusResponse;
import com.api1.demo.dto.response.MonthlySummaryResponse;
import com.api1.demo.entity.User;
import com.api1.demo.mappers.SummaryMapper;
import com.api1.demo.service.SummaryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    // Ej: GET /api/summary/monthly?month=2026-07
    @GetMapping("/summary/monthly")
    public MonthlySummaryResponse monthly(@AuthenticationPrincipal User currentUser,
                                          @RequestParam String month) {
        return SummaryMapper.toResponse(summaryService.getMonthlySummary(currentUser.getId(), month));
    }

    // Ej: GET /api/summary/trend?months=6
    @GetMapping("/summary/trend")
    public List<MonthlySummaryResponse> trend(@AuthenticationPrincipal User currentUser,
                                              @RequestParam(defaultValue = "6") int months) {
        return SummaryMapper.toResponseList(summaryService.getTrend(currentUser.getId(), months));
    }

    // Ej: GET /api/budgets/status?month=2026-07
    @GetMapping("/budgets/status")
    public List<BudgetStatusResponse> budgetStatus(@AuthenticationPrincipal User currentUser,
                                                   @RequestParam String month) {
        return SummaryMapper.toStatusResponseList(
                summaryService.getBudgetStatus(currentUser.getId(), month));
    }
}