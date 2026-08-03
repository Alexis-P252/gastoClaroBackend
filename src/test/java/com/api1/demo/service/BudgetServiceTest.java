package com.api1.demo.service;

import com.api1.demo.entity.Budget;
import com.api1.demo.entity.Category;
import com.api1.demo.entity.Transaction;
import com.api1.demo.entity.User;
import com.api1.demo.exception.BudgetExceededException;
import com.api1.demo.repository.BudgetRepository;
import com.api1.demo.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    // Mockito crea un BudgetService real, pero le "inyecta" los mocks de arriba
    @InjectMocks
    private BudgetService budgetService;

    private User user;
    private Category category;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());

        categoryId = UUID.randomUUID();
        category = new Category();
        category.setId(categoryId);
        category.setName("Alimentación");

        // Común a casi todos los tests de esta clase: la categoría existe y es del usuario.
        // "lenient" porque el test del monto inválido corta ANTES de llegar a usar
        // este mock — sin lenient, Mockito tira UnnecessaryStubbingException en ese caso.
        lenient().when(categoryService.getOwned(categoryId, user.getId())).thenReturn(category);
    }

    @Test
    void setBudget_creaUnPresupuestoNuevo_cuandoNoExisteTodavia() {
        // Arrange: no hay presupuesto previo, y no hay transacciones gastadas todavía
        when(budgetRepository.findByUserIdAndCategoryIdAndMonth(user.getId(), categoryId, "2026-07"))
                .thenReturn(Optional.empty());
        when(transactionRepository.findByUserIdAndCategoryIdAndDateBetween(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        Budget result = budgetService.setBudget(user, categoryId, new BigDecimal("500"), "2026-07");

        assertThat(result.getMonthlyLimit()).isEqualByComparingTo("500");
        assertThat(result.getMonth()).isEqualTo("2026-07");
        assertThat(result.getCategory()).isEqualTo(category);
    }

    @Test
    void setBudget_tiraExcepcion_cuandoElLimiteEsMenorALoYaGastado() {
        // Arrange: ya se gastaron 600 este mes en la categoría, se intenta poner un límite de 500
        Transaction gastoExistente = new Transaction();
        gastoExistente.setAmount(new BigDecimal("600"));
        gastoExistente.setType("EXPENSE");

        when(transactionRepository.findByUserIdAndCategoryIdAndDateBetween(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(gastoExistente)));

        assertThatThrownBy(() ->
                budgetService.setBudget(user, categoryId, new BigDecimal("500"), "2026-07")
        ).isInstanceOf(BudgetExceededException.class)
                .hasMessageContaining("500");
    }

    @Test
    void setBudget_tiraExcepcion_cuandoElLimiteEsCeroONegativo() {
        assertThatThrownBy(() ->
                budgetService.setBudget(user, categoryId, BigDecimal.ZERO, "2026-07")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void setBudget_actualizaElPresupuestoExistente_enVezDeCrearUnoNuevo() {
        Budget existente = new Budget();
        existente.setId(UUID.randomUUID());

        when(budgetRepository.findByUserIdAndCategoryIdAndMonth(user.getId(), categoryId, "2026-07"))
                .thenReturn(Optional.of(existente));
        when(transactionRepository.findByUserIdAndCategoryIdAndDateBetween(
                any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(budgetRepository.save(any(Budget.class))).thenAnswer(inv -> inv.getArgument(0));

        Budget result = budgetService.setBudget(user, categoryId, new BigDecimal("800"), "2026-07");

        // Mismo id que el existente: confirma que actualizó, no que creó uno nuevo
        assertThat(result.getId()).isEqualTo(existente.getId());
        assertThat(result.getMonthlyLimit()).isEqualByComparingTo("800");
    }
}