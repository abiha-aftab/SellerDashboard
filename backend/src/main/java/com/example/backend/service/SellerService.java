package com.example.backend.service;

import com.example.backend.dto.SellerSummary;
import com.example.backend.model.Sale;
import com.example.backend.repo.SaleRepository;
import com.example.backend.repo.SellerRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class SellerService {
    static final String SALES_DROP_ALERT = "Sales dropped by more than 30% vs last week";
    static final String RETURN_RATE_ALERT = "Return rate above 10%";

    private static final double SALES_DROP_THRESHOLD = 0.7;
    private static final double RETURN_RATE_THRESHOLD = 0.10;

    private final SaleRepository saleRepo;
    private final SellerRepository sellerRepo;
    private final Clock clock;

    public SellerService(SaleRepository saleRepo, SellerRepository sellerRepo, Clock clock) {
        this.saleRepo = saleRepo;
        this.sellerRepo = sellerRepo;
        this.clock = clock;
    }

    /**
     * "This week" is the last 7 days including today and "last week" the 7 days before it,
     * so the comparison is stable on any weekday instead of resetting every Monday.
     * Only those 14 days are loaded; the result is cached for 30s (see CacheConfig).
     */
    @Cacheable(value = "sellerSummary", key = "#sellerId")
    @Transactional(readOnly = true)
    public SellerSummary getSummary(Long sellerId) {
        if (!sellerRepo.existsById(sellerId)) {
            throw new SellerNotFoundException(sellerId);
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate thisWeekStart = today.minusDays(6);
        LocalDate lastWeekStart = today.minusDays(13);

        long thisWeekUnits = 0;
        long thisWeekReturnedUnits = 0;
        double thisWeekRevenue = 0.0;
        double lastWeekRevenue = 0.0;

        for (Sale s : saleRepo.findBySellerIdAndDateBetween(sellerId, lastWeekStart, today)) {
            double revenue = s.getQuantity() * s.getPrice();
            if (s.getDate().isBefore(thisWeekStart)) {
                lastWeekRevenue += revenue;
            } else {
                thisWeekUnits += s.getQuantity();
                thisWeekRevenue += revenue;
                if (s.isReturned()) thisWeekReturnedUnits += s.getQuantity();
            }
        }

        double returnRate = thisWeekUnits == 0 ? 0.0 : (double) thisWeekReturnedUnits / thisWeekUnits;
        List<String> alerts = new ArrayList<>();
        if (lastWeekRevenue > 0 && thisWeekRevenue < lastWeekRevenue * SALES_DROP_THRESHOLD) {
            alerts.add(SALES_DROP_ALERT);
        }
        if (returnRate > RETURN_RATE_THRESHOLD) {
            alerts.add(RETURN_RATE_ALERT);
        }

        return new SellerSummary(thisWeekUnits, round(thisWeekRevenue, 2), round(returnRate, 4), alerts);
    }

    private static double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
