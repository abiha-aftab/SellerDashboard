package com.example.backend.controller;

import com.example.backend.dto.SellerSummary;
import com.example.backend.service.SellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/seller")
@CrossOrigin(origins = "http://localhost:5173") // frontend dev server
public class SellerController {
    private final SellerService service;
    public SellerController(SellerService service) { this.service = service; }

    @GetMapping("/{id}/summary")
    public ResponseEntity<?> summary(@PathVariable Long id) {
        try {
            SellerSummary s = service.getSummary(id);
            return ResponseEntity.ok(s);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of(
                "status", 404, "error", "Not Found", "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "status", 500, "error", "Internal Server Error", "message", "An unexpected error occurred"
            ));
        }
    }
}
