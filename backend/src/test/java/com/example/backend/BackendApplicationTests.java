package com.example.backend;

import com.example.backend.model.Seller;
import com.example.backend.repo.SellerRepository;
import com.example.backend.service.SellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Boots the app with the demo seeder and checks each demo seller shows its intended state. */
@SpringBootTest
class BackendApplicationTests {
    @Autowired SellerService service;
    @Autowired SellerRepository sellers;

    private List<String> alertsFor(String name) {
        Seller seller = sellers.findAll().stream().filter(s -> s.getName().equals(name)).findFirst().orElseThrow();
        return service.getSummary(seller.getId()).getAlerts();
    }

    @Test
    void demoDataShowsEveryAlertState() {
        assertThat(sellers.count()).isEqualTo(3);
        assertThat(alertsFor("TechZone")).isEmpty();
        assertThat(alertsFor("GadgetHub")).containsExactly("Sales dropped by more than 30% vs last week");
        assertThat(alertsFor("EcoMart")).containsExactly("Return rate above 10%");
    }
}
