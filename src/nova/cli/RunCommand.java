package nova.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

/**
 * Lệnh 'run' dùng để thực thi chương trình Nova.
 * Lệnh này sẽ tìm và chạy file 'main.nova' trong thư mục 'src' của dự án.
 */
public class RunCommand implements CliCommand {

    /**
     * Thực thi lệnh run.
     * Mặc định nó sẽ xác định file chạy chính là 'src/main.nova'.
     *
     * @param args Các đối số dòng lệnh.
     * @throws IOException Nếu có lỗi phát sinh trong quá trình đọc file mã nguồn.
     */
    @Override
    public void execute(String[] args) throws IOException {
        // Lấy đường dẫn hiện tại và thư mục 'src'
        var currentPath = Paths.get("").toAbsolutePath();
        var srcDir = currentPath.resolve("src");
        var mainFile = srcDir.resolve("main.nova");
        
        // Kiểm tra sự tồn tại của file chính
        if (!Files.exists(mainFile)) {
            System.out.println("Error: Cannot find src/main.nova. Please run this command from the project root.");
            return;
        }
        
        // Thực thi file mã nguồn
        runFile(mainFile.toString());
    }

    /**
     * Đọc và thực thi mã nguồn từ một file cụ thể.
     *
     * @param path Đường dẫn đến file mã nguồn cần thực thi.
     * @throws IOException Nếu không thể đọc được file.
     */
    public void runFile(String path) throws IOException {
        // Tự động thêm đuôi .nova nếu người dùng chưa nhập
        if (!path.endsWith(".nova")) {
            path += ".nova";
        }

        // Chuyển sang đường dẫn tuyệt đối
        var filePath = Paths.get(path).toAbsolutePath();
        
        // Xác định ngôn ngữ cấu hình cho dự án chứa file này
        var expectedLocale = CommandUtils.getExpectedLocale(filePath);

        // Đọc toàn bộ nội dung file và chuyển cho CommandUtils để thực thi
        byte[] bytes = Files.readAllBytes(filePath);
        CommandUtils.run(new String(bytes, StandardCharsets.UTF_8), expectedLocale);
    }
}
