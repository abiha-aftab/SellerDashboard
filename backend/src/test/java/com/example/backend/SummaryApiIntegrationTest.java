package com.example.backend;

import com.example.backend.model.Sale;
import com.example.backend.model.Seller;
import com.example.backend.repo.SaleRepository;
import com.example.backend.repo.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.demo-data.enabled=false",
        "spring.datasource.url=jdbc:h2:mem:summary-api-test;DB_CLOSE_DELAY=-1"})
@AutoConfigureMockMvc
class SummaryApiIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired SellerRepository sellers;
    @Autowired SaleRepository sales;
    @Autowired CacheManager cacheManager;

    private Seller seller;

    @BeforeEach
    void setUp() {
        sales.deleteAll();
        sellers.deleteAll();
        cacheManager.getCache("sellerSummary").clear();
        seller = sellers.save(new Seller(null, "Test Seller", "EU"));
        LocalDate today = LocalDate.now();
        sales.save(new Sale(null, seller, today, 2, 25.0, false));
        sales.save(new Sale(null, seller, today.minusDays(8), 4, 25.0, false));
    }

    @Test
    void summaryIsComputedFromTheDatabase() throws Exception {
        mvc.perform(get("/api/seller/{id}/summary", seller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSales").value(2))
                .andExpect(jsonPath("$.totalRevenue").value(50.0))
                .andExpect(jsonPath("$.alerts[0]").value("Sales dropped by more than 30% vs last week"));
    }

    @Test
    void summaryIsServedFromCacheUntilItExpires() throws Exception {
        String url = "/api/seller/" + seller.getId() + "/summary";
        mvc.perform(get(url)).andExpect(jsonPath("$.totalSales").value(2));

        sales.save(new Sale(null, seller, LocalDate.now(), 10, 1.0, false));

        mvc.perform(get(url)).andExpect(jsonPath("$.totalSales").value(2));
        cacheManager.getCache("sellerSummary").clear();
        mvc.perform(get(url)).andExpect(jsonPath("$.totalSales").value(12));
    }

    @Test
    void missingSellerIs404() throws Exception {
        mvc.perform(get("/api/seller/{id}/summary", seller.getId() + 100))
                .andExpect(status().isNotFound());
    }

    @Test
    void devServerOriginIsAllowedByCors() throws Exception {
        mvc.perform(get("/api/seller/{id}/summary", seller.getId()).header("Origin", "http://localhost:5173"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
        mvc.perform(get("/api/seller/{id}/summary", seller.getId()).header("Origin", "http://evil.example"))
                .andExpect(status().isForbidden());
    }
}
