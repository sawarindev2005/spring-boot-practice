package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    /*ตอนสตาร์ท Spring จะ เขียนโค้ดตัวจริงให้เอง แล้วเราจะได้ method พวกนี้ใช้ฟรี ไม่ต้องเขียน SQL สักบรรทัด

    method	ทำอะไร	SQL ที่ Spring สร้างให้
    findAll()	ดึงทุกแถว	SELECT * FROM book
    findById(id)	ดึงตาม id	SELECT ... WHERE id = ?
    save(book)	เพิ่ม (หรือแก้)	INSERT INTO book ...
    deleteById(id)	ลบตาม id	DELETE FROM book WHERE id = ? */
}
