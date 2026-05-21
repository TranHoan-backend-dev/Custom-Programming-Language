# 🔁 Vòng lặp (Loops)

[« Quay lại README](../README.md)

---

CPL hỗ trợ các kiểu vòng lặp linh hoạt. Tất cả đều có thể hoạt động như **biểu thức trả về giá trị** (tương tự Rust).

---

## 1. Lặp không điều kiện (Infinite Loop)

Vòng lặp chạy vô hạn cho đến khi bị ngắt bởi `break`. Tương đương với `while (true)` trong Java/C.

### 🇬🇧 Phiên bản tiếng Anh

```text
loop {
    // Thực thi liên tục
}
```

### 🇻🇳 Phiên bản tiếng Việt

```text
lặp {
    // Thực thi liên tục
}
```

### Dạng biểu thức gán giá trị

```text
// en
var kết_quả = loop {
    break giá_trị   // trả về giá trị khi thoát
}

// vi
biến kết_quả = lặp {
    break giá_trị
}
```

---

## 2. Lặp kèm điều kiện (Conditional Loop)

Vòng lặp tiếp tục thực thi chừng nào điều kiện còn đúng. Tương đương với `while (điều_kiện)` trong Java/C.

### 🇬🇧 Phiên bản tiếng Anh

```text
loop <condition> {
    // Thực thi khi condition = true
}
```

### 🇻🇳 Phiên bản tiếng Việt

```text
lặp <điều_kiện> {
    // Thực thi khi điều_kiện = đúng
}
```

### Ví dụ

```text
// en
var i = 0
loop i < 10 {
    i = i + 1
}

// vi
biến i = 0
lặp i < 10 {
    i = i + 1
}
```

### Dạng biểu thức gán giá trị

```text
// en
var mut i = 0
var tổng = loop i < 10 {
    i = i + 1
    break i
}

// vi
biến khả_biến i = 0
biến tổng = lặp i < 10 {
    i = i + 1
    break i
}
```

---

## 3. Vòng lặp For (For Loops)

### 3.1 Duyệt theo phần tử (For-Each)

Duyệt qua từng phần tử trong một danh sách / tập hợp.

#### 🇬🇧 Phiên bản tiếng Anh

```text
for <item> of <list> {
    // Thực thi với từng item
}
```

#### 🇻🇳 Phiên bản tiếng Việt

```text
duyệt <phần_tử> của <danh_sách> {
    // Thực thi với từng phần_tử
}
```

#### Ví dụ

```text
// en
var numbers = [1, 2, 3, 4, 5]
for n of numbers {
    println(n)
}

// vi
biến số = [1, 2, 3, 4, 5]
duyệt n của số {
    in_dòng_mới(n)
}
```

---

### 3.2 Duyệt theo khoảng (Range-based Loop)

Duyệt với biến chạy đi qua một khoảng giá trị xác định (tương đương với vòng lặp `for (int i = start; i < end; i++)` trong Java nhưng ngắn gọn và an toàn hơn).

#### 🇬🇧 Phiên bản tiếng Anh

*   **Không bao gồm cận cuối (Exclusive):** `start..end` (chạy từ `start` đến `end - 1`)
    ```text
    for i in 0..10 {
        println(i) // In các số từ 0 đến 9
    }
    ```
*   **Bao gồm cả cận cuối (Inclusive):** `start..=end` (chạy từ `start` đến `end`)
    ```text
    for i in 1..=5 {
        println(i) // In các số từ 1 đến 5
    }
    ```

#### 🇻🇳 Phiên bản tiếng Việt

*   **Không bao gồm cận cuối (Exclusive):** `từ a đến b` (chạy từ `a` đến `b - 1`)
    ```text
    duyệt i từ 0 đến 10 {
        in_dòng_mới(i) // In các số từ 0 đến 9
    }
    ```
