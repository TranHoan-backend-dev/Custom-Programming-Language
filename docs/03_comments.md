# 📝 Chú thích (Comments)

[« Quay lại README](../README.md)

---

CPL hỗ trợ đa dạng kiểu chú thích và phân nhóm code để tăng tính đọc hiểu:

### 1. Chú thích một dòng (Single-line Comment)

```java
// Đây là chú thích trên một dòng
```

### 2. Chú thích nhiều dòng (Multi-line Comment)

```java
/*
   Đây là chú thích
   trên nhiều dòng
*/
```

### 3. Chú thích tài liệu (Documentation Comment)

```java
/**
 * Đây là chú thích tài liệu (Docs)
 * Dùng để mô tả cho các hàm, class, hoặc thư viện.
 */
```

### 4. Phân nhóm mã nguồn (Code Region)

Học hỏi từ C# để nhóm các khối code logic lại với nhau nhằm tối ưu hóa không gian hiển thị trong trình soạn thảo:

- **Trong C#:**

  ```csharp
  #region Tên nhóm
  // Code ở đây
  #endregion
  ```

- **Trong CPL:**

  ```text
  /// mô_tả: Nhóm các hàm xử lý chuỗi
  hàm_1() { ... }
  hàm_2() { ... }
  ///
  ```
