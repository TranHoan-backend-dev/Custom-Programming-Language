# 📐 Quy tắc viết mã (Coding Convention)

[« Quay lại README](../../README.md)

---

> [!NOTE]
> Tài liệu này mô tả các quy tắc **phong cách viết mã** khuyến nghị cho ngôn ngữ Nova.
> Mục tiêu: đảm bảo mã nguồn dễ đọc, nhất quán và dễ bảo trì trong mọi dự án.

---

## Mục lục

1. [Indentation & Whitespace](#1-indentation--whitespace)
2. [Braces style](#2-braces-style)
3. [Comment conventions](#3-comment-conventions)
4. [Line length](#4-line-length)
5. [Mutability conventions](#5-mutability-conventions)
6. [Null Safety conventions](#6-null-safety-conventions)

---

## 1. Indentation & Whitespace

### Thụt lề

- Sử dụng **4 dấu cách (spaces)** cho mỗi cấp thụt lề.
- **Không dùng tab.**

```text
function calculate_area(int width, int height) -> int {
    var area = width * height    // 4 spaces
    if (area > 100) {
        println("Large area!")   // 8 spaces
    }
    return area
}
```

### Khoảng trắng quanh toán tử

- Đặt **1 dấu cách** ở cả hai bên toán tử (`=`, `+`, `-`, `*`, `/`, `==`, `!=`, `>`, `<`, `&&`, `||`).

```text
// ✅ Đúng
var x = 10
var result = a + b * c
if (x >= 5 && y != 0) { ... }

// ❌ Sai
var x=10
var result=a+b*c
if (x>=5&&y!=0) { ... }
```

### Khoảng trắng sau dấu phẩy

- Đặt **1 dấu cách** sau dấu phẩy trong danh sách tham số hoặc mảng.

```text
// ✅ Đúng
function add(int a, int b, int c) -> int { ... }

// ❌ Sai
function add(int a,int b,int c) -> int { ... }
```

### Dòng trống

- Đặt **1 dòng trống** giữa các hàm.
- Đặt **1 dòng trống** trước các khối logic quan trọng trong hàm để dễ đọc.

---

## 2. Braces style

Nova sử dụng **K&R style** (giống Java) — dấu `{` mở nằm **cùng dòng** với câu lệnh khai báo.

```text
// ✅ K&R style (Đúng)
function greet(string name) -> void {
    if (name == "Nova") {
        println("Welcome home!")
    } else {
        println("Hello, " + name)
    }
}

// ❌ Allman style (Không dùng)
function greet(string name) -> void
{
    if (name == "Nova")
    {
        println("Welcome home!")
    }
}
```

**Quy tắc:**
- Dấu `{` mở luôn **cùng dòng** với `function`, `if`, `else`, `while`, `loop`, `switch`, v.v.
- Dấu `}` đóng luôn **nằm trên dòng riêng**, thẳng hàng với từ khóa mở.
- Từ khóa `else` nằm **cùng dòng** với `}` đóng của khối `if` phía trước.

---

## 3. Comment conventions

Nova tuân theo phong cách comment của Java.

### Comment một dòng (`//`)

- Dùng để giải thích logic ngắn gọn hoặc ghi chú nhanh.
- Đặt **1 dấu cách** sau `//`.

```text
// Tính diện tích hình chữ nhật
var area = width * height
```

### Comment nhiều dòng (`/* ... */`)

- Dùng cho giải thích dài hoặc vô hiệu hóa một đoạn code (disable code).

```text
/*
 * Thuật toán tìm kiếm nhị phân:
 * - Yêu cầu mảng đã sắp xếp
 * - Độ phức tạp: O(log n)
 */
```

### Comment tài liệu - Docstring (`/** ... */`)

- Dùng để viết tài liệu cho hàm. Luôn đặt ngay trước khai báo hàm.
- Sử dụng các thẻ như `@param` (mô tả tham số) và `@return` (mô tả giá trị trả về).

```text
/**
 * Tính tổng hai số nguyên.
 *
 * @param a Số hạng thứ nhất
 * @param b Số hạng thứ hai
 * @return Tổng của a và b
 */
hàm tinh_tong(số_nguyên a, số_nguyên b) -> số_nguyên {
    trả_về a + b
}
```

---

## 4. Line length

- Giới hạn **tối đa 120 ký tự** mỗi dòng (chuẩn Java hiện đại).
- Nếu dòng quá dài, hãy ngắt xuống dòng và thụt lề thêm (thường là 8 spaces) để dễ phân biệt với block code.

```text
// ✅ Dòng quá dài — tách dòng
var result = calculate_complex_formula(
        firstParameter, secondParameter,
        thirdParameter, fourthParameter)
```

---

## 5. Mutability conventions (Quy tắc về tính khả biến)

Trong Nova, mọi biến **mặc định là bất biến (immutable)**. Nghĩa là sau khi gán giá trị lần đầu, bạn không thể thay đổi nó. Nếu muốn thay đổi, bạn phải thêm từ khóa `mut` (tiếng Anh) hoặc `khả_biến` (tiếng Việt).

### Quy tắc

1. **Mặc định không dùng `mut`**: Hãy luôn tập thói quen khai báo biến bình thường trước. Chỉ khi trình biên dịch báo lỗi vì bạn cố gắng thay đổi giá trị của nó, bạn mới nên cân nhắc thêm `mut`.
2. **Tham số hàm luôn bất biến**: Không bao giờ được thay đổi giá trị của tham số truyền vào hàm.

### Khái niệm: "Hạn chế `mut` ở scope (phạm vi) nhỏ nhất"

Điều này có nghĩa là: **Đừng khai báo một biến `mut` ở đầu hàm nếu bạn chỉ cần thay đổi nó ở một đoạn code rất nhỏ bên dưới.** Việc giữ biến `mut` tồn tại trong thời gian dài làm tăng nguy cơ bạn vô tình thay đổi sai giá trị của nó ở những chỗ không mong muốn.

**Ví dụ:**

```text
// ❌ Không tốt: Khai báo biến khả_biến ở phạm vi quá rộng
hàm xu_ly_gio_hang() {
    biến khả_biến tong_tien = 0 // Khai báo tuốt trên này
    
    // ... rất nhiều dòng code không liên quan đến tong_tien ...
    // (Trong đó, ai đó có thể vô tình viết: tong_tien = -1)
    
    nếu (gio_hang_co_hang) {
        tong_tien = tinh_tien_gio_hang() // Chỉ thực sự cần đổi giá trị ở đây
        in_dòng_mới(tong_tien)
    }
}

// ✅ Tốt: Thu hẹp phạm vi khai báo
hàm xu_ly_gio_hang() {
    // ... rất nhiều dòng code ...
    
    nếu (gio_hang_co_hang) {
        biến tong_tien = tinh_tien_gio_hang() // Khai báo ngay tại nơi cần dùng, không cần `khả_biến`
        in_dòng_mới(tong_tien)
    }
}
```
*Lợi ích:* Code an toàn hơn, dễ theo dõi luồng dữ liệu hơn.

---

## 6. Null Safety conventions (Quy tắc an toàn Null)

Lỗi "Null Pointer" (cố gắng truy cập vào một giá trị không tồn tại) là lỗi phổ biến nhất trong lập trình gây sập chương trình. Nova giải quyết việc này bằng cách **cấm biến mang giá trị rỗng (`null` / `k_tồn_tại`) theo mặc định**.

### Làm rõ vấn đề

1. **Mặc định an toàn:** Biến thông thường bắt buộc phải có dữ liệu thực sự. Bạn không thể gán `null` cho nó.
2. **Khi nào dùng `?`**: Đôi khi trong thực tế, việc "không có dữ liệu" là hoàn toàn hợp lệ. Ví dụ: Tìm kiếm một người dùng trong cơ sở dữ liệu, có thể người đó không tồn tại. Lúc này, bạn thêm dấu `?` vào khai báo kiểu để nói cho trình biên dịch biết: *"Biến này có thể không có giá trị, hãy cẩn thận"*.
3. **Bắt buộc kiểm tra:** Khi bạn dùng một biến có dấu `?`, Nova sẽ ép bạn phải dùng lệnh `if` (nếu) để kiểm tra xem nó có bị `null` hay không trước khi được phép sử dụng.

### Ví dụ và Quy tắc

**1. Không lạm dụng `?` (Nullable) nếu không cần thiết:**

```text
// ❌ Sai: Tên người dùng luôn phải có, không nên cho phép rỗng
chuỗi? tenNguoiDung = "Nguyen Van A"

// ✅ Đúng:
chuỗi tenNguoiDung = "Nguyen Van A"
```

**2. Dùng `?` cho dữ liệu có thể vắng mặt hợp lý:**

```text
// ✅ Đúng: Tên đệm (middle name) có người có, có người không
chuỗi? tenDem = k_tồn_tại
```

**3. Luôn kiểm tra trước khi sử dụng:**

```text
hàm tim_nguoi_dung(số_nguyên id) -> chuỗi? {
    // Giả sử tìm không thấy
    trả_về k_tồn_tại
}

hàm main() -> trống {
    biến ketQua = tim_nguoi_dung(1)
    
    // ❌ Lỗi biên dịch: Bạn không thể dùng trực tiếp vì ketQua có thể là k_tồn_tại
    // in_dòng_mới("Tên là: " + ketQua) 
    
    // ✅ Đúng: Phải kiểm tra trước
    nếu (ketQua != k_tồn_tại) {
        in_dòng_mới("Tên là: " + ketQua)
    } ngược_lại {
        in_dòng_mới("Không tìm thấy người dùng")
    }
}
```
