package com.example.backend.init;

import com.example.backend.model.Seller;
import com.example.backend.model.Sale;
import com.example.backend.repo.SaleRepository;
import com.example.backend.repo.SellerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
public class DataSeeder implements CommandLineRunner {
    private final SellerRepository sellerRepo;
    private final SaleRepository saleRepo;
    private final Random rnd = new Random();

    public DataSeeder(SellerRepository sellerRepo, SaleRepository saleRepo) {
        this.sellerRepo = sellerRepo;
        this.saleRepo = saleRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (sellerRepo.count() > 0) return;

        Seller s1 = sellerRepo.save(new Seller(null, "TechZone", "North America"));
        Seller s2 = sellerRepo.save(new Seller(null, "GadgetHub", "Europe"));
        Seller s3 = sellerRepo.save(new Seller(null, "EcoMart", "Asia"));

        // ~100 rows per seller, last 30 days, ~10% returns
        for (Seller s : new Seller[]{s1, s2, s3}) {
            for (int i = 0; i < 100; i++) {
                LocalDate d = LocalDate.now().minusDays(rnd.nextInt(30));
                int qty = rnd.nextInt(5) + 1;
                double price = 20 + rnd.nextDouble() * 100;
                boolean returned = rnd.nextDouble() < 0.10;
                Sale sale = new Sale(null, s, d, qty, price, returned);
                saleRepo.save(sale);
            }
        }
    }
}
