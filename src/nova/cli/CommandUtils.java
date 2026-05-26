package nova.cli;

import nova.interpreter.Interpreter;
import nova.interpreter.Resolver;
import nova.lexer.Lexer;
import nova.lexer.Token;
import nova.lexer.TokenType;
import nova.parser.Parser;
import nova.utils.NovaLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp tiện ích cung cấp các phương thức dùng chung cho các lệnh CLI.
 * Chứa các hàm để đọc cấu hình ngôn ngữ, phân tích cú pháp và thực thi mã nguồn.
 */
public class CommandUtils {

    /**
     * Xác định ngôn ngữ (locale) được cấu hình cho dự án chứa file mã nguồn.
     * Hàm này đọc file 'resources/application.yaml' để tìm kiếm cấu hình ngôn ngữ ('vi' hoặc 'en').
     *
     * @param filePath Đường dẫn tới file mã nguồn (thường nằm trong thư mục 'src').
     * @return Chuỗi locale ('vi' hoặc 'en') nếu tìm thấy, ngược lại trả về null.
     * @throws IOException Nếu có lỗi khi đọc file cấu hình.
     */
    public static String getExpectedLocale(Path filePath) throws IOException {
        var parent = filePath.getParent();
        // Kiểm tra xem file có nằm trong thư mục 'src' không
        if (parent != null && parent.getFileName().toString().equals("src")) {
            var projectDir = parent.getParent();
            if (projectDir != null) {
                // Xây dựng đường dẫn tới file cấu hình yaml
                var yamlPath = projectDir.resolve("resources/application.yaml");
                if (Files.exists(yamlPath)) {
                    var yamlContent = Files.readString(yamlPath);
                    // Phân tích đơn giản nội dung yaml để lấy cấu hình ngôn ngữ
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

    /**
     * Phân tích mã nguồn Nova để kiểm tra lỗi cú pháp và lỗi phân giải (resolve).
     * Hàm này thực hiện phân tích từ vựng (lexing), cú pháp (parsing) và phân giải (resolving),
     * nhưng không thực thi (interpreting) mã nguồn.
     *
     * @param sourceCode Mã nguồn cần kiểm tra.
     * @param expectedLocale Ngôn ngữ sử dụng trong mã nguồn (vd: 'vi' hoặc 'en').
     * @return true nếu không có lỗi, false nếu có lỗi phân tích.
     */
    public static boolean analyzeCode(String sourceCode, String expectedLocale) {
        // Khởi tạo bộ phân tích từ vựng (Lexer) với ngôn ngữ cấu hình
        var lexer = (expectedLocale != null) ? new Lexer(sourceCode, expectedLocale) : new Lexer(sourceCode);
        List<Token> tokens = new ArrayList<>();
        try {
            // Quét tất cả các token từ mã nguồn
            while (true) {
                var token = lexer.nextToken();
                tokens.add(token);
                if (token.type() == TokenType.EOF) break;
            }
            // Khởi tạo bộ phân tích cú pháp (Parser)
            var parser = new Parser(tokens);
            var stmts = parser.parse();
            
            // Kiểm tra và in lỗi cú pháp nếu có
            if (parser.hasErrors()) {
                System.out.println("Phát hiện lỗi cú pháp:");
                for (var err : parser.getErrors()) {
                    System.out.println("Lỗi: " + err.getMessage());
                    NovaLogger.error("Parse Error in check: " + err.getMessage());
                }
                return false;
            } else {
                // Khởi tạo và chạy bộ phân giải biến (Resolver)
                var interpreter = new Interpreter();
                var resolver = new Resolver(interpreter);
                resolver.resolve(stmts);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Lỗi phân tích: " + e.getMessage());
            NovaLogger.error("Analyze error", e);
            return false;
        }
    }

    /**
     * Thực thi toàn bộ quá trình: phân tích từ vựng, cú pháp, phân giải và thực thi mã nguồn.
     *
     * @param sourceCode Mã nguồn cần thực thi.
     * @param expectedLocale Ngôn ngữ sử dụng trong mã nguồn.
     * @return true nếu thực thi thành công không có lỗi, ngược lại trả về false.
     */
    public static boolean run(String sourceCode, String expectedLocale) {
        // Khởi tạo bộ phân tích từ vựng (Lexer)
        var lexer = (expectedLocale != null) ? new Lexer(sourceCode, expectedLocale) : new Lexer(sourceCode);
        List<Token> tokens = new ArrayList<>();
        try {
            // Quét tất cả các token từ mã nguồn
            while (true) {
                var token = lexer.nextToken();
                tokens.add(token);
                if (token.type() == TokenType.EOF) break;
            }
            // Khởi tạo bộ phân tích cú pháp (Parser)
            var parser = new Parser(tokens);
            var stmts = parser.parse();
            
            // Nếu có lỗi cú pháp thì in ra và dừng thực thi
            if (parser.hasErrors()) {
                System.out.println("Phát hiện lỗi cú pháp:");
                for (var err : parser.getErrors()) {
                    System.out.println("Lỗi: " + err.getMessage());
                    NovaLogger.error("Parse Error in run: " + err.getMessage());
                }
                return false;
            } else {
                // Khởi tạo bộ thông dịch và phân giải
                var interpreter = new Interpreter();
                var resolver = new Resolver(interpreter);
                // Thực hiện phân giải để xác định tầm vực biến
                resolver.resolve(stmts);
                // Thực thi mã lệnh
                interpreter.interpret(stmts);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Lỗi thực thi: " + e.getMessage());
            NovaLogger.error("Run error", e);
            return false;
        }
    }
}
