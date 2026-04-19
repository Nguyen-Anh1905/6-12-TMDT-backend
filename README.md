# Backend_TMDT — Hướng dẫn cài đặt môi trường (Backend)

Tài liệu này hướng dẫn cách thiết lập và chạy backend của dự án `Backend_TMDT` trên máy Windows.

Môi trường yêu cầu
- Java 21 (JDK 21) — đảm bảo `JAVA_HOME` trỏ tới JDK 21 và `java -version` trả về 21.x
- Maven (không bắt buộc nếu bạn dùng `mvnw`), hoặc sử dụng Maven Wrapper có sẵn (`mvnw.cmd`)
- MySQL (hoặc MariaDB) — database để kết nối khi chạy ứng dụng
- IDE: IntelliJ IDEA (khuyến nghị) hoặc Visual Studio Code

Các bước thiết lập

1) Cài JDK 21
- Tải JDK 21 từ Oracle/OpenJDK, cài đặt và thiết lập `JAVA_HOME`.
- Kiểm tra:

```cmd
java -version
javac -version
```

2) Cài Maven (tuỳ chọn)
- Bạn có thể dùng `mvnw.cmd` có sẵn trong repo; nếu muốn cài Maven toàn cục, cài đặt và kiểm tra `mvn -v`.

3) Import project vào IntelliJ IDEA
- Open > chọn thư mục dự án `Backend_TMDT`.
- IntelliJ sẽ phát hiện `pom.xml` và import Maven project.

4) Kích hoạt Lombok trong IntelliJ (rất quan trọng)
- Cài plugin Lombok: Settings > Plugins > Marketplace > tìm `Lombok` > Install.
- Bật Annotation Processing: Settings > Build, Execution, Deployment > Compiler > Annotation Processors > check `Enable annotation processing`.
- Sau đó làm `Reload Maven` (nhấp phải vào `pom.xml` > Maven > Reload Project) hoặc `Reimport` project.

Lý do: controller sử dụng Lombok annotations như `@RequiredArgsConstructor`; nếu IntelliJ chưa bật annotation processing hoặc thiếu plugin Lombok, bạn sẽ thấy lỗi IDE kiểu "Cannot resolve symbol 'ProductService'" hoặc "Cannot resolve constructor" mặc dù mã hợp lệ và Maven có thể biên dịch được.

5) Cấu hình database
- Mở `src/main/resources/application.properties` và cấu hình kết nối MySQL:

```
spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

- Tạo database `your_db_name` trong MySQL hoặc chỉnh tên phù hợp.

6) Chạy build & chạy ứng dụng
- Dùng Maven Wrapper (Windows cmd):

```cmd
.\mvnw.cmd -DskipTests=true package
```

- Chạy jar (sau package):

```cmd
java -jar target\Backend_TMDT-0.0.1-SNAPSHOT.jar
```

- Hoặc chạy trong IDE: tìm `BackendTmdtApplication` và chạy `main`.

7) Test nhanh API
- Sau khi khởi chạy thành công, API mặc định lắng nghe trên `http://localhost:8080` (xem `application.properties` để xác nhận).
- Ví dụ điểm cuối sản phẩm: `GET /api/products`.

Khắc phục lỗi IDE: "Cannot resolve symbol 'ProductService'"
- Nguyên nhân hay gặp:
  - Lombok plugin chưa cài hoặc annotation processing chưa bật => Lombok-generated constructor không được nhận biết bởi IDE.
  - Maven project chưa reimport => class từ module khác chưa được IDE index.
  - Sai package/import (kiểm tra dòng import trong file controller).

- Các bước sửa nhanh:
  1. Cài plugin Lombok vào IntelliJ và bật Annotation Processing (như ở mục 4).
  2. Chuột phải `pom.xml` > Maven > Reload Project.
  3. Build project (Build > Rebuild Project) hoặc chạy `mvnw.cmd -DskipTests=true compile`.
  4. Nếu vẫn báo lỗi, thử Invalidate Caches & Restart trong IntelliJ.

Gợi ý bổ sung
- IDE khác: VSCode cần cài extension Lombok Annotations Support và Java extension pack; đảm bảo `java.compile.nullAnalysis` không chặn.
- Nếu bạn muốn build nhanh mà không cần DB, có thể thiết lập profile với in-memory DB (H2) trong `application.properties` tạm thời.

Liên hệ
- Nếu cần hướng dẫn chi tiết hơn (ví dụ tạo user DB, script SQL khởi tạo), cho biết OS và quyền truy cập DB, mình sẽ bổ sung.

-- END --

