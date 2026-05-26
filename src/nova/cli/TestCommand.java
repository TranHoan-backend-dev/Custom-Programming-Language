package nova.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

/**
 * Lệnh 'test' sử dụng để chạy toàn bộ các kịch bản kiểm thử (test) của dự án.
 * Lệnh này sẽ tìm tất cả các file có đuôi '_test.nova' trong thư mục 'src' và thực thi chúng.
 */
public class TestCommand implements CliCommand {

    /**
     * Thực thi lệnh chạy kiểm thử.
     * Quét tất cả các file kiểm thử, chạy lần lượt và tổng hợp kết quả.
     *
     * @param args Các tham số dòng lệnh.
     * @throws IOException Nếu có lỗi xảy ra khi đọc file hoặc duyệt thư mục.
     */
    @Override
    public void execute(String[] args) throws IOException {
        // Lấy đường dẫn thư mục hiện tại và thư mục 'src'
        var currentPath = Paths.get("").toAbsolutePath();
        var srcDir = currentPath.resolve("src");

        // Kiểm tra xem thư mục src có tồn tại hay không
        if (!Files.exists(srcDir)) {
            System.out.println("Error: Cannot find src/ directory. Please run this command from the project root.");
            return;
        }

        System.out.println("Đang tìm kiếm và chạy các file *_test.nova trong " + srcDir.getFileName() + "/ ...");

        // Duyệt toàn bộ cấu trúc thư mục 'src' để tìm các file kiểm thử
        try (Stream<Path> walk = Files.walk(srcDir)) {
            // Lọc ra danh sách các file kết thúc bằng '_test.nova'
            List<Path> testFiles = walk
                    .filter(path -> path.toString().endsWith("_test.nova"))
                    .toList();

            // Nếu không có file test nào
            if (testFiles.isEmpty()) {
                System.out.println("Không tìm thấy file kiểm thử nào (*_test.nova).");
                return;
            }

            var passed = 0;
            var failed = 0;

            // Thực thi từng file kiểm thử một
            for (var testFile : testFiles) {
                System.out.println("----------------------------------------");
                System.out.println("Chạy kiểm thử: " + testFile.getFileName());

                // Đọc ngôn ngữ cấu hình của file kiểm thử
                var expectedLocale = CommandUtils.getExpectedLocale(testFile);

                // Đọc mã nguồn file kiểm thử
                byte[] bytes = Files.readAllBytes(testFile);
                var sourceCode = new String(bytes, StandardCharsets.UTF_8);

                // Chạy chương trình và lấy kết quả trả về
                var success = CommandUtils.run(sourceCode, expectedLocale);
                if (success) {
                    System.out.println("✓ " + testFile.getFileName() + " - ĐẠT (PASS)");
                    passed++;
                } else {
                    System.out.println("✗ " + testFile.getFileName() + " - LỖI (FAIL)");
                    failed++;
                }
            }

            // In tổng kết kết quả kiểm thử
            System.out.println("----------------------------------------");
            System.out.println("Tổng kết Test: " + passed + " đạt, " + failed + " lỗi.");
        }
    }
}
