* * Technical Stacks:
- Java 17
- Maven 3.5+
- Spring Boot 3.2.3
- Spring Data Validation
- Spring Data JPA
- Postgres
- Lombok
- DevTools
- Docker, Docker compose 
---
* * Tong Quan Du An:
Du an nay duoc xay dung theo mo hinh REST API voi cau truc theo layers:
- Controller: Xu ly cac request HTTP va tra ve response
- Service: Chua logic xu ly business cua ung dung
- Repository: Giao tiep voi co so du lieu
- Model: Cac lop entity JPA dai dien cho cac bang du lieu
- DTO: Cac lop dung de truyen du lieu giua client va server
- Configuration: Cac cau hinh ung dung nhu Swagger, Translator, AppConfig
- Exception: Xu ly ngoai le tong hop
- Util: Chua cac Enum va cac ham tien ich

**Cac Tinh Nang Chinh:**
* User Management:
    - Tao, cap nhat, xoa, tim kiem nguoi dung
    - Ho tro phan trang va tim kiem nang cao
    - Quan ly dia chi nguoi dung
    - Quan ly trang thai va loai nguoi dung
    - Ho tro gioi tinh va ngay sinh
* Send Email:
    - Dung dich vu Google SMTP de gui email
    - Ho tro gui email toi da nguoi dung
  Instruction:
    - Step.1: Xac thuc 2 buoc: https://myaccount.google.com/signinoptions/two-step-verification/enroll-welcome
    - Step.2: Tao app chi dinh password: https://myaccount.google.com/apppasswords
    - Step.3: Gan thong tin vao mail sender.
  Properties
    - spring.mail.username=minhtantranle.dev
    - spring.mail.password=iatjblgudkrvgtnu
* Swagger API:
    - Tich hop Swagger/OpenAPI de tu dong sinh API Document
    - Co the truy cap qua endpoint /swagger-ui.html
* Connect DB:
    - Su dung PostgreSQL lam CSDL chinh
    - JPA Hibernate de orm va giao tiep voi database
    - Ho tro transaction va data validation
* Caching va Performance:
    - Su dung Redis de cache du lieu
    - Jedis client cho ket noi Redis
* Moi Truong:
    - Ho tro 3 moi truong: dev (phat trien), test (kiem thu), prod (san xuat)
    - Docker va Docker Compose de container hoa ung dung
    - Tuy chinh cac cau hinh theo moi truong
* Validation:
    - Global exception handler xu ly loi tong the
    - Spring Validation de kiem thu du lieu
    - Translator de ho tro da ngon ngu
* Kafka 
   - Tong Quan: Apache Kafka la mot nen tang message streaming phan tan, phep va co the cap nhat real-time. Trong du an nay, Kafka duoc su dung de quan ly viec gui email xac nhan tai khoan theo asynchronous.
  Kien Truc va Thanh Phan:
   - Producer (Nguoi San Xuat): Thanh phan chiu trach nhiem ghi du lieu vao Kafka.
   - Consumer (Nguoi Tieu Thu): Thanh phan doc du lieu tu cac topic va xu ly theo yeu cau ung dung.
   - Broker: Cac may chu luu tru cac ban ghi du lieu (record) va quan ly viec truyen tai giua producer va consumer.
   - Topic: Luong du lieu ma producer ghi vao va consumer doc tu. Moi topic co the chia thanh nhieu partition.
   - Partition: Moi topic chia thanh cac partition de tang kha nang phan tan va xu ly song song du lieu.
   - Zookeeper: Cong cu quan ly va theo doi Kafka, duy tri trang thai cac node Kafka va phan phoi partition cho cac broker.
    
  Use Case trong Du An:
   - Khi nguoi dung tao tai khoan: UserServiceImpl.saveUser() tao User va gui thong tin (email, userId, secretCode) toi "confirm-account-topic".
   - Email Sender Worker: MailService.sendConfirmLinkByKafka() lang nghe topic, nhan tin nhan va xu ly gui email xac nhan den email cua nguoi dung.
   - Loi Ich: Asynchronous processing, khong chon tao (blocking), nguoi dung nhan response nhanh, email duoc gui sau do.
    
  Cau Hinh:
   - Bootstrap Servers: localhost:29092 (dev environment)
   - Topic: confirm-account-topic (3 partitions, replication factor = 1)
   - Serializer: StringSerializer (Producer) / StringDeserializer (Consumer)
   - Message Format: "email,userId,secretCode" (sau do split() de xu ly)




