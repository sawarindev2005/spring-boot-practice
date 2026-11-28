package com.example.shop.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Entity = หน้าตาของ "ตาราง" ในฐานข้อมูล (1 object = 1 แถว)
 * Entity ใช้คุยกับ DB เท่านั้น → ไม่ส่งออกไปเป็น JSON ตรงๆ (ใช้ DTO แทน ดูใน package dto)
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    // 1 หมวดหมู่ มีได้หลายสินค้า (One-to-Many)
    // mappedBy = "category" → บอกว่า "เจ้าของความสัมพันธ์" คือฟิลด์ category ในคลาส Product
    // (foreign key category_id อยู่ที่ตาราง products ไม่ใช่ตารางนี้)
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    protected Category() {
        // JPA ต้องการ constructor ว่าง
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }
}
