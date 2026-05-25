# 📜 Chuỗi ký tự (Strings)

[« Quay lại README](../README.md)

---

CPL hỗ trợ kiểu dữ liệu chuỗi `string`/`chuỗi` với các thao tác quen thuộc từ Java, kết hợp thêm một số cú pháp tự nhiên hơn.

---

## 1. Tạo chuỗi mới (String Literal)

Chuỗi được tạo bằng cách gán trực tiếp một giá trị nằm trong cặp dấu ngoặc kép `"..."`.

### 🇬🇧 Phiên bản tiếng Anh

```text
var greeting = "Hello, World!"
string name = "Nguyễn Văn A"
```

### 🇻🇳 Phiên bản tiếng Việt

```text
biến loiChao = "Xin chào, thế giới!"
chuỗi ten = "Nguyễn Văn A"
```

---

## 2. Chuyển đổi giá trị sang chuỗi (to_string)

Để tạo một chuỗi từ một biến thuộc kiểu dữ liệu khác (số, boolean,...), sử dụng phương thức **`to_string()`**.

> ✅ Đây là cách duy nhất để chuyển đổi kiểu dữ liệu sang `string` trong CPL — không hỗ trợ ép kiểu ngầm định (implicit casting).

### 🇬🇧 Phiên bản tiếng Anh

```text
int age = 25
var ageStr = age.to_string()        // "25"

double pi = 3.14
var piStr = pi.to_string()          // "3.14"

boolean active = true
var activeStr = active.to_string()  // "true"
```

### 🇻🇳 Phiên bản tiếng Việt

```text
số_nguyên tuoi = 25
biến tuoiStr = tuoi.to_string()        // "25"

số_thực_kép pi = 3.14
biến piStr = pi.to_string()            // "3.14"

logic dangHoatDong = đúng
biến trangThaiStr = dangHoatDong.to_string()  // "đúng"
```

---

## 3. Nối chuỗi (String Concatenation)

Nối chuỗi sử dụng toán tử `+`, tương tự như Java. Không thể nối chuỗi trực tiếp với kiểu dữ liệu khác mà không gọi `to_string()` trước.

### 🇬🇧 Phiên bản tiếng Anh

```text
var firstName = "Nguyen"
var lastName = "Van A"
var fullName = firstName + " " + lastName   // "Nguyen Van A"

// Nối với số — phải gọi to_string() trước
int age = 25
var info = "Tuổi: " + age.to_string()       // "Tuổi: 25"
```

### 🇻🇳 Phiên bản tiếng Việt

```text
biến ho = "Nguyễn"
biến ten = "Văn A"
biến hoTen = ho + " " + ten                // "Nguyễn Văn A"

// Nối với số — phải gọi to_string() trước
số_nguyên tuoi = 25
biến thongTin = "Tuổi: " + tuoi.to_string()  // "Tuổi: 25"
```

---

## 4. Các thao tác chuỗi phổ biến (String Operations)

Các thao tác chuỗi kế thừa từ Java, được gọi thông qua ký hiệu chấm `.`:

| Phương thức          | Mô tả                                 | Ví dụ                                        |
| :------------------- | :------------------------------------ | :------------------------------------------- |
| `.length()`          | Lấy độ dài chuỗi                     | `"hello".length()` → `5`                     |
| `.toUpperCase()`     | Chuyển tất cả sang chữ hoa           | `"hello".toUpperCase()` → `"HELLO"`          |
| `.toLowerCase()`     | Chuyển tất cả sang chữ thường        | `"HELLO".toLowerCase()` → `"hello"`          |
| `.trim()`            | Xóa khoảng trắng ở đầu và cuối       | `"  hi  ".trim()` → `"hi"`                   |
| `.contains(str)`     | Kiểm tra chuỗi có chứa `str` không   | `"hello".contains("ell")` → `true`           |
| `.startsWith(str)`   | Kiểm tra chuỗi bắt đầu bằng `str`    | `"hello".startsWith("he")` → `true`          |
| `.endsWith(str)`     | Kiểm tra chuỗi kết thúc bằng `str`   | `"hello".endsWith("lo")` → `true`            |
| `.substring(a, b)`   | Lấy chuỗi con từ index `a` đến `b`   | `"hello".substring(1, 3)` → `"el"`           |
| `.replace(old, new)` | Thay thế chuỗi con                   | `"hello".replace("l", "r")` → `"herro"`     |
| `.split(sep)`        | Tách chuỗi theo ký tự phân tách      | `"a,b,c".split(",")` → `["a", "b", "c"]`    |
| `.indexOf(str)`      | Tìm vị trí đầu tiên của `str`        | `"hello".indexOf("l")` → `2`                 |
| `.isEmpty()`         | Kiểm tra chuỗi có rỗng không         | `"".isEmpty()` → `true`                      |
| `.to_string()`       | Chuyển kiểu dữ liệu khác sang chuỗi  | `(25).to_string()` → `"25"`                  |

---

## 5. So sánh chuỗi (String Comparison)

> ✅ **Quan trọng:** Khác với Java, CPL hỗ trợ so sánh **nội dung chuỗi** trực tiếp bằng toán tử `==` hoặc `!=` (tương tự như Kotlin, C#, Python, Rust) thay vì phải dùng phương thức `.equals()`.

```text
// en
var a = "hello"
var b = "hello"

a == b          // true  — So sánh nội dung chuỗi trực tiếp ✅
a != b          // false

// vi
biến a = "xin chào"
biến b = "xin chào"

a == b          // đúng  — So sánh nội dung chuỗi trực tiếp ✅
a khác b        // sai   (hoặc a != b)
```
