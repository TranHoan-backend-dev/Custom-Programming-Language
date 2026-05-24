# 🚀 Custom Programming Language (CPL)

Một ngôn ngữ lập trình hiện đại, dễ tiếp cận, kế thừa tinh hoa từ **Java** và **Rust**.

### 🌟 Đặc điểm nổi bật

- **Hỗ trợ song ngữ Anh - Việt:** Giúp người mới bắt đầu dễ dàng tiếp cận tư duy lập trình bằng tiếng mẹ đẻ.
- **Cú pháp tối giản:** Loại bỏ các thành phần rườm rà, tập trung vào tính tự nhiên và rõ ràng.
- **Mặc định bất biến (Immutable by default):** Học hỏi từ Rust giúp chương trình an toàn, tránh lỗi tranh chấp dữ liệu.
- **Nền tảng Java:** Tận dụng hệ sinh thái phong phú và khả năng chạy đa nền tảng của JVM.

> 💡 **Mục tiêu:** Giúp ngôn ngữ lập trình gần gũi với ngôn ngữ tự nhiên hơn, mang lại trải nghiệm học tập mượt mà và trực quan hơn cho người mới bắt đầu, đặc biệt là người Việt.

---

## 📚 Tài liệu hướng dẫn sử dụng (Documentation)

Dưới đây là chi tiết các phần đặc tả ngôn ngữ CPL được chia nhỏ theo từng chuyên đề:

- [⚙️ **Phần I: Cú pháp khởi chạy (Entry Point)**](docs/01_entry_point.md) - Điểm bắt đầu của mọi chương trình CPL (`main`).
- [📤 **Phần II: Xuất dữ liệu (Output)**](docs/02_output.md) - Các lệnh in dữ liệu ra màn hình console bằng cả tiếng Anh và tiếng Việt.
- [📝 **Phần III: Chú thích (Comments)**](docs/03_comments.md) - Cách viết chú thích một dòng, nhiều dòng, tài liệu (docstring) và phân nhóm code (`#region`).
- [📊 **Phần IV: Kiểu dữ liệu (Data Types)**](docs/04_data_types.md) - Định nghĩa các kiểu số nguyên, số thực, ký tự, chuỗi, boolean và kiểu đặc biệt.
- [🔑 **Phần V: Biến & Hằng số (Variables & Constants)**](docs/05_variables_constants.md) - Cú pháp khai báo biến (mặc định bất biến), biến khả biến (`mut`) và hằng số (`const`).
- [⚡ **Phần VI: Toán tử (Operators)**](docs/06_operators.md) - Đầy đủ toán tử số học, so sánh, logic song ngữ.
- [🔀 **Phần VII: Cấu trúc điều kiện (If-Else)**](docs/07_if_else.md) - Cách sử dụng câu lệnh điều kiện và biểu thức điều kiện trả về giá trị.
- [🔀 **Phần VIII: Cấu trúc lựa chọn (Switch-Case / Match)**](docs/08_switch_case.md) - Biểu thức rẽ nhánh nhiều lựa chọn hỗ trợ gom nhóm giá trị và biểu thức gán giá trị.
- [🔁 **Phần IX: Vòng lặp (Loops)**](docs/09_loops.md) - Lặp không điều kiện (`loop`), lặp có điều kiện, và vòng lặp khoảng (`range-based loop`).
- [🔧 **Phần X: Hàm (Functions)**](docs/10_functions.md) - Cú pháp khai báo hàm, tham số tường minh, trả về tuple (nhiều giá trị).
- [📜 **Phần XI: Chuỗi ký tự (Strings)**](docs/11_strings.md) - Tạo chuỗi, chuyển đổi kiểu với `to_string()`, nối chuỗi, và các thao tác chuỗi phổ biến.

---

## ⌨️ Chạy tương tác (REPL)

Bạn có thể chạy thử mã Nova ngay lập tức với Nova REPL (Read-Eval-Print Loop).
Vui lòng xem [Hướng dẫn sử dụng Nova REPL](docs/repl_demo.md) để biết thêm chi tiết.

---

## 🗺️ Lộ trình Phát triển (Roadmap)

