package nova.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Lệnh 'init' dùng để khởi tạo một dự án Nova mới.
 * Lệnh này sẽ tạo cấu trúc thư mục chuẩn, các tệp cấu hình (application.yaml),
 * tệp mã nguồn ban đầu (main.nova) và tự động khởi tạo Git cho dự án.
 */
public class InitCommand implements CliCommand {

    /**
     * Thực thi lệnh khởi tạo dự án.
     * Phương thức này sẽ tương tác với người dùng qua console để lấy tên dự án
     * và ngôn ngữ lập trình.
     *
     * @param args Các đối số dòng lệnh (hiện không được sử dụng trực tiếp).
     * @throws IOException Nếu có lỗi phát sinh trong quá trình tạo file/thư mục.
     */
    @Override
    public void execute(String[] args) throws IOException {
        var scanner = new Scanner(System.in);
        
        // Yêu cầu người dùng nhập tên dự án
        System.out.print("Project name: ");
        var projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            System.out.println("Project name cannot be empty!");
            return;
        }

        // Yêu cầu người dùng chọn ngôn ngữ của dự án
        System.out.print("Programming language (vi/en)? ");
        var lang = scanner.nextLine().trim().toLowerCase();
        if (!lang.equals("vi") && !lang.equals("en")) {
            System.out.println("Invalid language! (only 'vi' or 'en' is accepted)");
            return;
        }

        // Xác định đường dẫn thư mục dự án
        var projectPath = Paths.get(projectName);
        if (Files.exists(projectPath)) {
            System.out.println("Directory " + projectName + " already exists!");
            return;
        }

        // Tạo cấu trúc thư mục 'src' (chứa mã nguồn) và 'resources' (chứa cấu hình)
        Files.createDirectories(projectPath.resolve("src"));
        Files.createDirectories(projectPath.resolve("resources"));

        // Tạo file cấu hình chứa ngôn ngữ mặc định của dự án
        var yamlConfig = "project:\n  language: " + lang + "\n";
        Files.writeString(projectPath.resolve("resources/application.yaml"), yamlConfig);

        // Tạo file main.nova có sẵn một chương trình đơn giản tùy theo ngôn ngữ
        String mainCode;
        if (lang.equals("vi")) {
            mainCode = "// Chương trình Nova\nhàm main() -> trống {\n    biến x = 10;\n    in_dòng_mới(\"Xin chào: \" + x);\n}\n\nmain();\n";
        } else {
            mainCode = "// Nova Program\nfunction main() -> void {\n    var x = 10;\n    println(\"Hello: \" + x);\n}\n\nmain();\n";
        }
        Files.writeString(projectPath.resolve("src/main.nova"), mainCode);

        // Khởi tạo git repository nếu hệ thống đã cài đặt git
        try {
            // Kiểm tra xem git có sẵn hay không
            var pb = new ProcessBuilder("git", "--version");
            var p = pb.start();
            if (p.waitFor() == 0) {
                System.out.println("Initializing Git repository...");
                // Chạy lệnh 'git init' trong thư mục dự án
                var gitInit = new ProcessBuilder("git", "init");
                gitInit.directory(projectPath.toFile());
                gitInit.start().waitFor();

                // Tạo file .gitignore cơ bản bỏ qua thư mục build
                var gitignore = "out/\ndist/\n";
                Files.writeString(projectPath.resolve(".gitignore"), gitignore);

                // Tạo file .gitattributes để thiết lập line ending thống nhất
                var gitattributes = """
                        # Nova source files
                        *.nova text eol=lf
                        
                        # Common settings
                        *.md text eol=lf
                        *.yaml text eol=lf
                        """;
                Files.writeString(projectPath.resolve(".gitattributes"), gitattributes);
            }
        } catch (Exception e) {
            // Bỏ qua nếu lệnh 'git' không tồn tại trên máy
        }

        System.out.println("Successfully initialized project '" + projectName + "'!");
        System.out.println("You can run it with: nova run (if PATH is configured) or .\\nova.bat run");
    }
}
