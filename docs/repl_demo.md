# Hướng dẫn sử dụng Nova REPL

Nova REPL (Read-Eval-Print Loop) là môi trường tương tác giúp bạn gõ và chạy mã nguồn Nova trực tiếp, xem kết quả ngay lập tức.

## Cách chạy

Sau khi biên dịch dự án, bạn có thể chạy Nova REPL bằng lệnh:
```bash
java -cp out/production/untitled nova.repl.NovaRepl
```

Bạn sẽ thấy lời chào và con trỏ `>>>` chờ bạn nhập lệnh.

## Ví dụ sử dụng cơ bản

```text
Nova REPL v1.0. Gõ :help để xem trợ giúp, :exit để thoát.
>>> 1 + 2 * 3;
7
>>> biến a = 10;
>>> a * 2;
20
>>> print(a);
10
```

## Nhập lệnh nhiều dòng

Nova REPL hỗ trợ nhập nhiều dòng một cách thông minh. Nếu bạn mở một dấu ngoặc nhọn `{`, ngoặc vuông `[` hoặc ngoặc đơn `(`, REPL sẽ tự động tiếp tục đọc dòng tiếp theo và hiển thị con trỏ `... ` cho đến khi các dấu ngoặc được đóng lại.

```text
>>> nếu (a > 5) thì {
... print("Lớn hơn 5");
... }
Lớn hơn 5
```

Bạn cũng có thể dùng dấu chéo ngược `\` ở cuối dòng để bắt buộc xuống dòng mà không cần dấu ngoặc:

```text
>>> biến b = \
... 10 + 20;
>>> b;
30
```

## Các lệnh nội bộ

REPL cung cấp một số lệnh đặc biệt bắt đầu bằng dấu `:`:

- `:help` : Hiển thị bảng trợ giúp lệnh.
- `:vars` : Liệt kê tất cả các biến đang có trong môi trường toàn cục (không bao gồm các hàm dựng sẵn).
- `:reset` : Xóa toàn bộ biến môi trường hiện hành và khởi tạo lại trạng thái như lúc mới bật REPL.
- `:exit` : Thoát khỏi Nova REPL.
