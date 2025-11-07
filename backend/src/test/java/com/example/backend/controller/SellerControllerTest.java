package com.example.backend.controller;

import com.example.backend.dto.SellerSummary;
import com.example.backend.service.SellerNotFoundException;
import com.example.backend.service.SellerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SellerController.class)
class SellerControllerTest {
    @Autowired MockMvc mvc;
    @MockitoBean SellerService service;

    @Test
    void returnsSummaryJson() throws Exception {
        when(service.getSummary(1L)).thenReturn(
                new SellerSummary(12, 340.5, 0.0833, List.of("Return rate above 10%")));

        mvc.perform(get("/api/seller/1/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalSales").value(12))
                .andExpect(jsonPath("$.totalRevenue").value(340.5))
                .andExpect(jsonPath("$.returnRate").value(0.0833))
                .andExpect(jsonPath("$.alerts[0]").value("Return rate above 10%"));
    }

    @Test
    void unknownSellerIs404WithMessage() throws Exception {
        when(service.getSummary(7L)).thenThrow(new SellerNotFoundException(7L));

        mvc.perform(get("/api/seller/7/summary"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Seller 7 not found"));
    }

    @Test
    void nonNumericIdIs400() throws Exception {
        mvc.perform(get("/api/seller/abc/summary"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid value for 'id'"));
    }
}
