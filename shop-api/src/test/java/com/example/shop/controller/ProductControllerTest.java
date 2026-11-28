package com.example.shop.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.shop.dto.ProductRequest;
import com.example.shop.dto.ProductResponse;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.service.ProductService;

/**
 * @WebMvcTest = เปิดเฉพาะชั้น Web (Controller + ExceptionHandler + JSON) ไม่ต่อ DB
 * Service ถูกแทนด้วย mock → ทดสอบว่า Controller ตอบ status / JSON ถูกต้องไหม
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getById_returns200AndJson() throws Exception {
        when(productService.getById(1L)).thenReturn(
                new ProductResponse(1L, "เมาส์", new BigDecimal("450.00"), 5, 2L, "อุปกรณ์ไอที"));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("เมาส์"))
                .andExpect(jsonPath("$.categoryName").value("อุปกรณ์ไอที"));
    }

    @Test
    void getById_whenMissing_returns404WithErrorBody() throws Exception {
        when(productService.getById(99L)).thenThrow(new ResourceNotFoundException("สินค้า", 99L));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("ไม่พบ สินค้า id 99"))
                .andExpect(jsonPath("$.path").value("/api/products/99"));
    }

    @Test
    void getById_withNonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/api/products/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns201WithLocationHeader() throws Exception {
        when(productService.create(any(ProductRequest.class))).thenReturn(
                new ProductResponse(10L, "เมาส์", new BigDecimal("450.00"), 5, 2L, "อุปกรณ์ไอที"));

        String body = """
                { "name": "เมาส์", "price": 450.00, "stock": 5, "categoryId": 2 }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/products/10")))
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void create_withInvalidBody_returns400WithFieldErrors() throws Exception {
        String body = """
                { "name": "", "price": -5, "stock": -1 }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.price").exists())
                .andExpect(jsonPath("$.fieldErrors.stock").exists())
                .andExpect(jsonPath("$.fieldErrors.categoryId").exists());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
