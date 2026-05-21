# 📤 Xuất dữ liệu (Output)

[« Quay lại README](../README.md)

---

CPL hỗ trợ xuất dữ liệu ra màn hình console bằng cả hai ngôn ngữ:

### 🇬🇧 Phiên bản tiếng Anh

| Mô tả                           | Cú pháp                                   |
| :------------------------------ | :---------------------------------------- |
| In ra thông điệp trên cùng dòng | `print(giá_trị)`                          |
| In ra thông điệp kèm xuống dòng | `println(giá_trị)`                        |
| In ra thông điệp định dạng      | `printf(định_dạng, giá_trị)`              |
| In kèm placeholder              | `print("{placeholder} thông điệp", biến)` |

### 🇻🇳 Phiên bản tiếng Việt

| Mô tả                           | Cú pháp                                |
| :------------------------------ | :------------------------------------- |
| In ra thông điệp trên cùng dòng | `in(giá_trị)`                          |
| In ra thông điệp kèm xuống dòng | `in_dòng_mới(giá_trị)`                 |
| In ra thông điệp định dạng      | `in_định_dạng(định_dạng, giá_trị)`     |
| In kèm placeholder              | `in("{placeholder} thông điệp", biến)` |

---

### 3. Cơ chế định dạng và nội suy chuỗi (String Interpolation)

CPL hỗ trợ hai phương thức nội suy giá trị vào chuỗi thông điệp khi in:

#### A. Nội suy trực tiếp (Direct Interpolation)
Tự động tìm kiếm biến trong phạm vi hoạt động (scope) hiện tại để chèn trực tiếp giá trị của nó vào chuỗi tại các vị trí `{tên_biến}`.
*   **English:**
    ```text
    var name = "An"
    var age = 18
    println("Hello {name}, you are {age} years old.")
    // Kết quả: Hello An, you are 18 years old.
    ```
*   **Tiếng Việt:**
    ```text
    biến ten = "An"
    biến tuoi = 18
    in_dòng_mới("Chào {ten}, bạn {tuoi} tuổi.")
    // Kết quả: Chào An, bạn 18 tuổi.
    ```

#### B. Định dạng theo vị trí (Positional Formatting)
Nếu truyền thêm các đối số phía sau chuỗi thông điệp, các cặp `{}` trong chuỗi sẽ lần lượt được thay thế bằng giá trị của các đối số này theo đúng thứ tự truyền vào.
*   **English:**
    ```text
    var name = "An"
    var age = 18
    println("Hello {}, you are {} years old.", name, age)
    ```
*   **Tiếng Việt:**
    ```text
    biến ten = "An"
    biến tuoi = 18
    in_dòng_mới("Chào {}, bạn {} tuổi.", ten, tuoi)
    ```
