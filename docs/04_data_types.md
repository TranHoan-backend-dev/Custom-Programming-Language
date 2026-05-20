# 📊 Kiểu dữ liệu (Data types)

[« Quay lại README](../README.md)

---

Biến trong CPL có kiểu dữ liệu kế thừa từ sự an toàn của Rust và tính tường minh của Java.

### 1. Kiểu số nguyên

| Biến tiếng Anh | Biến tiếng Việt | Kích thước | Mô tả              | Giá trị                                                      |
| :------------- | :-------------- | :--------- | :----------------- | :----------------------------------------------------------- |
| `int`          | `số_nguyên`     | 32-bit     | Số nguyên mặc định | `-2,147,483,648` đến `2,147,483,647`                         |
| `int16`        | `số_nguyên_16`  | 16-bit     | Số nguyên ngắn     | `-32,768` đến `32,767`                                       |
| `int32`        | `số_nguyên_32`  | 32-bit     | Số nguyên 32-bit   | `-2,147,483,648` đến `2,147,483,647`                         |
| `int64`        | `số_nguyên_64`  | 64-bit     | Số nguyên lớn/dài  | `-9,223,372,036,854,775,808` đến `9,223,372,036,854,775,807` |

### 2. Kiểu số thực

| Biến tiếng Anh | Biến tiếng Việt | Kích thước | Mô tả                    | Giá trị                             |
| :------------- | :-------------- | :--------- | :----------------------- | :---------------------------------- |
| `double`       | `số_thực_kép`   | 64-bit     | Số thực độ chính xác kép | Khoảng `±4.9e-324` đến `±1.79e+308` |
| `float`        | `số_thực_đơn`   | 32-bit     | Số thực độ chính xác đơn | Khoảng `±1.4e-45` đến `±3.4e+38`    |

### 3. Kiểu ký tự & chuỗi

| Biến tiếng Anh | Biến tiếng Việt | Kích thước | Mô tả                  | Giá trị                            |
| :------------- | :-------------- | :--------- | :--------------------- | :--------------------------------- |
| `char`         | `ký_tự`         | 16-bit     | Ký tự Unicode đơn      | Ký tự từ `'\u0000'` đến `'\uffff'` |
| `string`       | `chuỗi`         | Thay đổi   | Chuỗi ký tự (Bất biến) | Tập hợp các ký tự trong dấu `""`   |

### 4. Kiểu logic

| Biến tiếng Anh | Biến tiếng Việt | Mô tả                 | Giá trị                              |
| :------------- | :-------------- | :-------------------- | :----------------------------------- |
| `boolean`      | `logic`         | Kiểu logic Đúng / Sai | `true` / `đúng` hoặc `false` / `sai` |

### 5. Kiểu đặc biệt

| Biến tiếng Anh | Biến tiếng Việt | Mô tả            | Ý nghĩa                              |
| :------------- | :-------------- | :--------------- | :----------------------------------- |
| `null`         | `k_tồn_tại`     | Giá trị rỗng     | Không trỏ tới vùng nhớ đối tượng nào |
| `void`         | `trống`         | Kiểu trả về rỗng | Biểu thị hàm không trả về kết quả    |