Xem chi tiết kế hoạch 6 giai đoạn phát triển tại [Lộ trình Phát triển CPL (Nova Lang)](roadmap.md).

## 🚀 Hướng dẫn sử dụng ngôn ngữ (Nova CLI)

Nova cung cấp một CLI mạnh mẽ giúp bạn dễ dàng khởi tạo, quản lý và chạy code. Điểm đặc biệt của Nova là **tính nghiêm ngặt về ngôn ngữ**: một project hoặc một file code chỉ được phép dùng đúng một ngôn ngữ (Tiếng Việt hoặc Tiếng Anh), không được trộn lẫn!

### 1. Khởi tạo Project (Khuyên dùng)

Để tạo một project hoàn chỉnh với cấu trúc chuẩn, chạy lệnh:

```text
.\nova.bat init
```

CLI sẽ hỏi tên project và ngôn ngữ lập trình bạn muốn sử dụng (vi/en). Sau đó, nó sẽ tự động tạo:

- Thư mục `src/`: chứa mã nguồn (ví dụ `main.nova`).
- Thư mục `resources/`: chứa file cấu hình `application.yaml` khóa cứng ngôn ngữ đã chọn.

*Khi chạy project này, nếu bạn gõ nhầm từ khóa của ngôn ngữ khác (ví dụ: đang dùng `vi` nhưng gõ chữ `if`), Nova sẽ báo lỗi không nhất quán ngôn ngữ.*

### 2. Chạy toàn bộ App (Khuyên dùng)

Khi bạn đang đứng ở thư mục gốc của project (chứa `src` và `resources`), chỉ cần chạy lệnh:

```text
.\nova.bat run
```

Lệnh này sẽ tự động đọc cấu hình `application.yaml` để xác nhận ngôn ngữ, sau đó tìm và chạy trực tiếp file `src/main.nova`. (Lưu ý: `main.nova` sẽ được tự động tạo sẵn một phương thức `main` để chạy app khi bạn dùng lệnh `init`).

### 3. Chạy file đơn lẻ (Script)

Để chạy một file code `.nova` bất kỳ, bạn có thể chỉ định đường dẫn tới file đó (có thể bỏ đuôi `.nova`):

```text
.\nova.bat <tên_file>
```

*(Ví dụ: `.\nova.bat MyProject/src/main.nova`)*

Nếu bạn chỉ tạo một file `.nova` nhỏ lẻ bên ngoài project và chạy, Nova vẫn sẽ bảo vệ tính nhất quán! Nó sẽ lấy **từ khóa đầu tiên** xuất hiện trong file làm ngôn ngữ chuẩn. Nếu các dòng sau dùng từ khóa của ngôn ngữ khác, lỗi sẽ lập tức được báo.

### 4. Các lệnh tiện ích khác

Ngoài ra, CLI còn cung cấp nhiều công cụ đắc lực cho vòng đời phát triển phần mềm:

- **Kiểm tra mã nguồn (Syntax & Semantic Check):** 
  ```text
  .\nova.bat check
  ```
  Lệnh này sẽ phân tích cú pháp toàn bộ `src/main.nova` để tìm các lỗi sai cú pháp, lỗi trộn lẫn ngôn ngữ hoặc các hàm/biến không hợp lệ mà *không cần thực thi* code.

- **Chạy kiểm thử (Unit Test):**
  ```text
  .\nova.bat test
  ```
  CLI sẽ tự động quét toàn bộ thư mục `src/` tìm các file kết thúc bằng đuôi `_test.nova` và thực thi chúng. Cuối cùng, CLI in ra bảng tổng kết bao nhiêu test PASS và bao nhiêu FAIL.

- **Xem phiên bản:**
  ```text
  .\nova.bat version
  ```

- **Trợ giúp:**
  ```text
  .\nova.bat help
  ```

- **Biên dịch trình thông dịch (Dành cho người phát triển ngôn ngữ Nova):**
  ```text
  .\nova.bat build
  ```
  *(Lưu ý: CLI sẽ tự động build ở lần chạy đầu tiên nếu chưa có thư mục `out`)*
