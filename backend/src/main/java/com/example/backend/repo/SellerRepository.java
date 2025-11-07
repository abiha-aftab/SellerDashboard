package com.example.backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.model.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> { }
