package com.api1.demo.repository;

import com.api1.demo.entity.Category;
import com.api1.demo.entity.Transaction;
import com.api1.demo.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace.NONE;

// @DataJpaTest normalmente reemplaza tu base real por una en memoria (H2).
// Con Replace.NONE le decimos "no reemplaces nada, dejá que use el datasource
// real" — que en este caso apunta al Postgres que levanta Testcontainers.
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@Testcontainers
class TransactionRepositoryIT {

    // WORKAROUND: Docker 29+ cambió su API interna y rompió la compatibilidad
    // con Testcontainers 1.x (bug conocido, sin fix en esa rama todavía).
    // Forzamos a que el cliente hable una versión de API vieja y compatible.
    // Tiene que ejecutarse ANTES de que el campo @Container de más abajo
    // intente conectarse, por eso va como bloque static, primero en la clase.
    static {
        System.setProperty("api.version", "1.44");
    }

    // Postgres real, corriendo en un contenedor Docker efímero que se crea
    // al arrancar el test y se destruye al terminar. No es H2 disfrazado.
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Category category;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@gastoclaro.com");
        user.setPasswordHash("hash-de-prueba");
        user = userRepository.save(user);

        category = new Category();
        category.setUser(user);
        category.setName("Alimentación");
        category.setType("EXPENSE");
        category = categoryRepository.save(category);
    }

    @Test
    void findByUserIdAndDateBetween_devuelveSoloLasTransaccionesDentroDelRango() {
        guardarTransaccion(new BigDecimal("100"), LocalDate.of(2026, 7, 15));
        guardarTransaccion(new BigDecimal("200"), LocalDate.of(2026, 7, 20));
        guardarTransaccion(new BigDecimal("300"), LocalDate.of(2026, 8, 5)); // fuera de rango

        Page<Transaction> result = transactionRepository.findByUserIdAndDateBetween(
                user.getId(),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31),
                Pageable.unpaged());

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(Transaction::getAmount)
                .containsExactlyInAnyOrder(new BigDecimal("100"), new BigDecimal("200"));
    }

    @Test
    void findByUserIdAndRecurringTrue_devuelveSoloLasMarcadasComoRecurrentes() {
        Transaction fija = guardarTransaccion(new BigDecimal("50"), LocalDate.of(2026, 7, 1));
        fija.setRecurring(true);
        transactionRepository.save(fija);

        guardarTransaccion(new BigDecimal("15"), LocalDate.of(2026, 7, 10)); // no recurrente

        var recurrentes = transactionRepository.findByUserIdAndRecurringTrue(user.getId());

        assertThat(recurrentes).hasSize(1);
        assertThat(recurrentes.get(0).getAmount()).isEqualByComparingTo("50");
    }

    private Transaction guardarTransaccion(BigDecimal amount, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setDate(date);
        transaction.setType("EXPENSE");
        transaction.setRecurring(false);
        return transactionRepository.save(transaction);
    }
}