package com.example.backend.service;

import com.example.backend.dto.SellerSummary;
import com.example.backend.model.Sale;
import com.example.backend.repo.SaleRepository;
import com.example.backend.repo.SellerRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class SellerService {
    private final SaleRepository saleRepo;
    private final SellerRepository sellerRepo;

    public SellerService(SaleRepository saleRepo, SellerRepository sellerRepo) {
        this.saleRepo = saleRepo;
        this.sellerRepo = sellerRepo;
    }

    /**
     * Returns seller summary. Cached for 30s by Caffeine.
     * Implementation note: we fetch sales once and compute both this-week
     * and last-week metrics in a single traversal (single-pass O(n)).
     */
    @Cacheable(value = "sellerSummary", key = "#sellerId")
    @Transactional(readOnly = true)
    public SellerSummary getSummary(Long sellerId) {
        var sellerOpt = sellerRepo.findById(sellerId);
        if (sellerOpt.isEmpty()) {
            throw new NoSuchElementException("Seller not found");
        }

        LocalDate today = LocalDate.now();
        LocalDate thisWeekStart = today.minusDays(6);    // last 7 days inclusive
        LocalDate lastWeekStart = today.minusDays(13);
        LocalDate lastWeekEnd = today.minusDays(7);

        List<Sale> sales = saleRepo.findBySellerId(sellerId);

        long thisWeekUnits = 0;
        double thisWeekRevenue = 0.0;
        long thisWeekReturns = 0;

        long lastWeekUnits = 0;
        double lastWeekRevenue = 0.0;

        for (Sale s : sales) {
            LocalDate d = s.getDate();
            long units = s.getQuantity();
            double revenue = units * s.getPrice();
            boolean returned = s.isReturned();

            if (!d.isBefore(thisWeekStart) && !d.isAfter(today)) {
                thisWeekUnits += units;
                thisWeekRevenue += revenue;
                if (returned) thisWeekReturns += units;
            } else if (!d.isBefore(lastWeekStart) && !d.isAfter(lastWeekEnd)) {
                lastWeekUnits += units;
                lastWeekRevenue += revenue;
            }
        }

        double returnRate = thisWeekUnits == 0 ? 0.0 : ((double) thisWeekReturns / thisWeekUnits);
        List<String> alerts = new ArrayList<>();

        // Alert A: sales dropped by >30% vs last week (use revenue comparison)
        if (lastWeekRevenue > 0 && thisWeekRevenue < lastWeekRevenue * 0.7) {
            alerts.add("Sales dropped by more than 30% vs last week");
        }
        // Alert B: return rate above 10%
        if (thisWeekUnits > 0 && returnRate > 0.10) {
            alerts.add("Return rate above 10%");
        }

        return new SellerSummary(thisWeekUnits, thisWeekRevenue, returnRate, alerts);
    }
}
