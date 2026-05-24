package nova.interpreter;

import nova.lexer.Lexer;
import nova.lexer.Token;
import nova.lexer.TokenType;
import nova.lexer.LexerError;
import nova.ast.Stmt;
import nova.parser.Parser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp kiểm thử tích hợp toàn diện cho bộ thông dịch (Interpreter),
 * bộ giải quyết phạm vi (Resolver) và trình kiểm tra kiểu tĩnh (Type Checker) của ngôn ngữ Nova.
 * Chứa các ca kiểm thử bao phủ toàn bộ các tính năng từ Phần I đến Phần XI trong tài liệu README.md.
 * Cung cấp đầy đủ các kịch bản thành công (happy cases) và các trường hợp biên/lỗi tĩnh/lỗi chạy (edge/error cases).
 *
 * @author XUAN HOAN
 */
public class InterpreterTest {

    /**
     * Lớp đại diện cho kết quả thực thi một đoạn mã nguồn.
     * Lưu trữ dữ liệu in ra stdout, stderr, và trạng thái lỗi tĩnh/động.
     */
    private static class ExecutionResult {
        /**
         * Dữ liệu ghi nhận từ System.out.
         */
        final String stdout;
        /**
         * Dữ liệu ghi nhận từ System.err hoặc thông báo lỗi tĩnh.
         */
        final String error;
        /**
         * Cờ đánh dấu có lỗi phân tích tĩnh (Lexer/Parser/Resolver/TypeChecker).
         */
        final boolean isStaticError;
        /**
         * Cờ đánh dấu có lỗi thời gian chạy (RuntimeError).
         */
        final boolean isRuntimeError;

        /**
         * Khởi tạo kết quả thực thi mới.
         *
         * @param stdout         Chuỗi kết quả xuất ra console chuẩn
         * @param error          Chuỗi thông tin lỗi thu thập được
         * @param isStaticError  Trạng thái lỗi phân tích tĩnh
         * @param isRuntimeError Trạng thái lỗi thời gian chạy
         */
        ExecutionResult(String stdout, String error, boolean isStaticError, boolean isRuntimeError) {
            this.stdout = stdout;
            this.error = error;
            this.isStaticError = isStaticError;
            this.isRuntimeError = isRuntimeError;
        }
    }

