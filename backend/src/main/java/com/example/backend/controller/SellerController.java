package com.example.backend.controller;

import com.example.backend.dto.SellerSummary;
import com.example.backend.service.SellerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seller")
public class SellerController {
    private final SellerService service;

    public SellerController(SellerService service) { this.service = service; }

    @GetMapping("/{id}/summary")
    public SellerSummary summary(@PathVariable Long id) {
        return service.getSummary(id);
    }
}
