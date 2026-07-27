package com.api1.demo.mappers;

import com.api1.demo.dto.response.BudgetStatusResponse;
import com.api1.demo.dto.response.MonthlySummaryResponse;
import com.api1.demo.service.SummaryService;

import java.util.List;

public class SummaryMapper {

    private SummaryMapper() {}

    // Convierte el record interno del Service (SummaryService.MonthlySummary)
    // al DTO de response de la API.

    public static MonthlySummaryResponse toResponse(SummaryService.MonthlySummary summary) {
        return new MonthlySummaryResponse(
                summary.month(), summary.income(), summary.expense(),
                summary.balance(), summary.byCategory());
    }

    public static List<MonthlySummaryResponse> toResponseList(List<SummaryService.MonthlySummary> summaries) {
        return summaries.stream().map(SummaryMapper::toResponse).toList();
    }

    public static BudgetStatusResponse toResponse(SummaryService.BudgetStatus status) {
        return new BudgetStatusResponse(
                status.categoryName(), status.spent(), status.limit(),
                status.percentageUsed(), status.exceeded());
    }

    public static List<BudgetStatusResponse> toStatusResponseList(List<SummaryService.BudgetStatus> statuses) {
        return statuses.stream().map(SummaryMapper::toResponse).toList();
    }
}
