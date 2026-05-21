# 🔀 Cấu trúc lựa chọn (Switch-Case / Match)

[« Quay lại README](../README.md)

---

CPL hỗ trợ cấu trúc rẽ nhánh nhiều trường hợp `switch-case` tương tự như biểu thức `match` của Rust hoặc `switch expression` của Java hiện đại. Cấu trúc này có thể hoạt động như một câu lệnh độc lập hoặc một biểu thức trả về giá trị để gán trực tiếp cho biến.

### 1. Cú pháp cơ bản

#### 🇬🇧 Phiên bản tiếng Anh

```text
switch (biến) {
    case_a -> biểu_thức_hoặc_hành_động
    case_b -> biểu_thức_hoặc_hành_động
    _ -> giá_trị_mặc_định
}
```

#### 🇻🇳 Phiên bản tiếng Việt

```text
trường_hợp (biến) {
    case_a -> biểu_thức_hoặc_hành_động
    case_b -> biểu_thức_hoặc_hành_động
    _ -> giá_trị_mặc_định
}
```

---

### 2. Dạng biểu thức gán giá trị (Switch Expressions)

Cho phép gán trực tiếp kết quả của cấu trúc rẽ nhánh cho một biến.

#### 🇬🇧 Phiên bản tiếng Anh

```text
var kết_quả = switch (x) {
    1 -> "Một"
    2 -> "Hai"
    _ -> "Khác"
}
```

#### 🇻🇳 Phiên bản tiếng Việt

```text
biến kết_quả = trường_hợp (x) {
    1 -> "Một"
    2 -> "Hai"
    _ -> "Khác"
}
```

---

### 3. Hỗ trợ nhiều giá trị trong cùng một nhánh (Multiple Case)

Bạn có thể gom nhóm nhiều giá trị bằng ký tự `|` để thực thi cùng một khối lệnh.

#### 🇬🇧 Phiên bản tiếng Anh

```text
switch (x) {
    1 | 3 -> // Thực thi khi x = 1 hoặc x = 3
    2 | 4 -> // Thực thi khi x = 2 hoặc x = 4
    _ -> // Trường hợp mặc định
}
```

#### 🇻🇳 Phiên bản tiếng Việt

```text
trường_hợp (x) {
    1 | 3 -> // Thực thi khi x = 1 hoặc x = 3
    2 | 4 -> // Thực thi khi x = 2 hoặc x = 4
    _ -> // Trường hợp mặc định
}
```
