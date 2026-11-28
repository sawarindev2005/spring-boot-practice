# shop-api — ตัวอย่าง REST API ด้วย Spring Boot

ระบบร้านค้าเล็กๆ มี 2 ตาราง: **Category (หมวดหมู่)** และ **Product (สินค้า)**
1 หมวดหมู่มีได้หลายสินค้า (One-to-Many)

โปรเจคนี้ต่อยอดจาก `demo/` (Book) โดยเพิ่มสิ่งที่งานจริงใช้กัน:

| เรื่อง | ใน demo | ใน shop-api |
|---|---|---|
| รับ/ส่งข้อมูล | ใช้ Entity ตรงๆ | ใช้ **DTO** (record) แยกจาก Entity |
| Status code | 200 ทุกอย่าง | **201** สร้าง, **204** ลบ, 400/404/409 |
| ความสัมพันธ์ตาราง | ไม่มี | `@ManyToOne` / `@OneToMany` |
| ดึงข้อมูลเยอะ | findAll ทั้งหมด | **Pagination + ค้นหา + เรียงลำดับ** |
| Error | Map ธรรมดา | JSON รูปแบบเดียวกันทุก error |
| Transaction | ไม่มี | `@Transactional` |
| ข้อมูลตัวอย่าง | ไม่มี | `DataSeeder` ใส่ให้ตอนตารางว่าง |
| Test | ไม่มี | Unit test, Web test, Repository test |

---

## โครงสร้างไฟล์

```
shop-api/
├── pom.xml                         ← รายการ library (dependency)
├── requests.http                   ← ตัวอย่าง request กดยิงได้เลย
└── src/
    ├── main/
    │   ├── java/com/example/shop/
    │   │   ├── ShopApiApplication.java   ← main() จุดเริ่มโปรแกรม
    │   │   ├── controller/   ← รับ HTTP request / ส่ง response (ไม่มี logic)
    │   │   ├── service/      ← กฎทางธุรกิจ + @Transactional
    │   │   ├── repository/   ← คุยกับฐานข้อมูล (JpaRepository)
    │   │   ├── entity/       ← หน้าตาตารางใน DB
    │   │   ├── dto/          ← หน้าตา JSON ที่รับเข้า (Request) / ส่งออก (Response)
    │   │   ├── mapper/       ← แปลง Entity ⇄ DTO
    │   │   ├── exception/    ← exception ของเรา + ตัวแปลงเป็น HTTP error
    │   │   └── config/       ← ตั้งค่า / ข้อมูลตัวอย่าง
    │   └── resources/
    │       └── application.properties
    └── test/java/com/example/shop/
        ├── controller/   ← @WebMvcTest  (ทดสอบ HTTP, mock service)
        ├── service/      ← Mockito      (ทดสอบ logic, mock repository)
        └── repository/   ← @DataJpaTest (ทดสอบ query จริงบน H2)
```

> **หลักการ**: แบ่งชั้นตาม "หน้าที่" แต่ละชั้นคุยกับชั้นถัดไปเท่านั้น
> Controller ห้ามเรียก Repository ตรงๆ, Entity ห้ามหลุดออกไปเป็น JSON

## เส้นทางของ 1 request

```
POST /api/products  {"name":"เมาส์","price":450,"stock":5,"categoryId":2}
        │
        ▼
ProductController.create()     @Valid ตรวจ ProductRequest → ผิด = 400
        │
        ▼
ProductService.create()        หา Category id 2 → ไม่เจอ = 404
        │                      ProductMapper: Request → Entity
        ▼
ProductRepository.save()       INSERT INTO products ...
        │
        ▼
ProductMapper: Entity → ProductResponse
        │
        ▼
201 Created
Location: http://localhost:8081/api/products/8
{"id":8,"name":"เมาส์",...}
```

ถ้ามี exception ตรงไหนก็ตาม → `GlobalExceptionHandler` จับแล้วแปลงเป็น JSON error:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "ไม่พบ หมวดหมู่ id 2",
  "path": "/api/products",
  "timestamp": "2026-11-24T13:00:00Z"
}
```

## Endpoint ทั้งหมด

| Method | URL | สำเร็จ | Error ที่เป็นไปได้ |
|---|---|---|---|
| GET | `/api/categories` | 200 | |
| GET | `/api/categories/{id}` | 200 | 404 |
| GET | `/api/categories/{id}/products` | 200 | 404 |
| POST | `/api/categories` | 201 | 400, 409 ชื่อซ้ำ |
| PUT | `/api/categories/{id}` | 200 | 400, 404, 409 |
| DELETE | `/api/categories/{id}` | 204 | 404, 409 ยังมีสินค้า |
| GET | `/api/products?keyword=&categoryId=&page=&size=&sort=` | 200 | 400 sort ผิด |
| GET | `/api/products/{id}` | 200 | 400, 404 |
| POST | `/api/products` | 201 | 400, 404 ไม่มีหมวด |
| PUT | `/api/products/{id}` | 200 | 400, 404 |
| DELETE | `/api/products/{id}` | 204 | 404 |

### ความหมายของ status code
- **200 OK** — สำเร็จ มีข้อมูลส่งกลับ
- **201 Created** — สร้างใหม่สำเร็จ (บอกที่อยู่ของมันใน header `Location`)
- **204 No Content** — สำเร็จ แต่ไม่มีอะไรส่งกลับ (ใช้กับลบ)
- **400 Bad Request** — client ส่งข้อมูลผิด
- **404 Not Found** — หาไม่เจอ
- **409 Conflict** — ข้อมูลขัดกับสถานะปัจจุบัน (ซ้ำ / ลบไม่ได้)
- **500** — server พังเอง (bug) ไม่ใช่ความผิดของ client

## วิธีรัน

ต้องมี **JDK 21 ขึ้นไป** (Spring Boot 4 ใช้ Java 8 ไม่ได้) และ MySQL เปิดอยู่

1. สร้างไฟล์ `shop-api/.env` (ไฟล์นี้ไม่ขึ้น git):
   ```
   DB_PASSWORD=รหัส_mysql_ของคุณ
   ```
   database `shopdb` จะถูกสร้างให้อัตโนมัติ
2. รัน
   ```bash
   cd shop-api
   ./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
   ```
   หรือกดปุ่ม Run ที่ `ShopApiApplication.java` ใน VS Code
3. เปิด `requests.http` แล้วกด Send Request หรือใช้ Postman ยิงไปที่ `http://localhost:8081/api/products`

รันเทสต์ (ไม่ต้องเปิด MySQL):
```bash
./mvnw test
```

## ลำดับการอ่านโค้ดที่แนะนำ
1. `entity/Category.java`, `entity/Product.java` — ตารางและความสัมพันธ์
2. `dto/` — ทำไมแยก Request/Response
3. `repository/ProductRepository.java` — derived query และ `@Query` + Pageable
4. `service/CategoryService.java` — กฎธุรกิจ, `@Transactional`, dirty checking
5. `controller/ProductController.java` — ResponseEntity, 201 + Location, Pageable
6. `exception/GlobalExceptionHandler.java` — error ทั้งระบบรวมที่เดียว
7. `test/` — ดูว่าแต่ละชั้นถูกทดสอบแยกกันยังไง
