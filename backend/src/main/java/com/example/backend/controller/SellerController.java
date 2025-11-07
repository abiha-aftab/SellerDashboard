package com.example.backend.controller;

import com.example.backend.dto.SellerOption;
import com.example.backend.dto.SellerSummary;
import com.example.backend.service.SellerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SellerController {
    private final SellerService service;

    public SellerController(SellerService service) { this.service = service; }

    @GetMapping("/sellers")
    public List<SellerOption> sellers() {
        return service.listSellers();
    }

    @GetMapping("/seller/{id}/summary")
    public SellerSummary summary(@PathVariable Long id) {
        return service.getSummary(id);
    }
}
