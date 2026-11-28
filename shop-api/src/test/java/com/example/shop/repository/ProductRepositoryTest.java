package com.example.shop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.shop.entity.Category;
import com.example.shop.entity.Product;

/**
 * @DataJpaTest = เปิดเฉพาะชั้น JPA/Repository แล้วใช้ฐานข้อมูล H2 ในหน่วยความจำแทน MySQL
 * ทุกเทสต์ rollback อัตโนมัติ → ข้อมูลไม่ค้างข้ามเทสต์
 * ใช้ตรวจว่า query ที่เราเขียนเอง (@Query) ทำงานถูกจริงกับ SQL
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Category gadget;

    @BeforeEach
    void setUp() {
        Category clothes = categoryRepository.save(new Category("เสื้อผ้า", null));
        gadget = categoryRepository.save(new Category("อุปกรณ์ไอที", null));

        productRepository.save(new Product("เสื้อยืด", new BigDecimal("199.00"), 10, clothes));
        productRepository.save(new Product("Mouse ไร้สาย", new BigDecimal("450.00"), 10, gadget));
        productRepository.save(new Product("Mechanical Keyboard", new BigDecimal("1590.00"), 5, gadget));
    }

    @Test
    void search_withoutFilters_returnsAll() {
        Page<Product> page = productRepository.search(null, null, PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(3);
    }

    @Test
    void search_byKeyword_isCaseInsensitive() {
        Page<Product> page = productRepository.search("mouse", null, PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(Product::getName).containsExactly("Mouse ไร้สาย");
    }

    @Test
    void search_byCategory_sortedByPriceDesc() {
        Page<Product> page = productRepository.search(null, gadget.getId(),
                PageRequest.of(0, 10, Sort.by("price").descending()));

        assertThat(page.getContent()).extracting(Product::getName)
                .containsExactly("Mechanical Keyboard", "Mouse ไร้สาย");
    }

    @Test
    void search_paginates() {
        Page<Product> page = productRepository.search(null, null, PageRequest.of(1, 2, Sort.by("id")));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isLast()).isTrue();
    }

    @Test
    void existsByCategoryId_and_existsByNameIgnoreCase() {
        assertThat(productRepository.existsByCategoryId(gadget.getId())).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCase("อุปกรณ์ไอที")).isTrue();
        assertThat(categoryRepository.existsByNameIgnoreCaseAndIdNot("อุปกรณ์ไอที", gadget.getId())).isFalse();
    }
}
