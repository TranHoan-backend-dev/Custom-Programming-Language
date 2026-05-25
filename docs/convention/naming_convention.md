# 📛 Quy tắc đặt tên (Naming Convention)

[« Quay lại README](../../README.md)

---

> [!NOTE]
> Tài liệu này mô tả các quy tắc đặt tên **khuyến nghị** cho ngôn ngữ Nova.
> Tuân thủ đúng quy tắc giúp mã nguồn dễ đọc, nhất quán và dễ bảo trì trong toàn bộ dự án.

### Bảng tóm tắt

| Thành phần | Tiếng Anh | Tiếng Việt | Ví dụ EN | Ví dụ VI |
|:---|:---|:---|:---|:---|
| File name | `PascalCase` | `PascalCase` | `MathUtils.nova` | `TienIch.nova` |
| Function name | `snake_case` | `snake_case` | `get_user_by_id()` | `lay_nguoi_dung_theo_id()` |
| Variable name | `camelCase` | `camelCase` | `totalCount` | `tongSoLuong` |
| Constant name | `UPPER_SNAKE_CASE` | `UPPER_SNAKE_CASE` | `MAX_RETRY` | `SO_LAN_THU_LAI` |

---

## 1. File name

Tên file `.nova` sử dụng **PascalCase** — viết hoa chữ cái đầu mỗi từ, không dùng dấu gạch dưới hay gạch ngang.

### Ví dụ

| ✅ Đúng | ❌ Sai |
|:---|:---|
| `MathUtils.nova` | `math_utils.nova` |
| `UserService.nova` | `user-service.nova` |
| `StringHelper.nova` | `stringHelper.nova` |
| `HttpClient.nova` | `httpClient.nova` |

### Quy tắc

- Mỗi từ trong tên file viết hoa chữ cái đầu: `OrderDetail.nova`, `PaymentGateway.nova`.
- Không dùng dấu cách, dấu gạch ngang (`-`), hoặc ký tự đặc biệt.
- Chỉ dùng chữ cái (không dấu), chữ số. Không bắt đầu bằng số.
- Tiếng Việt áp dụng tương tự: `DonHang.nova`, `TienIch.nova`.

### Trường hợp đặc biệt

| File | Tên | Lý do |
|:---|:---|:---|
| Entry point | `main.nova` | Quy ước bắt buộc — luôn viết thường. |
| File test | `*_test.nova` | Kết thúc bằng `_test` (ví dụ: `MathUtils_test.nova`). |

---

## 2. Function name

Tên hàm sử dụng **snake_case** — tất cả chữ thường, các từ phân tách bằng dấu gạch dưới `_`.

### Ví dụ

**Tiếng Anh:**

```text
function calculate_total(int price, int quantity) -> int {
    return price * quantity
}

function get_user_by_id(int id) -> string { ... }
function is_valid_email(string email) -> bool { ... }
function send_notification() -> void { ... }
```

**Tiếng Việt:**

```text
hàm tinh_tong(số_nguyên gia, số_nguyên so_luong) -> số_nguyên {
    trả_về gia * so_luong
}

hàm lay_nguoi_dung_theo_id(số_nguyên id) -> chuỗi { ... }
hàm la_email_hop_le(chuỗi email) -> đúng_sai { ... }
hàm gui_thong_bao() -> trống { ... }
```

### Quy tắc

- Tất cả chữ thường, phân tách từ bằng `_`.
- Tên hàm phải là **động từ** hoặc **cụm động từ** mô tả hành động.
- Tiếng Việt: dùng tiếng Việt **không dấu** (ví dụ: `tinh_tong`, không phải `tính_tổng`).
- Không bắt đầu bằng số.

### Gợi ý đặt tên hàm

| Loại hàm | Prefix EN | Prefix VI | Ví dụ |
|:---|:---|:---|:---|
| Lấy dữ liệu | `get_` | `lay_` | `get_name()`, `lay_ten()` |
| Gán dữ liệu | `set_` | `gan_` | `set_name()`, `gan_ten()` |
| Kiểm tra boolean | `is_`, `has_`, `can_` | `la_`, `co_`, `co_the_` | `is_active()`, `la_hoat_dong()` |
| Tính toán | `calculate_`, `compute_` | `tinh_` | `calculate_tax()`, `tinh_thue()` |
| Chuyển đổi | `to_`, `convert_` | `sang_`, `chuyen_` | `to_string()`, `sang_chuoi()` |

---

## 3. Variable name

Tên biến sử dụng **camelCase** — chữ cái đầu viết thường, viết hoa chữ cái đầu của các từ tiếp theo. Áp dụng cho **cả tiếng Anh và tiếng Việt**.

### Ví dụ

**Tiếng Anh:**

```text
var userName = "An"
int mut totalCount = 0
string? emailAddress = null
bool isActive = true
```

