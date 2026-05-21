# 🔑 Biến & Hằng số (Variables & Constants)

[« Quay lại README](../README.md)

---

> [!CAUTION]
> **Lưu ý về tên biến tiếng Việt — Dùng tiếng Việt KHÔNG DẤU**
>
> Khi đặt tên biến, hàm, hoặc hằng số bằng tiếng Việt, hãy **luôn dùng tiếng Việt không dấu** để tránh khó khăn khi gõ phím và đảm bảo tính nhất quán trong mã nguồn.
>
> | ❌ Không nên (có dấu)     | ✅ Nên dùng (không dấu)   |
> | :------------------------ | :------------------------ |
> | `biến tổngSố = 0`         | `biến tongSo = 0`         |
> | `biến họTên = "..."`      | `biến hoTen = "..."`      |
> | `biến tuổi = 18`          | `biến tuoi = 18`          |
> | `tínhTổng(số_nguyên a)`   | `tinhTong(so_nguyen a)`   |
>
> **Lý do:** Gõ ký tự có dấu tiếng Việt trong môi trường lập trình đòi hỏi bộ gõ đặc biệt, dễ gây ra lỗi mã hóa và làm chậm tốc độ viết code.

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
> 
> **Cấu trúc tổng quát:**
> `<từ_khóa_khai_báo / kiểu_dữ_liệu> + <mut / khả_biến> + <tên_biến> = <giá_trị>`
> 
> *Ghi chú: Từ khóa `mut`/`khả_biến` luôn đứng ngay sau từ khóa khai báo (bao gồm cả từ khóa suy luận kiểu `var`/`biến` và các kiểu dữ liệu tường minh như `int`/`số_nguyên`, `double`/`số_thực_kép`,...).*

- **Ví dụ khai báo khả biến:**

  ```text
  // --- Phiên bản tiếng Anh ---
  // Suy luận kiểu
  var mut x = 10
  x = 20 // Hợp lệ

  // Kiểu dữ liệu tường minh
  int mut y = 20
  y = 30 // Hợp lệ

  // --- Phiên bản tiếng Việt ---
  // Suy luận kiểu
  biến khả_biến x = 10
  x = 20 // Hợp lệ

  // Kiểu dữ liệu tường minh
  số_nguyên khả_biến y = 20
  y = 30 // Hợp lệ
  ```

---

### 3. Phạm vi hoạt động của biến (Scope)

Mọi biến được khai báo trong CPL đều có **phạm vi hoạt động (scope) giới hạn trong cặp ngoặc nhọn `{}`** nơi chúng được khai báo. Khi ra ngoài block đó, biến sẽ không còn tồn tại.

#### Quy tắc Scope

| Tình huống                        | Kết quả                                                   |
| :-------------------------------- | :-------------------------------------------------------- |
| Biến khai báo trong block `{}`    | Chỉ truy cập được bên trong block đó                      |
| Biến khai báo ở block ngoài       | Truy cập được từ tất cả các block lồng bên trong          |
| Hai biến cùng tên ở hai block khác nhau | Là hai biến độc lập, không ảnh hưởng nhau          |
| Biến của block con trùng tên block cha | Block con sẽ **ẩn (shadow)** biến của block cha    |

#### Ví dụ minh họa

```text
// en
var x = 10          // x ở block ngoài

if (x > 5) {
    var y = 20      // y chỉ tồn tại bên trong if
    println(x)      // Hợp lệ: x từ block ngoài
    println(y)      // Hợp lệ: y cùng block
}

println(x)          // Hợp lệ
// println(y)       // Lỗi: y không tồn tại ở đây

// vi
biến x = 10

nếu (x > 5) thì {
    biến y = 20
    in_dòng_mới(x)  // Hợp lệ
    in_dòng_mới(y)  // Hợp lệ
}

in_dòng_mới(x)     // Hợp lệ
// in_dòng_mới(y)  // Lỗi: y không tồn tại ở đây
```

#### Shadowing (Che khuất biến)

Biến ở block trong có thể trùng tên với biến ở block ngoài — biến trong sẽ **che khuất (shadow)** biến ngoài trong phạm vi của nó.

```text
// en
var x = 10
if (true) {
    var x = 99      // x mới, che khuất x bên ngoài
    println(x)      // In: 99
}
println(x)          // In: 10 (x ban đầu không bị thay đổi)

// vi
biến x = 10
nếu (đúng) thì {
    biến x = 99
    in_dòng_mới(x)  // In: 99
}
in_dòng_mới(x)      // In: 10
```

---

### 4. Hằng số (Constants)

Hằng số yêu cầu phải khai báo rõ kiểu dữ liệu tại thời điểm biên dịch (compile time) và không dùng từ khóa tự suy luận.

```text
// Phiên bản tiếng Anh
const string X = "10"

// Phiên bản tiếng Việt
hằng_số chuỗi X = "10"
```

> 💡 **Mẹo viết code:** Tên hằng số nên được viết hoa toàn bộ và sử dụng `snake_case` (ví dụ: `MAX_VALUE`, `TỐC_ĐỘ_TỐI_ĐA`).

> ⚠️ **Scope của hằng số:** Hằng số cũng tuân theo block scope giống biến thường. Tuy nhiên, hằng số khai báo ở cấp cao nhất (top-level, ngoài mọi hàm) có thể được truy cập từ toàn bộ chương trình.

