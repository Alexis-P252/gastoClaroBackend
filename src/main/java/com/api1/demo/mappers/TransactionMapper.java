package com.api1.demo.mappers;

import com.api1.demo.dto.response.TransactionResponse;
import com.api1.demo.entity.Tag;
import com.api1.demo.entity.Transaction;

import java.util.List;

public class TransactionMapper {

    private TransactionMapper() {}

    public static TransactionResponse toResponse(Transaction transaction) {
        List<String> tagNames = transaction.getTags().stream()
                .map(Tag::getName)
                .toList();

        return new TransactionResponse(
                transaction.getId(),
                transaction.getCategory().getName(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getType(),
                transaction.isRecurring(),
                tagNames
        );
    }

    public static List<TransactionResponse> toResponseList(List<Transaction> transactions) {
        return transactions.stream().map(TransactionMapper::toResponse).toList();
    }

    // No hay toEntity() acá: crear una Transaction requiere resolver la Category
    // y los Tags contra la base de datos (ownership incluido), algo que solo
    // el TransactionService puede hacer con sus repositories. Mapear eso "a ciegas"
    // en un mapper estático rompería esa validación.
}