**Tiếng Việt:**

```text
biến tenNguoiDung = "An"
số_nguyên khả_biến tongSoLuong = 0
chuỗi? diaChiEmail = k_tồn_tại
đúng_sai laHoatDong = đúng
```

### Quy tắc

- Chữ cái đầu tiên viết **thường**, các từ tiếp theo viết hoa chữ cái đầu.
- Tên biến phải là **danh từ** hoặc **cụm danh từ** mô tả dữ liệu chứa bên trong.
- Tiếng Việt: dùng tiếng Việt **không dấu** (ví dụ: `tongGia`, không phải `tổngGiá`).
- Không bắt đầu bằng số. Tránh tên 1 ký tự (trừ biến đếm `i`, `j`, `k`).

### Gợi ý đặt tên biến

| Loại | Ví dụ EN | Ví dụ VI |
|:---|:---|:---|
| Thông thường | `userName`, `orderTotal` | `tenNguoiDung`, `tongDonHang` |
| Boolean | `isValid`, `hasPermission` | `laHopLe`, `coQuyen` |
| Nullable | `middleName?` | `tenDem?` |
| Số đếm | `itemCount`, `retryLimit` | `soLuongSanPham`, `gioiHanThuLai` |
| Biến đếm vòng lặp | `i`, `j`, `k`, `index` | `i`, `j`, `k`, `chiSo` |

---

## 4. Constant name

Hằng số sử dụng **UPPER_SNAKE_CASE** — tất cả chữ hoa, các từ phân tách bằng dấu gạch dưới `_`. Áp dụng cho **cả tiếng Anh và tiếng Việt**.

### Ví dụ

**Tiếng Anh:**

```text
const int MAX_RETRY_COUNT = 3
const string API_BASE_URL = "https://api.example.com"
const double PI = 3.14159
const int HTTP_STATUS_OK = 200
```

**Tiếng Việt:**

```text
hằng_số số_nguyên SO_LAN_THU_LAI_TOI_DA = 3
hằng_số chuỗi DUONG_DAN_GOC_API = "https://api.example.com"
hằng_số số_thực_kép PI = 3.14159
hằng_số số_nguyên MA_TRANG_THAI_OK = 200
```

### Quy tắc

- Tất cả chữ **HOA**, phân tách từ bằng `_`.
- Tên hằng phải mô tả rõ ý nghĩa — tránh viết tắt mơ hồ.
- Tiếng Việt: dùng tiếng Việt **không dấu**, viết hoa toàn bộ.

| ✅ Đúng | ❌ Sai | Lý do |
|:---|:---|:---|
| `MAX_VALUE` | `maxValue` | Hằng số phải viết hoa toàn bộ |
| `API_TIMEOUT` | `X` | Tên quá ngắn, không rõ nghĩa |
| `SO_LAN_TOI_DA` | `soLanToiDa` | Hằng số tiếng Việt cũng phải UPPER_SNAKE_CASE |

---

## 5. Vietnamese vs English

Nova hỗ trợ song ngữ, nhưng **không được trộn lẫn** hai ngôn ngữ trong cùng một file.

### Quy tắc bắt buộc

| # | Quy tắc | Mô tả |
|:---|:---|:---|
| 1 | **Một file, một ngôn ngữ** | Một file `.nova` chỉ được sử dụng từ khóa của đúng một ngôn ngữ (Anh hoặc Việt). |
| 2 | **Tên do dev đặt: không dấu** | Tên biến, hàm, hằng do lập trình viên đặt phải dùng tiếng Việt **không dấu**. |
| 3 | **Từ khóa ngôn ngữ: giữ nguyên** | Chỉ các từ khóa hệ thống (`biến`, `hàm`, `khả_biến`, `trả_về`,...) mới được có dấu. |

### Ví dụ sai — trộn lẫn ngôn ngữ

```text
// ❌ SAI: Trộn từ khóa tiếng Anh ("function", "int") và tiếng Việt ("trả_về")
function tinh_tong(int a, int b) -> int {
    trả_về a + b
}
```

### Ví dụ đúng — nhất quán ngôn ngữ

```text
// ✅ Toàn bộ tiếng Anh
function calculate_sum(int a, int b) -> int {
    return a + b
}

// ✅ Toàn bộ tiếng Việt
hàm tinh_tong(số_nguyên a, số_nguyên b) -> số_nguyên {
    trả_về a + b
}
```

### Khuyến nghị chọn ngôn ngữ

| Tình huống | Khuyến nghị |
|:---|:---|
| Dự án team / open source | Tiếng Anh |
| Học tập / giảng dạy cho người Việt | Tiếng Việt |
| Dự án cá nhân | Tùy chọn, nhưng phải nhất quán trong toàn bộ file |
