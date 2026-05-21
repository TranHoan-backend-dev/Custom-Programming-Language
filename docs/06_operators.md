# ⚡ Toán tử (Operators)

[« Quay lại README](../README.md)

---

CPL hỗ trợ đầy đủ các nhóm toán tử cơ bản giúp xử lý các phép tính logic và số học:

### 1. Toán tử số học (Arithmetic Operators)

Dùng cho các phép tính số học cơ bản:

| Toán tử | Ý nghĩa          | Ví dụ   |
| :-----: | :--------------- | :------ |
|   `+`   | Phép cộng        | `x + y` |
|   `-`   | Phép trừ         | `x - y` |
|   `*`   | Phép nhân        | `x * y` |
|   `/`   | Phép chia        | `x / y` |
|   `%`   | Phép chia lấy dư | `x % y` |

### 2. Toán tử so sánh (Comparison Operators)

Trả về giá trị logic (`đúng` / `sai` hoặc `true` / `false`):

|  Anh   |  Việt   | Ý nghĩa           | Ví dụ                             |
| :----: | :-----: | :---------------- | :-------------------------------- |
|  `==`  |  `==`   | So sánh bằng      | `x == y`                          |
|  `!=`  |  `!=`   | So sánh khác      | `x != y`                          |
|`is_not`| `khác`  | So sánh khác      | `x is_not y` hoặc `x khác y`      |
|  `<`   |   `<`   | Nhỏ hơn           | `x < y`                           |
|  `>`   |   `>`   | Lớn hơn           | `x > y`                           |
|  `<=`  |  `<=`   | Nhỏ hơn hoặc bằng | `x <= y`                          |
|  `>=`  |  `>=`   | Lớn hơn hoặc bằng | `x >= y`                          |

### 3. Toán tử logic (Logical Operators)

Dùng để kết hợp nhiều biểu thức điều kiện. CPL hỗ trợ cả ký hiệu toán tử chuẩn lẫn từ khóa bằng chữ tự nhiên:

|  Anh   |  Việt  | Ý nghĩa         | Ví dụ                                              |
| :----: | :----: | :-------------- | :------------------------------------------------- |
|  `&&`  |  `&&`  | Phép VÀ logic   | `điều_kiện_1 && điều_kiện_2`                       |
| `and`  |  `và`  | Phép VÀ logic   | `điều_kiện_1 and điều_kiện_2` hoặc `... và ...`    |
| `\|\|` | `\|\|` | Phép HOẶC logic | `điều_kiện_1 || điều_kiện_2`                       |
|  `or`  | `hoặc` | Phép HOẶC logic | `điều_kiện_1 or điều_kiện_2` hoặc `... hoặc ...`   |
|  `!`   |  `!`   | Phép PHỦ ĐỊNH   | `!điều_kiện_1`                                     |
| `not`  | `không`| Phép PHỦ ĐỊNH   | `not điều_kiện_1` hoặc `không điều_kiện_1`         |