*   **Bao gồm cả cận cuối (Inclusive):** `từ a đến_hết b` (chạy từ `a` đến `b`)
    ```text
    duyệt i từ 1 đến_hết 5 {
        in_dòng_mới(i) // In các số từ 1 đến 5
    }
    ```

---

## 5. Điều khiển vòng lặp (Loop Control)

### 5.1 `break` / `dừng` — Thoát vòng lặp

Dùng để **thoát ngay lập tức** khỏi vòng lặp. Có thể kèm theo một giá trị để trả về cho biến bên ngoài (tương tự Rust).

#### 🇬🇧 Phiên bản tiếng Anh

```text
loop {
    if (điều_kiện) {
        break           // thoát không trả về giá trị
    }
    if (điều_kiện_2) {
        break giá_trị   // thoát và trả về giá trị
    }
}
```

#### 🇻🇳 Phiên bản tiếng Việt

```text
lặp {
    nếu (điều_kiện) thì {
        dừng            // thoát không trả về giá trị
    }
    nếu (điều_kiện_2) thì {
        dừng giá_trị    // thoát và trả về giá trị
    }
}
```

#### Ví dụ — Tìm phần tử trong danh sách

```text
// en
var mut i = 0
var vị_trí = loop i < danh_sách.size {
    if (danh_sách[i] == mục_tiêu) {
        break i         // trả về index khi tìm thấy
    }
    i = i + 1
    break -1            // không tìm thấy
}

// vi
biến khả_biến i = 0
biến vị_trí = lặp i < danh_sách.size {
    nếu (danh_sách[i] == mục_tiêu) thì {
        dừng i
    }
    i = i + 1
    dừng -1
}
```

---

### 5.2 `continue` / `tiếp` — Bỏ qua iteration hiện tại

Dùng để **bỏ qua phần còn lại** của vòng lặp hiện tại và chuyển sang iteration tiếp theo, mà không thoát khỏi vòng lặp.

#### 🇬🇧 Phiên bản tiếng Anh

```text
for item of danh_sách {
    if (item == bỏ_qua) {
        continue        // bỏ qua phần tử này
    }
    println(item)       // chỉ in các phần tử không bị bỏ qua
}
```

#### 🇻🇳 Phiên bản tiếng Việt

```text
duyệt phần_tử của danh_sách {
    nếu (phần_tử == bỏ_qua) thì {
        tiếp            // bỏ qua phần tử này
    }
    in_dòng_mới(phần_tử)
}
```

#### Ví dụ — In số chẵn trong danh sách

```text
// en
var numbers = [1, 2, 3, 4, 5, 6]
for n of numbers {
    if (n % 2 != 0) {
        continue        // bỏ qua số lẻ
    }
    println(n)          // in: 2, 4, 6
}

// vi
biến số = [1, 2, 3, 4, 5, 6]
duyệt n của số {
    nếu (n % 2 != 0) thì {
        tiếp
    }
    in_dòng_mới(n)
}
```

---

## 6. Bảng từ khóa vòng lặp

| Tiếng Anh      | Tiếng Việt       | Token      | Mô tả                                            |
| :------------- | :--------------- | :--------- | :----------------------------------------------- |
| `loop`         | `lặp`            | `LOOP`     | Bắt đầu vòng lặp vô hạn hoặc lặp có điều kiện    |
| `for`          | `duyệt`          | `FOR`      | Bắt đầu vòng lặp duyệt (duyệt khoảng / danh sách) |
| `of`           | `của`            | `OF`       | Chỉ định danh sách trong `for-each`              |
| `in`           | `từ`             | `IN`       | Bắt đầu chỉ định khoảng chạy trong range-based   |
| `..` hoặc `..=`| `đến` / `đến_hết`| `RANGE`    | Xác định giới hạn khoảng chạy (Exclusive / Inclusive) |
| `break`        | `dừng`           | `BREAK`    | Thoát khỏi vòng lặp, có thể trả về giá trị       |
| `continue`     | `tiếp`           | `CONTINUE` | Bỏ qua iteration hiện tại, chuyển sang vòng tiếp |