    /**
     * Phương thức chạy chính thực hiện lần lượt tất cả các nhóm kịch bản kiểm thử.
     *
     * @param args Đối số dòng lệnh (không sử dụng)
     */
    public static void main(String[] args) {
        System.out.println("BẮT ĐẦU CHẠY BỘ KIỂM THỬ INTERPRETER TOÀN DIỆN...");

        try {
            testPart1EntryPoint();
            testPart2Output();
            testPart3Comments();
            testPart4DataTypesAndNullSafety();
            testPart5VariablesAndConstants();
            testPart6Operators();
            testPart7IfElse();
            testPart8SwitchCase();
            testPart9Loops();
            testPart10Functions();
            testPart11Strings();

            System.out.println("\nTẤT CẢ CÁC CA KIỂM THỬ INTERPRETER ĐÃ VƯỢT QUA THÀNH CÔNG (100%)!");
        } catch (Throwable t) {
            System.err.println("\nKIỂM THỬ INTERPRETER THẤT BẠI:");
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Chạy mã nguồn Nova qua luồng phân tích từ vựng, cú pháp, giải quyết phạm vi, kiểm tra kiểu tĩnh và thông dịch.
     * Thu thập toàn bộ đầu ra từ System.out hoặc danh sách lỗi tĩnh/động nếu có.
     *
     * @param source Mã nguồn Nova cần thực thi
     * @return Kết quả chứa thông tin đầu ra, lỗi tĩnh hoặc lỗi runtime
     */
    private static ExecutionResult runSource(String source) {
        var outBaos = new ByteArrayOutputStream();
        var errBaos = new ByteArrayOutputStream();
        var originalOut = System.out;
        var originalErr = System.err;

        PrintStream capturedOut;
        PrintStream capturedErr;
        capturedOut = new PrintStream(outBaos, true, StandardCharsets.UTF_8);
        capturedErr = new PrintStream(errBaos, true, StandardCharsets.UTF_8);

        try {
            // Lexer
            var lexer = new Lexer(source);
            List<Token> tokens = new ArrayList<>();
            while (true) {
                var t = lexer.nextToken();
                tokens.add(t);
                if (t.type() == TokenType.EOF) break;
            }

            // Parser
            var parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();
            if (parser.hasErrors()) {
                var sb = new StringBuilder();
                for (var err : parser.getErrors()) {
                    sb.append(err.getMessage()).append("\n");
                }
                return new ExecutionResult(null, sb.toString().trim(), true, false);
            }

            // Interpreter
            var interpreter = new Interpreter(capturedOut);

            // Resolver
            var resolver = new Resolver(interpreter);
            List<SemanticError> resolverErrors = resolver.resolve(statements);
            if (resolver.hasErrors()) {
                var sb = new StringBuilder();
                for (var err : resolverErrors) {
                    sb.append(err.toString()).append("\n");
                }
                return new ExecutionResult(null, sb.toString().trim(), true, false);
            }

            // TypeChecker
            var typeChecker = new TypeChecker(interpreter);
            // Đăng ký biến list toàn cục dùng cho các kịch bản kiểm thử duyệt danh sách
            typeChecker.defineGlobalType("list", "DanhSách<số_nguyên>");
            interpreter.globals.define("list", java.util.List.of(1, 2, 3));

            List<SemanticError> typeErrors = typeChecker.check(statements);
            if (typeChecker.hasErrors()) {
                var sb = new StringBuilder();
                for (var err : typeErrors) {
                    sb.append(err.toString()).append("\n");
                }
                return new ExecutionResult(null, sb.toString().trim(), true, false);
            }

            // Redirect stdout/stderr sang stream tạm
            System.setOut(capturedOut);
            System.setErr(capturedErr);

            // Chạy trình thông dịch
            interpreter.interpret(statements);

            // Nếu người dùng định nghĩa hàm main() mà không tự gọi, tự động thực thi hàm main()
            var mainFunc = interpreter.globals.getByName("main");
            if (mainFunc instanceof NovaCallable callable) {
                if (callable.arity() == 0) {
                    try {
                        callable.call(interpreter, new ArrayList<>());
                    } catch (RuntimeError e) {
                        capturedErr.println(e.getMessage());
                    }
                }
            }

            capturedOut.flush();
            capturedErr.flush();

            var outStr = outBaos.toString(StandardCharsets.UTF_8).trim();
            var errStr = errBaos.toString(StandardCharsets.UTF_8).trim();

            return new ExecutionResult(outStr, errStr, false, !errStr.isEmpty());

        } catch (LexerError e) {
            return new ExecutionResult(null, e.getMessage(), true, false);
        } catch (Exception e) {
            return new ExecutionResult(null, e.getMessage(), false, true);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

    /**
     * Kiểm thử Phần I: Cú pháp khởi chạy (Entry Point).
     * Kiểm tra thực thi hàm main() khai báo theo cả tiếng Anh và tiếng Việt.
     */
    private static void testPart1EntryPoint() {
        System.out.print("Đang chạy kiểm thử Phần I (Entry Point)... ");

        // Case 1: Tiếng Anh
        var src1 = """
                function main() -> void {
                    print("Main English run");
                }
                """;
        var r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertFalse(r1.isRuntimeError);
        InterpreterAssert.assertEquals("Main English run", r1.stdout);

        // Case 2: Tiếng Việt
        var src2 = """
                hàm main() -> trống {
                    print("Main Việt chạy");
                }
                """;
        var r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertFalse(r2.isRuntimeError);
        InterpreterAssert.assertEquals("Main Việt chạy", r2.stdout);

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần II: Xuất dữ liệu (Output).
     * Kiểm tra in ra stdout bằng in/print/println/in_dòng_mới/printf/in_định_dạng và nội suy chuỗi.
     */
    private static void testPart2Output() {
        System.out.print("Đang chạy kiểm thử Phần II (Output)... ");

        // Case 1: Lệnh in cơ bản tiếng Anh và tiếng Việt
        var src1 = """
                print("Hello", 123);
                println();
                in("Việt Nam", true);
                in_dòng_mới();
                """;
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("Hello 123\nViệt Nam true", r1.stdout.replace("\r\n", "\n"));

        // Case 2: printf và in_định_dạng theo vị trí {}
        var src2 = """
                printf("Xin chào {} và {}", "Nova", 2026);
                in_định_dạng(" - Điểm số: {}", 9.5);
                """;
        var r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("Xin chào Nova và 2026 - Điểm số: 9.5", r2.stdout);

        // Case 3: Nội suy trực tiếp chuỗi chứa {tên_biến}
        var src3 = """
                biến tên = "Nova"
                biến năm = 2026
                print("Ngôn ngữ {tên} ra mắt năm {năm}")
                """;
        var r3 = runSource(src3);
        InterpreterAssert.assertFalse(r3.isStaticError);
        InterpreterAssert.assertEquals("Ngôn ngữ Nova ra mắt năm 2026", r3.stdout);

        // Case 4: Định dạng thiếu tham số (giữ nguyên {})
        var src4 = """
                printf("Chỉ có {} và {}");
                """;
        var r4 = runSource(src4);
        InterpreterAssert.assertEquals("Chỉ có {} và {}", r4.stdout);

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần III: Chú thích (Comments).
     * Đảm bảo chú thích một dòng, nhiều dòng, tài liệu và `#region` được bỏ qua chính xác.
     */
    private static void testPart3Comments() {
        System.out.print("Đang chạy kiểm thử Phần III (Comments)... ");

        var src = """
                // Đây là chú thích 1 dòng
                biến x = 10; // chú thích sau code
                /* Chú thích nhiều dòng
                   bên trong block */
                /** Chú thích tài liệu Javadoc */
                /// mô_tả: Phân nhóm code
                print(x);
                ///
                """;
        var r = runSource(src);
        InterpreterAssert.assertFalse(r.isStaticError);
        InterpreterAssert.assertEquals("10", r.stdout);

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần IV: Kiểu dữ liệu và An toàn Null (Data Types & Null Safety).
     * Kiểm tra gán trị hợp lệ cho nullable, smart casting sau check null và chặn lỗi gán sai kiểu tĩnh.
     */
    private static void testPart4DataTypesAndNullSafety() {
        System.out.print("Đang chạy kiểm thử Phần IV (Null Safety & Smart Casting)... ");

        // Case 1: Khai báo nullable và gán null hợp lệ
        String src1 = """
                biến chuỗi? s = k_tồn_tại;
                print(s);
                """;
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("null", r1.stdout);

        // Case 2: Smart casting kiểu nullable -> phi nullable sau điều kiện khác null
        String src2 = """
                biến chuỗi? s = "hello";
                nếu (s != k_tồn_tại) thì {
                    // Ở đây s được smart cast sang kiểu chuỗi (không nullable)
                    print(s.length());
                }
                """;
        ExecutionResult r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("5", r2.stdout);

        // Case 3: Lỗi tĩnh khi gán null cho biến không nullable
        String src3 = """
                biến chuỗi s = k_tồn_tại;
                """;
        ExecutionResult r3 = runSource(src3);
        InterpreterAssert.assertTrue(r3.isStaticError);

        // Case 4: Lỗi tĩnh khi truy xuất phương thức trên nullable mà chưa check null
        String src4 = """
                biến chuỗi? s = "test";
                print(s.length());
                """;
        ExecutionResult r4 = runSource(src4);
        InterpreterAssert.assertTrue(r4.isStaticError);
        InterpreterAssert.assertTrue(r4.error.contains("Không thể gọi thuộc tính hoặc phương thức 'length' trên đối tượng nullable"));

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần V: Biến & Hằng số (Variables & Constants).
     * Kiểm tra hành vi khả biến (mut/khả_biến), bất biến mặc định, hằng số (const/hằng_số), và các lỗi khai báo/gán tĩnh.
     *
     * @throws AssertionError nếu một khẳng định kiểm thử thất bại
     */
    private static void testPart5VariablesAndConstants() {
        System.out.print("Đang chạy kiểm thử Phần V (Variables & Constants)... ");

        // Case 1: Khai biến khả biến và gán lại giá trị
        String src1 = """
                biến khả_biến x = 10;
                x = 20;
                print(x);
                """;
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("20", r1.stdout);

        // Case 2: Biến bất biến mặc định
        String src2 = """
                biến x = 100;
                print(x);
                """;
        ExecutionResult r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("100", r2.stdout);

        // Case 3: Hằng số
        String src3 = """
                hằng_số PI = 3.14;
                print(PI);
                """;
        ExecutionResult r3 = runSource(src3);
        InterpreterAssert.assertFalse(r3.isStaticError);
        InterpreterAssert.assertEquals("3.14", r3.stdout);

        // Case 4: Lỗi tĩnh khi gán lại biến bất biến
        String src4 = """
                biến x = 10;
                x = 20;
                """;
        ExecutionResult r4 = runSource(src4);
        InterpreterAssert.assertTrue(r4.isStaticError);
        InterpreterAssert.assertTrue(r4.error.contains("Biến bất biến"));

        // Case 5: Lỗi tĩnh khi gán lại hằng số
        String src5 = """
                hằng_số X = 5;
                X = 10;
                """;
        ExecutionResult r5 = runSource(src5);
        InterpreterAssert.assertTrue(r5.isStaticError);
        InterpreterAssert.assertTrue(r5.error.contains("Hằng số"));

        // Case 6: Lỗi tĩnh khi khai báo trùng lặp biến trong cùng một block
        String src6 = """
                {
                    biến a = 1;
                    biến a = 2;
                }
                """;
        ExecutionResult r6 = runSource(src6);
        InterpreterAssert.assertTrue(r6.isStaticError);
        InterpreterAssert.assertTrue(r6.error.contains("Đã có biến tên 'a' được khai báo trong tầm vực này."));

        // Case 7: Lỗi tĩnh khi đọc biến trong biểu thức khởi tạo của chính nó
        String src7 = """
                {
                    biến x = x + 1;
                }
                """;
        ExecutionResult r7 = runSource(src7);
        InterpreterAssert.assertTrue(r7.isStaticError);
        InterpreterAssert.assertTrue(r7.error.contains("Không thể đọc biến cục bộ 'x' trong biểu thức khởi tạo của chính nó."));

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần VI: Toán tử (Operators).
     * Kiểm tra các toán tử số học, so sánh, logic (song ngữ) và xử lý chia cho 0.
     */
    private static void testPart6Operators() {
        System.out.print("Đang chạy kiểm thử Phần VI (Operators)... ");

        // Case 1: Toán tử số học cơ bản
        String src1 = "print((10 + 5 * 2 - 8 / 2) % 3);";
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("1", r1.stdout); // (10 + 10 - 4) % 3 = 16 % 3 = 1

        // Case 2: Toán tử so sánh và logic song ngữ (và/hoặc/không/khác)
        String src2 = """
                biến a = đúng;
                biến b = sai;
                nếu (a và không b) thì {
                    println("OK1");
                }
                nếu (a hoặc b) thì {
                    println("OK2");
                }
                nếu (10 khác 5) thì {
                    println("OK3");
                }
                """;
        ExecutionResult r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("OK1 OK2 OK3", r2.stdout.replace("\n", " ").replace("\r", "").trim());

        // Case 3: Toán tử so sánh số học
        String src3 = "print(10 >= 5 và 3 < 4 và 5 <= 5 và 5 == 5.0);";
        ExecutionResult r3 = runSource(src3);
        InterpreterAssert.assertEquals("true", r3.stdout);

        // Case 4: Lỗi runtime chia cho 0
        String src4 = "print(10 / 0);";
        ExecutionResult r4 = runSource(src4);
        InterpreterAssert.assertTrue(r4.isRuntimeError);
        InterpreterAssert.assertTrue(r4.error.toLowerCase().contains("chia cho 0"));

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần VII: Cấu trúc điều kiện (If-Else).
     * Kiểm tra cấu trúc rẽ nhánh bình thường và biểu thức if-else gán trị.
     */
    private static void testPart7IfElse() {
        System.out.print("Đang chạy kiểm thử Phần VII (If-Else)... ");

        // Case 1: Câu lệnh rẽ nhánh if-else lồng nhau
        String src1 = """
                biến điểm = 85;
                nếu (điểm >= 90) thì {
                    print("Xuất sắc");
                } còn_nếu (điểm >= 80) thì {
                    print("Giỏi");
                } không_thì {
                    print("Khá");
                }
                """;
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("Giỏi", r1.stdout);

        // Case 2: Biểu thức if-else trả về giá trị (gán trực tiếp cho biến)
        String src2 = """
                biến x = nếu (10 > 5) thì { 100 } không_thì { 200 };
                print(x);
                """;
        ExecutionResult r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("100", r2.stdout);

        // Case 3: Lỗi tĩnh khi kiểu của 2 nhánh trong biểu thức if-else không khớp
        String src3 = """
                biến x = nếu (đúng) thì { "Chuỗi" } không_thì { 123 };
                """;
        ExecutionResult r3 = runSource(src3);
        InterpreterAssert.assertTrue(r3.isStaticError);
        InterpreterAssert.assertTrue(r3.error.contains("Kiểu dữ liệu trả về của hai nhánh trong biểu thức 'nếu' không tương thích"));

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần VIII: Cấu trúc lựa chọn (Switch-Case / Match).
     * Kiểm tra cấu trúc rẽ nhánh trường_hợp, gom nhóm | mẫu, giá trị mặc định và biểu thức switch gán trị.
     */
    private static void testPart8SwitchCase() {
        System.out.print("Đang chạy kiểm thử Phần VIII (Switch-Case)... ");

        // Case 1: Câu lệnh switch-case thông thường với gom nhóm mẫu và mặc định
        String src1 = """
                biến x = 2;
                trường_hợp (x) {
                    1 | 3 -> print("Lẻ");
                    2 | 4 -> print("Chẵn");
                    _     -> print("Khác");
                }
                """;
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("Chẵn", r1.stdout);

        // Case 2: Biểu thức switch-case trả về giá trị
        String src2 = """
                biến k = 5;
                biến res = trường_hợp (k) {
                    1 | 2 -> "Nhỏ"
                    3 | 4 -> "Vừa"
                    _     -> "Lớn"
                };
                print(res);
                """;
        ExecutionResult r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("Lớn", r2.stdout);

        // Case 3: Mẫu mặc định bằng tiếng Việt 'sai' (đại diện cho mẫu bắt tất cả còn lại)
        String src3 = """
                biến k = 10;
                trường_hợp (k) {
                    1 -> print("Một");
                    sai -> print("Mặc định Việt");
                }
                """;
        ExecutionResult r3 = runSource(src3);
        InterpreterAssert.assertFalse(r3.isStaticError);
        InterpreterAssert.assertEquals("Mặc định Việt", r3.stdout);

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần IX: Vòng lặp (Loops).
     * Kiểm tra các vòng lặp: loop vô hạn, while điều kiện, duyệt khoảng (từ...đến/đến_hết), duyệt danh sách (của), và break/continue.
     *
     * @throws AssertionError nếu một khẳng định kiểm thử thất bại
     */
    private static void testPart9Loops() {
        System.out.print("Đang chạy kiểm thử Phần IX (Loops)... ");

        // Case 1: Vòng lặp while cơ bản với break/continue
        String src1 = """
                biến khả_biến i = 0;
                lặp (i < 10) {
                    i = i + 1;
                    nếu (i == 3) thì { tiếp; }
                    nếu (i == 6) thì { dừng; }
                    println(i);
                }
                """;
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("1 2 4 5", r1.stdout.replace("\n", " ").replace("\r", "").trim());

        // Case 2: Vòng lặp khoảng tăng dần inclusive (đến_hết)
        String src2 = """
                duyệt i từ 1 đến_hết 3 {
                    println(i);
                }
                """;
        ExecutionResult r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("1 2 3", r2.stdout.replace("\n", " ").replace("\r", "").trim());

        // Case 3: Vòng lặp khoảng giảm dần exclusive (đến)
        String src3 = """
                duyệt i từ 3 đến 1 {
                    println(i);
                }
                """;
        ExecutionResult r3 = runSource(src3);
        InterpreterAssert.assertFalse(r3.isStaticError);
        InterpreterAssert.assertEquals("3 2", r3.stdout.replace("\n", " ").replace("\r", "").trim());

        // Case 4: Duyệt qua phần tử danh sách (loop của) sử dụng biến list được đăng ký sẵn toàn cục
        String src4 = """
                duyệt x của list {
                    println(x);
                }
                """;
        ExecutionResult r4 = runSource(src4);
        InterpreterAssert.assertFalse(r4.isStaticError);
        InterpreterAssert.assertEquals("1 2 3", r4.stdout.replace("\n", " ").replace("\r", "").trim());

        // Case 5: Lỗi tĩnh sử dụng dừng; / tiếp; ngoài vòng lặp
        String src5 = """
                dừng;
                """;
        ExecutionResult r5 = runSource(src5);
        InterpreterAssert.assertTrue(r5.isStaticError);
        InterpreterAssert.assertTrue(r5.error.contains("Không thể sử dụng câu lệnh 'dừng'"));

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần X: Hàm (Functions).
     * Kiểm tra khai báo hàm, gọi hàm đệ quy, trả về tuple nhiều trị, lồng hàm, và closures.
     *
     * @throws AssertionError nếu một khẳng định kiểm thử thất bại
     */
    private static void testPart10Functions() {
        System.out.print("Đang chạy kiểm thử Phần X (Functions)... ");

        // Case 1: Hàm đệ quy tính Fibonacci
        String src1 = """
                hàm fib(số_nguyên n) -> số_nguyên {
                    nếu (n <= 1) thì {
                        trả_về n;
                    }
                    trả_về fib(n - 1) + fib(n - 2);
                }
                print(fib(6));
                """;
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("8", r1.stdout);

        // Case 2: Hàm trả về Tuple (nhiều giá trị)
        String src2 = """
                hàm getCoordinates() -> (số_nguyên x, số_nguyên y) {
                    trả_về (10, 20);
                }
                biến (posX, posY) = getCoordinates();
                print(posX, posY);
                """;
        ExecutionResult r2 = runSource(src2);
        if (r2.isStaticError) {
            System.out.println("STATIC ERROR: " + r2.error);
        }
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("10 20", r2.stdout);

        // Case 3: Hàm lồng nhau và Closures chụp biến môi trường cha
        String src3 = """
                hàm makeCounter() -> func() -> số_nguyên {
                    biến khả_biến count = 0;
                    hàm counter() -> số_nguyên {
                        count = count + 1;
                        trả_về count;
                    }
                    trả_về counter;
                }
                biến myCounter = makeCounter();
                println(myCounter());
                println(myCounter());
                """;
        ExecutionResult r3 = runSource(src3);
        if (r3.isStaticError) {
            System.out.println("CASE 3 STATIC ERROR: " + r3.error);
        }
        InterpreterAssert.assertFalse(r3.isStaticError);
        InterpreterAssert.assertEquals("1 2", r3.stdout.replace("\n", " ").replace("\r", "").trim());

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần XI: Chuỗi ký tự (Strings).
     * Kiểm tra nối chuỗi, so sánh chuỗi, và các thuộc tính/phương thức chuẩn như length(), toUpperCase() và to_string().
     */
    private static void testPart11Strings() {
        System.out.print("Đang chạy kiểm thử Phần XI (Strings)... ");

        // Case 1: Nối chuỗi và so sánh chuỗi
        String src1 = """
                biến s1 = "Hello";
                biến s2 = " World";
                println(s1 + s2);
                println(s1 == "Hello");
                """;
        ExecutionResult r1 = runSource(src1);
        InterpreterAssert.assertFalse(r1.isStaticError);
        InterpreterAssert.assertEquals("Hello World\ntrue", r1.stdout.replace("\r\n", "\n").trim());

        // Case 2: Gọi length(), toUpperCase() trên chuỗi và to_string() trên các kiểu số/logic
        String src2 = """
                biến s = "Nova";
                println(s.length());
                println(s.toUpperCase());
                biến x = 123;
                biến b = đúng;
                println(x.to_string());
                println(b.to_string());
                """;
        ExecutionResult r2 = runSource(src2);
        InterpreterAssert.assertFalse(r2.isStaticError);
        InterpreterAssert.assertEquals("4 NOVA 123 true", r2.stdout.replace("\n", " ").replace("\r", "").trim());

        System.out.println("ĐẠT");
    }
}
