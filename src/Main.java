import nova.interpreter.Interpreter;
import nova.interpreter.Resolver;
import nova.lexer.Lexer;
import nova.lexer.TokenType;
import nova.lexer.Token;
import nova.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String cmd = args[0];
            switch (cmd) {
                case "init":
                    initProject();
                    return;
                case "run":
                    runApp();
                    return;
                case "check":
                    checkApp();
                    return;
                case "test":
                    testApp();
                    return;
                case "version":
                case "-v":
                case "--version":
                    printVersion();
                    return;
                case "help":
                case "-h":
                case "--help":
                    printHelp();
                    return;
                default:
                    // Chạy file trực tiếp
                    if (args.length > 1) {
                        System.out.println("Cách dùng: java Main [đường_dẫn_tới_file.nova]");
                        System.out.println("Gõ 'nova help' để xem thêm.");
                        System.exit(64);
                    } else {
                        runFile(args[0]);
                    }
                    return;
            }
        }

        System.out.println("Vui lòng cung cấp một lệnh hoặc đường dẫn tới file .nova để thực thi.");
        System.out.println("Ví dụ: java Main hello.nova");
        System.out.println("Gõ 'nova help' để xem các lệnh khả dụng.");
    }

    private static void printVersion() {
        System.out.println("Nova Language v1.0.0 - Interpreter");
    }

    private static void printHelp() {
        System.out.println("Nova Language CLI - Cách dùng:");
        System.out.println("  nova init       : Khởi tạo một dự án Nova mới (tạo cấu trúc thư mục và file cơ bản).");
        System.out.println("  nova run        : Chạy toàn bộ dự án từ file src/main.nova.");
        System.out.println("  nova check      : Kiểm tra lỗi cú pháp và định danh (không thực thi code).");
        System.out.println("  nova test       : Chạy các file kiểm thử (kết thúc bằng _test.nova) trong thư mục src/.");
        System.out.println("  nova version    : Hiển thị phiên bản Nova hiện tại.");
        System.out.println("  nova help       : Hiển thị hướng dẫn này.");
        System.out.println("  nova <file>     : Chạy một file .nova độc lập.");
    }

    private static void runApp() throws IOException {
        java.nio.file.Path currentPath = Paths.get("").toAbsolutePath();
        java.nio.file.Path srcDir = currentPath.resolve("src");
        java.nio.file.Path mainFile = srcDir.resolve("main.nova");
        
        if (!Files.exists(mainFile)) {
            System.out.println("Error: Cannot find src/main.nova. Please run this command from the project root.");
            return;
        }
        
        runFile(mainFile.toString());
    }

    private static void checkApp() throws IOException {
        java.nio.file.Path currentPath = Paths.get("").toAbsolutePath();
        java.nio.file.Path srcDir = currentPath.resolve("src");
        java.nio.file.Path mainFile = srcDir.resolve("main.nova");
        
        if (!Files.exists(mainFile)) {
            System.out.println("Error: Cannot find src/main.nova. Please run this command from the project root.");
            return;
        }
        
        String expectedLocale = getExpectedLocale(mainFile);
        byte[] bytes = Files.readAllBytes(mainFile);
        String sourceCode = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        
        System.out.println("Đang kiểm tra (check) " + mainFile.getFileName() + "...");
        boolean success = analyzeCode(sourceCode, expectedLocale);
        if (success) {
            System.out.println("✓ Kiểm tra thành công! Không có lỗi cú pháp.");
        } else {
            System.out.println("✗ Kiểm tra thất bại! Có lỗi trong mã nguồn.");
        }
    }

    private static void testApp() throws IOException {
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
                .collect(java.util.stream.Collectors.toList());

            if (testFiles.isEmpty()) {
                System.out.println("Không tìm thấy file kiểm thử nào (*_test.nova).");
                return;
            }

            int passed = 0;
            int failed = 0;

            for (java.nio.file.Path testFile : testFiles) {
                System.out.println("----------------------------------------");
                System.out.println("Chạy kiểm thử: " + testFile.getFileName());
                String expectedLocale = getExpectedLocale(testFile);
                byte[] bytes = Files.readAllBytes(testFile);
                String sourceCode = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
                boolean success = run(sourceCode, expectedLocale);
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

    private static String getExpectedLocale(java.nio.file.Path filePath) throws IOException {
        java.nio.file.Path parent = filePath.getParent();
        if (parent != null && parent.getFileName().toString().equals("src")) {
            java.nio.file.Path projectDir = parent.getParent();
            if (projectDir != null) {
                java.nio.file.Path yamlPath = projectDir.resolve("resources/application.yaml");
                if (Files.exists(yamlPath)) {
                    String yamlContent = new String(Files.readAllBytes(yamlPath), java.nio.charset.StandardCharsets.UTF_8);
                    if (yamlContent.contains("language: vi")) {
                        return "vi";
                    } else if (yamlContent.contains("language: en")) {
                        return "en";
                    }
                }
            }
        }
        return null;
    }

    private static void runFile(String path) throws IOException {
        if (!path.endsWith(".nova")) {
            path += ".nova";
        }

        java.nio.file.Path filePath = Paths.get(path).toAbsolutePath();
        String expectedLocale = getExpectedLocale(filePath);

        byte[] bytes = Files.readAllBytes(filePath);
        run(new String(bytes, java.nio.charset.StandardCharsets.UTF_8), expectedLocale);
    }

    private static void initProject() throws IOException {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
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
        Files.write(projectPath.resolve("resources/application.yaml"), yamlConfig.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        String mainCode = "";
        if (lang.equals("vi")) {
            mainCode = "// Chương trình Nova\nhàm main() -> trống {\n    biến x = 10;\n    in_dòng_mới(\"Xin chào: \" + x);\n}\n\nmain();\n";
        } else {
            mainCode = "// Nova Program\nfunction main() -> void {\n    var x = 10;\n    println(\"Hello: \" + x);\n}\n\nmain();\n";
        }
        Files.write(projectPath.resolve("src/main.nova"), mainCode.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        System.out.println("Successfully initialized project '" + projectName + "'!");
        System.out.println("You can run it with: .\\nova.bat run");
    }

    private static boolean analyzeCode(String sourceCode, String expectedLocale) {
        var lexer = (expectedLocale != null) ? new Lexer(sourceCode, expectedLocale) : new Lexer(sourceCode);
        List<Token> tokens = new java.util.ArrayList<>();
        try {
            while (true) {
                var token = lexer.nextToken();
                tokens.add(token);
                if (token.type() == TokenType.EOF) break;
            }
            var parser = new Parser(tokens);
            var stmts = parser.parse();
            if (parser.hasErrors()) {
                System.out.println("Phát hiện lỗi cú pháp:");
                for (var err : parser.getErrors()) {
                    System.out.println("Lỗi: " + err.getMessage());
                }
                return false;
            } else {
                var interpreter = new Interpreter();
                var resolver = new Resolver(interpreter);
                resolver.resolve(stmts);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Lỗi phân tích: " + e.getMessage());
            return false;
        }
    }

    private static boolean run(String sourceCode, String expectedLocale) {
        var lexer = (expectedLocale != null) ? new Lexer(sourceCode, expectedLocale) : new Lexer(sourceCode);
        List<Token> tokens = new java.util.ArrayList<>();
        try {
            while (true) {
                var token = lexer.nextToken();
                tokens.add(token);
                if (token.type() == TokenType.EOF) break;
            }
            var parser = new Parser(tokens);
            var stmts = parser.parse();
            if (parser.hasErrors()) {
                System.out.println("Phát hiện lỗi cú pháp:");
                for (var err : parser.getErrors()) {
                    System.out.println("Lỗi: " + err.getMessage());
                }
                return false;
            } else {
                var interpreter = new Interpreter();
                var resolver = new Resolver(interpreter);
                resolver.resolve(stmts);
                interpreter.interpret(stmts);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Lỗi thực thi: " + e.getMessage());
            // e.printStackTrace();
            return false;
        }
    }
}