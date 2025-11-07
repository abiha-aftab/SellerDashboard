package com.example.backend.repo;

import com.example.backend.model.Sale;
import com.example.backend.model.Seller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "app.demo-data.enabled=false")
class SaleRepositoryTest {
    @Autowired SellerRepository sellers;
    @Autowired SaleRepository sales;

    @Test
    void findsOnlyThatSellersSalesWithinInclusiveRange() {
        Seller a = sellers.save(new Seller(null, "A", "EU"));
        Seller b = sellers.save(new Seller(null, "B", "EU"));
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 14);
        sales.save(new Sale(null, a, from.minusDays(1), 1, 1.0, false));
        sales.save(new Sale(null, a, from, 1, 1.0, false));
        sales.save(new Sale(null, a, to, 1, 1.0, false));
        sales.save(new Sale(null, a, to.plusDays(1), 1, 1.0, false));
        sales.save(new Sale(null, b, from, 1, 1.0, false));

        assertThat(sales.findBySellerIdAndDateBetween(a.getId(), from, to))
                .extracting(Sale::getDate)
                .containsExactlyInAnyOrder(from, to);
    }
}
