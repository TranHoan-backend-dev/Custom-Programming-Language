// Import thư viện API của VS Code để có thể tương tác với Editor
const vscode = require('vscode');

/**
 * Hàm activate() được gọi MỘT LẦN duy nhất khi extension của bạn được kích hoạt.
 * VS Code sẽ tự động gọi hàm này khi người dùng mở file .nova (do ta đã cấu hình trong package.json)
 * @param {vscode.ExtensionContext} context - Chứa ngữ cảnh và trạng thái của extension
 */
function activate(context) {
    // Đăng ký một lệnh mới có tên là 'nova.run'. Lệnh này phải khớp với lệnh đã khai báo trong package.json
    let disposable = vscode.commands.registerCommand('nova.run', function () {
        
        // Lấy ra cửa sổ Editor đang được mở và focus hiện tại
        const editor = vscode.window.activeTextEditor;
        if (!editor) {
            // Nếu không có file nào đang mở, hiển thị thông báo lỗi nhỏ ở góc màn hình
            vscode.window.showErrorMessage('Không có file nào đang mở để chạy!');
            return;
        }

        // Lấy thông tin về tài liệu (file) đang mở trong Editor
        const document = editor.document;
        // Kiểm tra xem file này có đúng là ngôn ngữ 'nova' hay không (dựa trên đuôi .nova)
        if (document.languageId !== 'nova') {
            vscode.window.showErrorMessage('File hiện tại không phải là file Nova!');
            return;
        }

        // Tự động lưu file trước khi chạy để đảm bảo code mới nhất được thực thi
        document.save().then(() => {
            // Lấy đường dẫn tuyệt đối của file trên máy tính (VD: C:\Project\main.nova)
            const filePath = document.fileName;
            
            // Tìm xem đã có sẵn một Terminal (cửa sổ dòng lệnh) nào tên là "Nova" đang mở chưa
            let terminal = vscode.window.terminals.find(t => t.name === 'Nova');
            if (!terminal) {
                // Nếu chưa có, tạo một Terminal mới và đặt tên là "Nova"
                terminal = vscode.window.createTerminal('Nova');
            }
            
            // Hiển thị Terminal đó lên màn hình cho người dùng thấy
            terminal.show();
            
            // Gửi lệnh vào Terminal để chạy. 
            // Cú pháp lệnh: nova "C:\Đường dẫn\đến\file.nova"
            // Việc đặt dấu ngoặc kép ("") quanh filePath giúp xử lý an toàn các thư mục có dấu cách trong tên
            terminal.sendText(`nova "${filePath}"`);
        });
    });

    // Thêm lệnh vừa tạo vào danh sách quản lý của context.
    // Khi extension bị tắt (deactivate), VS Code sẽ tự động dọn dẹp và hủy đăng ký lệnh này để giải phóng bộ nhớ.
    context.subscriptions.push(disposable);
}

/**
 * Hàm deactivate() được gọi khi extension bị tắt hoặc khi đóng VS Code.
 * Hiện tại chúng ta không có tài nguyên đặc biệt nào cần dọn dẹp thủ công nên để trống.
 */
function deactivate() {}

// Xuất các hàm ra để VS Code có thể đọc và sử dụng được
module.exports = {
    activate,
    deactivate
}
