package nova.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class CheckCommand implements CliCommand {

    @Override
    public void execute(String[] args) throws IOException {
        java.nio.file.Path currentPath = Paths.get("").toAbsolutePath();
        java.nio.file.Path srcDir = currentPath.resolve("src");
        java.nio.file.Path mainFile = srcDir.resolve("main.nova");
        
        if (!Files.exists(mainFile)) {
            System.out.println("Error: Cannot find src/main.nova. Please run this command from the project root.");
            return;
        }
        
        String expectedLocale = CommandUtils.getExpectedLocale(mainFile);
        byte[] bytes = Files.readAllBytes(mainFile);
        String sourceCode = new String(bytes, StandardCharsets.UTF_8);
        
        System.out.println("Đang kiểm tra (check) " + mainFile.getFileName() + "...");
        boolean success = CommandUtils.analyzeCode(sourceCode, expectedLocale);
        if (success) {
            System.out.println("✓ Kiểm tra thành công! Không có lỗi cú pháp.");
        } else {
            System.out.println("✗ Kiểm tra thất bại! Có lỗi trong mã nguồn.");
        }
    }
}
