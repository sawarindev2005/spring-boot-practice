package com.example.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * จุดเริ่มต้นของโปรแกรม
 * @SpringBootApplication จะสแกนทุก class ที่อยู่ใน package com.example.shop (และ package ย่อย)
 * → นี่คือเหตุผลที่ไฟล์นี้ต้องอยู่ "บนสุด" ของ package เสมอ
 */
@SpringBootApplication
public class ShopApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApiApplication.class, args);
    }
}
