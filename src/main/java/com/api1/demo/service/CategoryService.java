package com.api1.demo.service;


import com.api1.demo.entity.Category;
import com.api1.demo.entity.User;
import com.api1.demo.exception.ResourceNotFoundException;
import com.api1.demo.repository.CategoryRepository;
import com.api1.demo.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<Category> listForUser(UUID userId) {
        return categoryRepository.findByUserId(userId);
    }

    public Category create(User user, String name, String type) {
        Category category = new Category();
        category.setUser(user);
        category.setName(name);
        category.setType(type);
        return categoryRepository.save(category);
    }

    public Category getOwned(UUID categoryId, UUID userId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
    }

    public Category update(UUID categoryId, UUID userId, String name, String type) {
        Category category = getOwned(categoryId, userId);
        category.setName(name);
        category.setType(type);
        return categoryRepository.save(category);
    }

    public void delete(UUID categoryId, UUID userId) {
        Category category = getOwned(categoryId, userId);

        // Regla de negocio: no se borra una categoría que ya tiene movimientos.
        // Evita romper el historial y transacciones "huérfanas".
        boolean hasTransactions = transactionRepository
                .findByUserIdAndCategoryIdAndDateBetween(
                        userId, categoryId,
                        java.time.LocalDate.MIN, java.time.LocalDate.MAX,
                        org.springframework.data.domain.Pageable.unpaged())
                .hasContent();

        if (hasTransactions) {
            throw new IllegalStateException(
                    "No se puede eliminar una categoría con transacciones asociadas");
        }

        categoryRepository.delete(category);
    }
}