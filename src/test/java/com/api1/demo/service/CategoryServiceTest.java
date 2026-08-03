package com.api1.demo.service;

import com.api1.demo.entity.Category;
import com.api1.demo.entity.Transaction;
import com.api1.demo.exception.ResourceNotFoundException;
import com.api1.demo.repository.CategoryRepository;
import com.api1.demo.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getOwned_tiraResourceNotFound_cuandoLaCategoriaNoExisteONoEsDelUsuario() {
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(categoryRepository.findByIdAndUserId(categoryId, userId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getOwned(categoryId, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_tiraIllegalState_cuandoLaCategoriaTieneTransacciones() {
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findByIdAndUserId(categoryId, userId))
                .thenReturn(Optional.of(category));

        // Simula que SÍ hay al menos una transacción asociada a esta categoría
        Page<Transaction> conUnaTransaccion = new PageImpl<>(List.of(new Transaction()));
        when(transactionRepository.findByUserIdAndCategoryIdAndDateBetween(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(conUnaTransaccion);

        assertThatThrownBy(() -> categoryService.delete(categoryId, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("transacciones asociadas");

        // Verificación extra: nunca debería haber llegado a borrarla
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void delete_borraLaCategoria_cuandoNoTieneTransacciones() {
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Category category = new Category();
        category.setId(categoryId);

        when(categoryRepository.findByIdAndUserId(categoryId, userId))
                .thenReturn(Optional.of(category));
        when(transactionRepository.findByUserIdAndCategoryIdAndDateBetween(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        categoryService.delete(categoryId, userId);

        verify(categoryRepository).delete(category);
    }
}