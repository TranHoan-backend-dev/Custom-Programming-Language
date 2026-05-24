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
        if (args.length > 0 && args[0].equals("init")) {
            initProject();
            return;
        }

        if (args.length > 1) {
            System.out.println("Cách dùng: java Main [đường_dẫn_tới_file.nova]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            System.out.println("Vui lòng cung cấp đường dẫn tới file .nova để thực thi.");
            System.out.println("Ví dụ: java Main hello.nova");
        }
    }

    private static void runFile(String path) throws IOException {
        if (!path.endsWith(".nova")) {
            path += ".nova";
        }

        java.nio.file.Path filePath = Paths.get(path).toAbsolutePath();
        java.nio.file.Path parent = filePath.getParent();
        String expectedLocale = null;

        // Cố gắng tìm resources/application.yaml
        if (parent != null && parent.getFileName().toString().equals("src")) {
            java.nio.file.Path projectDir = parent.getParent();
            if (projectDir != null) {
                java.nio.file.Path yamlPath = projectDir.resolve("resources/application.yaml");
                if (Files.exists(yamlPath)) {
                    String yamlContent = new String(Files.readAllBytes(yamlPath), java.nio.charset.StandardCharsets.UTF_8);
                    if (yamlContent.contains("language: vi")) {
                        expectedLocale = "vi";
                    } else if (yamlContent.contains("language: en")) {
                        expectedLocale = "en";
                    }
                }
            }
        }

        byte[] bytes = Files.readAllBytes(Paths.get(path));
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
            mainCode = "// Chương trình Nova\nbiến x = 10;\nprintln(\"Xin chào: \" + x);\n";
        } else {
            mainCode = "// Nova Program\nvar x = 10;\nprintln(\"Hello: \" + x);\n";
        }
        Files.write(projectPath.resolve("src/main.nova"), mainCode.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        System.out.println("Successfully initialized project '" + projectName + "'!");
        System.out.println("You can run it with: .\\nova.bat " + projectName + "/src/main.nova");
    }

    private static void run(String sourceCode, String expectedLocale) {
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
            } else {
                var interpreter = new Interpreter();
                var resolver = new Resolver(interpreter);
                resolver.resolve(stmts);
                interpreter.interpret(stmts);
            }
        } catch (Exception e) {
            System.err.println("Lỗi thực thi: " + e.getMessage());
            e.printStackTrace();
        }
    }
}