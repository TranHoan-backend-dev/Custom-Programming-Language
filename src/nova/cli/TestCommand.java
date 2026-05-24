package nova.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestCommand implements CliCommand {

    @Override
    public void execute(String[] args) throws IOException {
        java.nio.file.Path currentPath = Paths.get("").toAbsolutePath();
        java.nio.file.Path srcDir = currentPath.resolve("src");
        
        if (!Files.exists(srcDir)) {
            System.out.println("Error: Cannot find src/ directory. Please run this command from the project root.");
            return;
        }

        System.out.println("Đang tìm kiếm và chạy các file *_test.nova trong " + srcDir.getFileName() + "/ ...");
        try (java.util.stream.Stream<java.nio.file.Path> walk = Files.walk(srcDir)) {
            List<java.nio.file.Path> testFiles = walk
                .filter(path -> path.toString().endsWith("_test.nova"))
                .toList();

            if (testFiles.isEmpty()) {
                System.out.println("Không tìm thấy file kiểm thử nào (*_test.nova).");
                return;
            }

            int passed = 0;
            int failed = 0;

            for (java.nio.file.Path testFile : testFiles) {
                System.out.println("----------------------------------------");
                System.out.println("Chạy kiểm thử: " + testFile.getFileName());
                String expectedLocale = CommandUtils.getExpectedLocale(testFile);
                byte[] bytes = Files.readAllBytes(testFile);
                String sourceCode = new String(bytes, StandardCharsets.UTF_8);
                boolean success = CommandUtils.run(sourceCode, expectedLocale);
                if (success) {
                    System.out.println("✓ " + testFile.getFileName() + " - ĐẠT (PASS)");
                    passed++;
                } else {
                    System.out.println("✗ " + testFile.getFileName() + " - LỖI (FAIL)");
                    failed++;
                }
            }
            System.out.println("----------------------------------------");
            System.out.println("Tổng kết Test: " + passed + " đạt, " + failed + " lỗi.");
        }
    }
}
