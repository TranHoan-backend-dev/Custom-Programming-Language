# 🔧 Hàm (Functions)

[« Quay lại README](../README.md)

---

CPL hỗ trợ định nghĩa hàm với cú pháp rõ ràng, tường minh. Tên hàm được viết theo quy ước **camelCase**.

---

## 1. Cú pháp khai báo hàm

Thứ tự khai báo: **tên hàm** `()` `->` **kiểu trả về** `{}`

### 🇬🇧 Phiên bản tiếng Anh

```text
functionName() -> ReturnType {
    // Thân hàm
}
```

### 🇻🇳 Phiên bản tiếng Việt

CPL không có từ khóa khai báo hàm riêng — tên hàm đứng đầu trực tiếp (tương tự Go).

```text
tênHàm() -> KiểuTrảVề {
    // Thân hàm
}
```

---

## 2. Hàm không trả về giá trị (void)

### 🇬🇧 Phiên bản tiếng Anh

```text
greet() -> void {
    println("Hello!")
}
```

### 🇻🇳 Phiên bản tiếng Việt

```text
chàoHỏi() -> trống {
    in_dòng_mới("Xin chào!")
}
```

---

## 3. Hàm có tham số (Parameters)

Tham số được khai báo theo cú pháp: **kiểu dữ liệu + tên biến**.

> ⚠️ **Quy tắc:** Không được phép dùng kiểu dữ liệu suy luận (`var`/`biến`) cho tham số hàm. Phải khai báo tường minh kiểu dữ liệu.

### 🇬🇧 Phiên bản tiếng Anh

```text
add(int a, int b) -> int {
    return a + b
}

greetUser(string name, int age) -> void {
    println("Tên: " + name + ", Tuổi: " + age)
}
```

### 🇻🇳 Phiên bản tiếng Việt

```text
tổng(số_nguyên a, số_nguyên b) -> số_nguyên {
    trả_về a + b
}

chàoNgườiDùng(chuỗi tên, số_nguyên tuổi) -> trống {
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

| Tiếng Anh | Tiếng Việt | Token    | Mô tả                         |
| :-------- | :--------- | :------- | :---------------------------- |
| `return`  | `trả_về`   | `RETURN` | Trả về giá trị và thoát hàm   |
| `void`    | `trống`    | `VOID`   | Kiểu trả về rỗng (không giá trị) |
