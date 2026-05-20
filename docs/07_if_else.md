# 🔀 Cấu trúc điều kiện (If-Else)

[« Quay lại README](../README.md)

---

CPL hỗ trợ cấu trúc rẽ nhánh điều kiện `if-else` dưới cả dạng câu lệnh (statement) thông thường và dạng biểu thức (expression) có giá trị trả về tương tự như Rust để gán trực tiếp cho biến.

### 1. Dạng câu lệnh điều kiện (If-Else Statements)

#### 🇬🇧 Phiên bản tiếng Anh (tương tự Java/JS)

Cú pháp:
```text
if (điều_kiện_1) {
    // Thực thi nếu điều_kiện_1 đúng
} else if (điều_kiện_2) {
    // Thực thi nếu điều_kiện_2 đúng
} else {
    // Thực thi nếu tất cả điều kiện trên sai
}
```

#### 🇻🇳 Phiên bản tiếng Việt

Cú pháp:
```text
nếu (điều_kiện_1) thì {
    // Thực thi nếu điều_kiện_1 đúng
} còn_nếu (điều_kiện_2) thì {
    // Thực thi nếu điều_kiện_2 đúng
} không_thì {
    // Thực thi nếu tất cả điều kiện trên sai
}
```

---

### 2. Dạng biểu thức gán giá trị (If-Else Expressions)

CPL cho phép khối điều kiện hoạt động như một biểu thức có trả về giá trị (tương tự như Rust). Giá trị trả về của khối là kết quả của biểu thức cuối cùng trong khối (không có dấu chấm phẩy `;`).

#### 🇬🇧 Phiên bản tiếng Anh

```text
var x = if (điều_kiện) { 10 } else { 20 }
```

#### 🇻🇳 Phiên bản tiếng Việt

```text
biến x = nếu (điều_kiện) thì { 10 } không_thì { 20 }
```
