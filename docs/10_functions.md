# 🔧 Hàm (Functions)

[« Quay lại README](../README.md)

---

CPL hỗ trợ định nghĩa hàm với cú pháp rõ ràng, tường minh. Tên hàm được viết theo quy ước **camelCase**.

---

## 1. Cú pháp khai báo hàm

CPL cho phép khai báo hàm theo hai phong cách:

1. **Bắt đầu trực tiếp bằng tên hàm** (không dùng từ khóa khai báo).
2. **Sử dụng từ khóa khai báo tùy chọn** (`function` trong tiếng Anh hoặc `hàm` trong tiếng Việt) đứng trước tên hàm để tăng tính rõ ràng và giúp IDE hỗ trợ tốt hơn.

> [!IMPORTANT]
> **Từ khóa khai báo hàm là TÙY CHỌN (Optional):**
>
> - Việc sử dụng từ khóa `function` (tiếng Anh) hoặc `hàm` (tiếng Việt) trước tên hàm là không bắt buộc. Bạn có thể chọn cách khai báo có hoặc không có từ khóa tùy theo sở thích và độ phức tạp của dự án.
> - Cú pháp tiếng Anh tương ứng sử dụng từ khóa **`function`** (ví dụ: `function functionName() -> void`).

### 🇬🇧 Phiên bản tiếng Anh

```text
// Cách 1: Bắt đầu trực tiếp bằng tên hàm
functionName() -> ReturnType {
    // Thân hàm
}

// Cách 2: Sử dụng từ khóa function (tùy chọn)
function functionName() -> ReturnType {
    // Thân hàm
}
```

### 🇻🇳 Phiên bản tiếng Việt

```text
// Cách 1: Bắt đầu trực tiếp bằng tên hàm
tenHam() -> KiểuTrảVề {
    // Thân hàm
}

// Cách 2: Sử dụng từ khóa hàm (tùy chọn)
hàm tenHam() -> KiểuTrảVề {
    // Thân hàm
}
```

---

## 2. Hàm không trả về giá trị (void)

### 🇬🇧 Phiên bản tiếng Anh

```text
// Sử dụng từ khóa function
function greet() -> void {
    println("Hello!")
}
```

### 🇻🇳 Phiên bản tiếng Việt

```text
// Sử dụng từ khóa hàm
hàm chaoHoi() -> trống {
    in_dòng_mới("Xin chào!")
}
```

---

## 3. Hàm có tham số (Parameters)

Tham số được khai báo theo cú pháp: **kiểu dữ liệu + tên biến**.

> ⚠️ **Quy tắc:** Không được phép dùng kiểu dữ liệu suy luận (`var`/`biến`) cho tham số hàm. Phải khai báo tường minh kiểu dữ liệu.

### 🇬🇧 Phiên bản tiếng Anh

```text
// Ví dụ không dùng từ khóa khai báo
add(int a, int b) -> int {
    return a + b
}

// Ví dụ dùng từ khóa function
function greetUser(string name, int age) -> void {
    println("Tên: " + name + ", Tuổi: " + age)
}
```

### 🇻🇳 Phiên bản tiếng Việt

```text
// Ví dụ không dùng từ khóa khai báo
tổng(số_nguyên a, số_nguyên b) -> số_nguyên {
    trả_về a + b
}

// Ví dụ dùng từ khóa hàm
hàm chaoNguoiDung(chuỗi tên, số_nguyên tuổi) -> trống {
    in_dòng_mới("Tên: " + tên + ", Tuổi: " + tuổi)
}
```

---

## 4. Giá trị trả về (Return Value)

Quy tắc sử dụng `return`/`trả_về`:

| Kiểu trả về      | Bắt buộc `return`/`trả_về`? | Ghi chú                                 |
| :--------------- | :--------------------------- | :-------------------------------------- |
| `void` / `trống` | ❌ Không cần                 | Hàm tự kết thúc sau câu lệnh cuối cùng |
| Kiểu dữ liệu bất kỳ | ✅ Bắt buộc              | Phải có `return`/`trả_về` tường minh    |

Hàm sẽ kết thúc ngay tại dòng `return`/`trả_về` đầu tiên được thực thi.

### 🇬🇧 Phiên bản tiếng Anh

```text
// Hàm void — không cần return
greet(string name) -> void {
    println("Hello, " + name)
}

// Hàm có giá trị trả về — bắt buộc return
square(int n) -> int {
    return n * n
}

// Thoát sớm với return
abs(int n) -> int {
    if (n < 0) {
        return n * -1
    }
    return n
}
```

### 🇻🇳 Phiên bản tiếng Việt

```text
// Hàm trống — không cần trả_về
chàoHỏi(chuỗi tên) -> trống {
    in_dòng_mới("Xin chào, " + tên)
}

// Hàm có giá trị trả về — bắt buộc trả_về
bìnhPhương(số_nguyên n) -> số_nguyên {
    trả_về n * n
}

// Thoát sớm với trả_về
trịTuyệtĐối(số_nguyên n) -> số_nguyên {
    nếu (n < 0) thì {
        trả_về n * -1
    }
    trả_về n
}
```

---

## 5. Hàm trả về Tuple (Multiple Return Values)

CPL hỗ trợ trả về **nhiều giá trị** bằng cách sử dụng Tuple. Cú pháp kiểu trả về là danh sách các `kiểu_dữ_liệu tên` được bọc trong `()`.

### 🇬🇧 Phiên bản tiếng Anh

```text
// Khai báo hàm trả về tuple
divAndMod(int a, int b) -> (int quotient, int remainder) {
    return (a / b, a % b)
}

// Sử dụng
var (q, r) = divAndMod(10, 3)
println(q)   // 3
println(r)   // 1
```

### 🇻🇳 Phiên bản tiếng Việt

```text
// Khai báo hàm trả về tuple
chiaCóDư(số_nguyên a, số_nguyên b) -> (số_nguyên thương, số_nguyên dư) {
    trả_về (a / b, a % b)
}

// Sử dụng
biến (t, d) = chiaCóDư(10, 3)
in_dòng_mới(t)   // 3
in_dòng_mới(d)   // 1
```

---

## 6. Quy ước đặt tên hàm

| Quy tắc          | Mô tả                                          | Ví dụ                            |
| :--------------- | :--------------------------------------------- | :------------------------------- |
| **camelCase**    | Chữ cái đầu viết thường, từ tiếp theo viết hoa | `tínhTổng`, `layDuLieu`          |
| Tên mô tả hành động | Nên bắt đầu bằng động từ                    | `tínhDiệnTích`, `kiểmTra`, `lấy` |
| Không dùng `_`   | Không dùng snake_case cho tên hàm               | ❌ `tinh_tong` → ✅ `tínhTổng`   |

---

## 7. Bảng từ khóa liên quan

| Tiếng Anh  | Tiếng Việt | Token      | Mô tả                                 |
| :--------- | :--------- | :--------- | :------------------------------------ |
| `function` | `hàm`      | `FUNCTION` | Bắt đầu khai báo hàm (tùy chọn)       |
| `return`   | `trả_về`   | `RETURN`   | Trả về giá trị và thoát hàm           |
| `void`     | `trống`    | `VOID`     | Kiểu trả về rỗng (không có giá trị)   |
