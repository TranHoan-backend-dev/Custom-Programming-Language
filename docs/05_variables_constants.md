# 🔑 Biến & Hằng số (Variables & Constants)

[« Quay lại README](../README.md)

---

### 1. Cú pháp khai báo biến thông thường

CPL hỗ trợ cả tự suy luận kiểu dữ liệu (`var`/`biến`) hoặc khai báo tường minh.

```text
// Phiên bản tiếng Anh
var x = 10
int y = 20

// Phiên bản tiếng Việt
biến x = 10
số_nguyên y = 20
```

> ⚠️ **Quan trọng:** Mặc định các biến đều là **bất biến (immutable)**.
> Nếu muốn biến có thể gán lại giá trị mới, bạn phải thêm từ khóa `mut`/`khả_biến`.

- **Ví dụ khai báo khả biến:**

  ```text
  // en
  var mut x = 10
  x = 20 // Hợp lệ

  // vi
  biến khả_biến x = 10
  x = 20 // Hợp lệ
  ```

- **Phạm vi hoạt động (Scope):** Các biến có phạm vi hoạt động giới hạn trong cặp ngoặc nhọn `{}` (block scope) nơi chúng được khai báo.

### 2. Hằng số (Constants)

Hằng số yêu cầu phải khai báo rõ kiểu dữ liệu tại thời điểm biên dịch (compile time) và không dùng từ khóa tự suy luận.

```text
// Phiên bản tiếng Anh
const string X = "10"

// Phiên bản tiếng Việt
hằng_số chuỗi X = "10"
```

> 💡 **Mẹo viết code:** Tên hằng số nên được viết hoa toàn bộ và sử dụng `snake_case` (ví dụ: `MAX_VALUE`, `TỐC_ĐỘ_TỐI_ĐA`).
