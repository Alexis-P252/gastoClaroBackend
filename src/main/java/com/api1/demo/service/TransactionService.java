package com.api1.demo.service;

import com.api1.demo.entity.Category;
import com.api1.demo.entity.Tag;
import com.api1.demo.entity.Transaction;
import com.api1.demo.entity.User;
import com.api1.demo.exception.ResourceNotFoundException;
import com.api1.demo.repository.TagRepository;
import com.api1.demo.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TagRepository tagRepository;
    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepository,
                              TagRepository tagRepository,
                              CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.tagRepository = tagRepository;
        this.categoryService = categoryService;
    }

    public Transaction create(User user, UUID categoryId, BigDecimal amount,
                              String description, LocalDate date, String type,
                              boolean recurring, List<UUID> tagIds) {

        // La categoría tiene que existir y ser del mismo usuario (no de otro)
        Category category = categoryService.getOwned(categoryId, user.getId());

        validateAmount(amount);
        validateDateNotFuture(date);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setDate(date);
        transaction.setType(type);
        transaction.setRecurring(recurring);
        transaction.setTags(resolveTags(user.getId(), tagIds));

        return transactionRepository.save(transaction);
    }

    public Page<Transaction> list(UUID userId, UUID categoryId,
                                  LocalDate from, LocalDate to, Pageable pageable) {
        if (categoryId != null) {
            return transactionRepository
                    .findByUserIdAndCategoryIdAndDateBetween(userId, categoryId, from, to, pageable);
        }
        return transactionRepository.findByUserIdAndDateBetween(userId, from, to, pageable);
    }

    public Transaction getOwned(UUID transactionId, UUID userId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
    }

    public void delete(UUID transactionId, UUID userId) {
        Transaction transaction = getOwned(transactionId, userId);
        transactionRepository.delete(transaction);
    }

    // --- Reglas de negocio propias de la transacción ---

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
    }

    private void validateDateNotFuture(LocalDate date) {
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura");
        }
    }

    private Set<Tag> resolveTags(UUID userId, List<UUID> tagIds) {
        Set<Tag> tags = new HashSet<>();
        if (tagIds == null) {
            return tags;
        }
        for (UUID tagId : tagIds) {
            Tag tag = tagRepository.findByIdAndUserId(tagId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Tag no encontrado: " + tagId));
            tags.add(tag);
        }
        return tags;
    }
}