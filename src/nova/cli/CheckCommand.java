package nova.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

/**
 * Lệnh 'check' được sử dụng để kiểm tra cú pháp của mã nguồn Nova.
 * Lệnh này sẽ phân tích cú pháp (parse) và phân giải (resolve) biến/hàm
 * nhưng không thực thi (interpret) mã nguồn.
 */
public class CheckCommand implements CliCommand {

    /**
     * Thực thi lệnh kiểm tra mã nguồn.
     * Mặc định lệnh sẽ tìm kiếm và kiểm tra file src/main.nova.
     *
     * @param args Các đối số dòng lệnh (hiện tại không sử dụng).
     * @throws IOException Nếu có lỗi khi đọc file mã nguồn.
     */
    @Override
    public void execute(String[] args) throws IOException {
        // Lấy đường dẫn thư mục hiện tại nơi chạy lệnh
        var currentPath = Paths.get("").toAbsolutePath();
        var srcDir = currentPath.resolve("src");
        var mainFile = srcDir.resolve("main.nova");
        
        // Kiểm tra xem file src/main.nova có tồn tại không
        if (!Files.exists(mainFile)) {
            System.out.println("Error: Cannot find src/main.nova. Please run this command from the project root.");
            return;
        }

        // Lấy thông tin ngôn ngữ (locale) từ cấu hình dự án
        var expectedLocale = CommandUtils.getExpectedLocale(mainFile);
        
        // Đọc toàn bộ nội dung file mã nguồn
        byte[] bytes = Files.readAllBytes(mainFile);
        var sourceCode = new String(bytes, StandardCharsets.UTF_8);
        
        System.out.println("Đang kiểm tra (check) " + mainFile.getFileName() + "...");
        
        // Tiến hành phân tích mã nguồn
        var success = CommandUtils.analyzeCode(sourceCode, expectedLocale);
        if (success) {
            System.out.println("✓ Kiểm tra thành công! Không có lỗi cú pháp.");
        } else {
            System.out.println("✗ Kiểm tra thất bại! Có lỗi trong mã nguồn.");
        }
    }
}
