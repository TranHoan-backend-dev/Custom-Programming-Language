package nova.cli;

import nova.interpreter.Interpreter;
import nova.interpreter.Resolver;
import nova.lexer.Lexer;
import nova.lexer.Token;
import nova.lexer.TokenType;
import nova.parser.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CommandUtils {

    public static String getExpectedLocale(Path filePath) throws IOException {
        Path parent = filePath.getParent();
        if (parent != null && parent.getFileName().toString().equals("src")) {
            Path projectDir = parent.getParent();
            if (projectDir != null) {
                Path yamlPath = projectDir.resolve("resources/application.yaml");
                if (Files.exists(yamlPath)) {
                    String yamlContent = Files.readString(yamlPath);
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

    public static boolean analyzeCode(String sourceCode, String expectedLocale) {
        var lexer = (expectedLocale != null) ? new Lexer(sourceCode, expectedLocale) : new Lexer(sourceCode);
        List<Token> tokens = new ArrayList<>();
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

    public static boolean run(String sourceCode, String expectedLocale) {
        var lexer = (expectedLocale != null) ? new Lexer(sourceCode, expectedLocale) : new Lexer(sourceCode);
        List<Token> tokens = new ArrayList<>();
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
            return false;
        }
    }
}
