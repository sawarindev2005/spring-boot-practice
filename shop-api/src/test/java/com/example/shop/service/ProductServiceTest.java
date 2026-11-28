package com.example.shop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.shop.dto.ProductRequest;
import com.example.shop.dto.ProductResponse;
import com.example.shop.entity.Category;
import com.example.shop.entity.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.ResourceNotFoundException;
import com.example.shop.repository.ProductRepository;

/**
 * Unit test ของ Service: ไม่เปิด Spring ไม่ต่อ DB
 * ใช้ Mockito สร้าง "ของปลอม" (mock) แทน Repository แล้วกำหนดว่าจะให้ตอบอะไร
 * → เทสต์เร็วมาก และทดสอบเฉพาะ logic ใน Service จริงๆ
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    @Test
    void getById_whenNotFound_throwsResourceNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_linksProductToCategory() {
        Category category = new Category("อุปกรณ์ไอที", null);
        when(categoryService.findCategory(1L)).thenReturn(category);
        // save() คืน object เดิมที่ส่งเข้าไป
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductRequest request = new ProductRequest("  เมาส์  ", new BigDecimal("450.00"), 5, 1L);
        ProductResponse result = productService.create(request);

        assertThat(result.name()).isEqualTo("เมาส์"); // ถูก trim แล้ว
        assertThat(result.categoryName()).isEqualTo("อุปกรณ์ไอที");
        assertThat(result.price()).isEqualByComparingTo("450");
    }

    @Test
    void search_withUnknownSortField_throwsBadRequest() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("password"));

        assertThatThrownBy(() -> productService.search(null, null, pageable))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void create_whenCategoryMissing_doesNotSave() {
        when(categoryService.findCategory(7L)).thenThrow(new ResourceNotFoundException("หมวดหมู่", 7L));

        ProductRequest request = new ProductRequest("เมาส์", new BigDecimal("450.00"), 5, 7L);

        assertThatThrownBy(() -> productService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(productRepository, never()).save(any());
    }
}
