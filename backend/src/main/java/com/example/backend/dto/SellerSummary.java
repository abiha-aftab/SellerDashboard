package com.example.backend.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerSummary {
    private long totalSales;     // units this week
    private double totalRevenue; // revenue this week
    private double returnRate;   // returns / units (0..1)
    private List<String> alerts;
}
