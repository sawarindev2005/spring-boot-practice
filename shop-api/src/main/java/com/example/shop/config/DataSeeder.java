package com.example.shop.config;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.shop.entity.Category;
import com.example.shop.entity.Product;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;

/**
 * CommandLineRunner = โค้ดที่ Spring รันให้ 1 ครั้งหลังแอปสตาร์ทเสร็จ
 * ใช้ใส่ข้อมูลตัวอย่าง (เฉพาะตอนที่ตารางยังว่าง จะได้ไม่ใส่ซ้ำทุกครั้งที่รัน)
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataSeeder(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category clothes = categoryRepository.save(new Category("เสื้อผ้า", "เสื้อ กางเกง และเครื่องแต่งกาย"));
        Category gadget = categoryRepository.save(new Category("อุปกรณ์ไอที", "คีย์บอร์ด เมาส์ หูฟัง"));
        Category books = categoryRepository.save(new Category("หนังสือ", "หนังสือเรียนและนิยาย"));

        productRepository.save(new Product("เสื้อยืดสีขาว", new BigDecimal("199.00"), 50, clothes));
        productRepository.save(new Product("กางเกงยีนส์", new BigDecimal("890.00"), 20, clothes));
        productRepository.save(new Product("คีย์บอร์ด Mechanical", new BigDecimal("1590.00"), 15, gadget));
        productRepository.save(new Product("เมาส์ไร้สาย", new BigDecimal("450.00"), 40, gadget));
        productRepository.save(new Product("หูฟังบลูทูธ", new BigDecimal("1290.50"), 10, gadget));
        productRepository.save(new Product("Java เบื้องต้น", new BigDecimal("299.00"), 30, books));
        productRepository.save(new Product("Spring Boot ฉบับลงมือทำ", new BigDecimal("450.00"), 25, books));

        log.info("ใส่ข้อมูลตัวอย่างแล้ว: 3 หมวดหมู่, 7 สินค้า");
    }
}
