package nova.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class InitCommand implements CliCommand {

    @Override
    public void execute(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Project name: ");
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            System.out.println("Project name cannot be empty!");
            return;
        }

        System.out.print("Programming language (vi/en)? ");
        String lang = scanner.nextLine().trim().toLowerCase();
        if (!lang.equals("vi") && !lang.equals("en")) {
            System.out.println("Invalid language! (only 'vi' or 'en' is accepted)");
            return;
        }

        java.nio.file.Path projectPath = Paths.get(projectName);
        if (Files.exists(projectPath)) {
            System.out.println("Directory " + projectName + " already exists!");
            return;
        }

        Files.createDirectories(projectPath.resolve("src"));
        Files.createDirectories(projectPath.resolve("resources"));

        String yamlConfig = "project:\n  language: " + lang + "\n";
        Files.writeString(projectPath.resolve("resources/application.yaml"), yamlConfig);

        String mainCode;
        if (lang.equals("vi")) {
            mainCode = "// Chương trình Nova\nhàm main() -> trống {\n    biến x = 10;\n    in_dòng_mới(\"Xin chào: \" + x);\n}\n\nmain();\n";
        } else {
            mainCode = "// Nova Program\nfunction main() -> void {\n    var x = 10;\n    println(\"Hello: \" + x);\n}\n\nmain();\n";
        }
        Files.writeString(projectPath.resolve("src/main.nova"), mainCode);

        // Khởi tạo git nếu có thể
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "--version");
            Process p = pb.start();
            if (p.waitFor() == 0) {
                System.out.println("Khởi tạo Git repository...");
                ProcessBuilder gitInit = new ProcessBuilder("git", "init");
                gitInit.directory(projectPath.toFile());
                gitInit.start().waitFor();

                // Tạo file .gitignore cơ bản cho dự án
                String gitignore = "out/\ndist/\n";
                Files.writeString(projectPath.resolve(".gitignore"), gitignore);
            }
        } catch (Exception e) {
            // Bỏ qua nếu git không được cài đặt
        }

        System.out.println("Successfully initialized project '" + projectName + "'!");
        System.out.println("You can run it with: nova run (nếu đã cài đặt biến môi trường) hoặc .\\nova.bat run");
    }
}
