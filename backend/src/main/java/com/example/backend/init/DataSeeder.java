package com.example.backend.init;

import com.example.backend.model.Sale;
import com.example.backend.model.Seller;
import com.example.backend.repo.SaleRepository;
import com.example.backend.repo.SellerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeds 30 days of demo sales. Each seller gets a profile so the dashboard shows
 * every state: a steady seller, one whose sales collapsed this week, and one
 * with a high return rate. The fixed seed keeps the numbers reproducible.
 */
@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true", matchIfMissing = true)
public class DataSeeder implements CommandLineRunner {
    private record Profile(String name, String region, double thisWeekVolume, double returnChance) {}

    private static final List<Profile> PROFILES = List.of(
            new Profile("TechZone", "North America", 1.0, 0.05),
            new Profile("GadgetHub", "Europe", 0.4, 0.06),
            new Profile("EcoMart", "Asia", 1.0, 0.22));

    private final SellerRepository sellerRepo;
    private final SaleRepository saleRepo;
    private final Clock clock;

    public DataSeeder(SellerRepository sellerRepo, SaleRepository saleRepo, Clock clock) {
        this.sellerRepo = sellerRepo;
        this.saleRepo = saleRepo;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (sellerRepo.count() > 0) return;

        Random rnd = new Random(42);
        LocalDate today = LocalDate.now(clock);
        List<Sale> sales = new ArrayList<>();
        for (Profile p : PROFILES) {
            Seller seller = sellerRepo.save(new Seller(null, p.name(), p.region()));
            for (int daysAgo = 0; daysAgo < 30; daysAgo++) {
                double volume = daysAgo < 7 ? p.thisWeekVolume() : 1.0;
                int orders = (int) Math.round((3 + rnd.nextInt(3)) * volume);
                for (int i = 0; i < orders; i++) {
                    int qty = rnd.nextInt(5) + 1;
                    double price = Math.round((20 + rnd.nextDouble() * 100) * 100) / 100.0;
                    boolean returned = rnd.nextDouble() < p.returnChance();
                    sales.add(new Sale(null, seller, today.minusDays(daysAgo), qty, price, returned));
                }
            }
        }
        saleRepo.saveAll(sales);
    }
}
