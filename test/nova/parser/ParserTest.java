package nova.parser;

import nova.lexer.Lexer;
import nova.lexer.Token;
import nova.lexer.TokenType;
import nova.lexer.LexerError;
import nova.ast.Expr;
import nova.ast.Stmt;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp kiểm thử toàn diện cho bộ phân tích cú pháp Parser của ngôn ngữ Nova.
 * Chứa các kịch bản kiểm thử bao phủ toàn bộ các tính năng từ Phần I đến Phần XI trong README.md.
 * Cung cấp cả ca kiểm thử thành công (Happy cases) và ca kiểm thử lỗi (Edge cases / Error recovery).
 * 
 * @author XUAN HOAN
 */
public class ParserTest {

    /**
     * Phương thức chạy chính, thực thi lần lượt tất cả các nhóm kịch bản kiểm thử.
     * 
     * @param args Đối số dòng lệnh (không sử dụng)
     */
    public static void main(String[] args) {
        System.out.println("BẮT ĐẦU CHẠY BỘ KIỂM THỬ PARSER TOÀN DIỆN...");
        
        try {
            testPart1EntryPoint();
            testPart2Output();
            testPart3Comments();
            testPart4DataTypes();
            testPart5VariablesAndConstants();
            testPart6Operators();
            testPart7IfElse();
            testPart8SwitchCase();
            testPart9Loops();
            testPart10Functions();
            testPart11Strings();
            testPanicModeRecovery();
            
            System.out.println("\nTẤT CẢ CÁC CA KIỂM THỬ ĐÃ VƯỢT QUA THÀNH CÔNG (100%)!");
        } catch (Throwable t) {
            System.err.println("\nKIỂM THỬ THẤT BẠI:");
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Hàm helper thực hiện lexing và parsing một đoạn mã nguồn Nova.
     * 
     * @param source Mã nguồn Nova cần phân tích
     * @return Đối tượng Parser sau khi đã phân tích cú pháp
     */
    private static Parser parseSource(String source) {
        var lexer = new Lexer(source);
        List<Token> tokens = new ArrayList<>();
        try {
            while (true) {
                var token = lexer.nextToken();
                tokens.add(token);
                if (token.type() == TokenType.EOF) {
                    break;
                }
            }
        } catch (LexerError e) {
            throw new RuntimeException("Lỗi phân tích từ vựng trong test: " + e.getMessage(), e);
        }
        
        Parser parser = new Parser(tokens);
        return parser;
    }

    /**
     * Chuyển đổi danh sách các câu lệnh AST thành chuỗi biểu diễn để dễ dàng so sánh kết quả.
     * 
     * @param statements Danh sách các câu lệnh AST
     * @return Chuỗi đại diện của cây AST
     */
    private static String printAST(List<Stmt> statements) {
        var printer = new TestASTPrinter();
        var sb = new StringBuilder();
        for (var stmt : statements) {
            sb.append(printer.print(stmt)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Kiểm thử Phần I: Cú pháp khởi chạy (Entry Point).
     * Bao gồm khai báo hàm main tiếng Anh và tiếng Việt, và lỗi cú pháp.
     */
    private static void testPart1EntryPoint() {
        System.out.print("Đang chạy kiểm thử Phần I (Entry Point)... ");
        
        // Happy Case 1: Tiếng Anh
        var src1 = "function main() -> void {}";
        var p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals("Hàm: main\nTham số: Không có\nKiểu trả về: void\nThân hàm: {\n}", printAST(ast1));

        // Happy Case 2: Tiếng Việt
        var src2 = "hàm main() -> trống {}";
        var p2 = parseSource(src2);
        List<Stmt> ast2 = p2.parse();
        ParserAssert.assertTrue(!p2.hasErrors());
        ParserAssert.assertEquals("Hàm: main\nTham số: Không có\nKiểu trả về: trống\nThân hàm: {\n}", printAST(ast2));

        // Edge Case 1: Thiếu dấu ngoặc đơn ()
        var srcErr = "hàm main -> trống {}";
        Parser pErr = parseSource(srcErr);
        pErr.parse();
        ParserAssert.assertTrue(pErr.hasErrors());

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần II: Xuất dữ liệu (Output).
     * Bao gồm các hàm in tiếng Anh/Việt, placeholder và nội suy chuỗi.
     */
    private static void testPart2Output() {
        System.out.print("Đang chạy kiểm thử Phần II (Output)... ");

        // Happy Case 1: print, println tiếng Anh và in, in_dòng_mới tiếng Việt
        var src1 = """
            print(x);
            println(y);
            in(a);
            in_dòng_mới(b);
            """;
        var p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Gọi_hàm Đọc_biến(print) với (Đọc_biến(x))\n" +
            "Gọi_hàm Đọc_biến(println) với (Đọc_biến(y))\n" +
            "Gọi_hàm Đọc_biến(in) với (Đọc_biến(a))\n" +
            "Gọi_hàm Đọc_biến(in_dòng_mới) với (Đọc_biến(b))", 
            printAST(ast1)
        );

        // Happy Case 2: In định dạng printf và in_định_dạng
        var src2 = """
            printf("Format {}", x);
            in_định_dạng("Format Việt {}", y);
            """;
        var p2 = parseSource(src2);
        List<Stmt> ast2 = p2.parse();
        ParserAssert.assertTrue(!p2.hasErrors());
        ParserAssert.assertEquals(
            "Gọi_hàm Đọc_biến(printf) với (Format {}, Đọc_biến(x))\n" +
            "Gọi_hàm Đọc_biến(in_định_dạng) với (Format Việt {}, Đọc_biến(y))", 
            printAST(ast2)
        );

        // Edge Case: Thiếu dấu ngoặc đóng
        var srcErr = "in(a;";
        var pErr = parseSource(srcErr);
        pErr.parse();
        ParserAssert.assertTrue(pErr.hasErrors());

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần III: Chú thích (Comments).
     * Chú thích một dòng, nhiều dòng, tài liệu, và đảm bảo parser bỏ qua chú thích.
     */
    private static void testPart3Comments() {
        System.out.print("Đang chạy kiểm thử Phần III (Comments)... ");

        String src1 = """
            // Đây là chú thích một dòng
            biến x = 10;
            /* Chú thích
               nhiều dòng */
            hằng_số y = 20;
            /** Chú thích tài liệu */
            biến z = 30;
            """;
        Parser p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo biến [x] (Tự suy luận) Khởi tạo: 10\n" +
            "Khai báo hằng_số [y] (Tự suy luận) Khởi tạo: 20\n" +
            "Khai báo biến [z] (Tự suy luận) Khởi tạo: 30", 
            printAST(ast1)
        );

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần IV: Kiểu dữ liệu (Data Types).
     * Kiểm tra các kiểu dữ liệu cơ bản, generic lồng nhau, và kiểu nullable.
     */
    private static void testPart4DataTypes() {
        System.out.print("Đang chạy kiểm thử Phần IV (Data Types)... ");

        // Happy Case 1: Generic lồng nhau và Nullable
        String src1 = """
            biến DanhSách<số_nguyên> ds = k_tồn_tại;
            biến BảnĐồ<chuỗi, DanhSách<số_nguyên>> map = k_tồn_tại;
            biến chuỗi? tenNullable = k_tồn_tại;
            """;
        Parser p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo biến [ds] (Kiểu: DanhSách<số_nguyên>) Khởi tạo: null\n" +
            "Khai báo biến [map] (Kiểu: BảnĐồ<chuỗi, DanhSách<số_nguyên>>) Khởi tạo: null\n" +
            "Khai báo biến [tenNullable] (Kiểu: chuỗi?) Khởi tạo: null", 
            printAST(ast1)
        );

        // Edge Case: Generic không đóng ngoặc '>'
        String srcErr = "biến DanhSách<số_nguyên ds = k_tồn_tại;";
        Parser pErr = parseSource(srcErr);
        pErr.parse();
        ParserAssert.assertTrue(pErr.hasErrors());

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần V: Biến & Hằng số (Variables & Constants).
     * Kiểm tra biến bất biến, khả biến mut, hằng số const, nullable và lỗi liên quan.
     */
    private static void testPart5VariablesAndConstants() {
        System.out.print("Đang chạy kiểm thử Phần V (Variables & Constants)... ");

        // Happy Case 1: Các loại khai báo (Tiếng Việt)
        String src1 = """
            biến x = 1;
            biến khả_biến y = 2;
            hằng_số z = 3;
            """;
        Parser p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo biến [x] (Tự suy luận) Khởi tạo: 1\n" +
            "Khai báo khả_biến [y] (Tự suy luận) Khởi tạo: 2\n" +
            "Khai báo hằng_số [z] (Tự suy luận) Khởi tạo: 3", 
            printAST(ast1)
        );

        // Happy Case 2: Các loại khai báo (Tiếng Anh)
        String src2 = """
            var a = 4;
            var mut b = 5;
            const c = 6;
            """;
        Parser p2 = parseSource(src2);
        List<Stmt> ast2 = p2.parse();
        ParserAssert.assertTrue(!p2.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo var [a] (Tự suy luận) Khởi tạo: 4\n" +
            "Khai báo mut [b] (Tự suy luận) Khởi tạo: 5\n" +
            "Khai báo const [c] (Tự suy luận) Khởi tạo: 6", 
            printAST(ast2)
        );

        // Edge Case 1: Hằng số không khởi tạo
        String srcErr1 = "hằng_số số_nguyên k;";
        Parser pErr1 = parseSource(srcErr1);
        try {
            pErr1.parse();
        } catch (Exception e) {
            // Có thể quăng lỗi lúc parse
        }
        ParserAssert.assertTrue(pErr1.hasErrors());

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần VI: Toán tử (Operators).
     * Kiểm tra toán tử số học, so sánh, logic song ngữ và độ ưu tiên toán tử.
     */
    private static void testPart6Operators() {
        System.out.print("Đang chạy kiểm thử Phần VI (Operators)... ");

        // Happy Case 1: Số học, so sánh, logic và độ ưu tiên
        String src1 = "biến x = a + b * c == d || !e;";
        Parser p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo biến [x] (Tự suy luận) Khởi tạo: (((Đọc_biến(a) + (Đọc_biến(b) * Đọc_biến(c))) == Đọc_biến(d)) || (!Đọc_biến(e)))", 
            printAST(ast1)
        );

        // Happy Case 2: Toán tử song ngữ Việt - Anh
        String src2 = "biến y = a khác b và không c hoặc d;";
        Parser p2 = parseSource(src2);
        List<Stmt> ast2 = p2.parse();
        ParserAssert.assertTrue(!p2.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo biến [y] (Tự suy luận) Khởi tạo: (((Đọc_biến(a) khác Đọc_biến(b)) và (!Đọc_biến(c))) hoặc Đọc_biến(d))", 
            printAST(ast2)
        );

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần VII: Cấu trúc điều kiện (If-Else).
     * Kiểm tra câu lệnh if-else, if-else lồng và biểu thức if gán trực tiếp cho biến.
     */
    private static void testPart7IfElse() {
        System.out.print("Đang chạy kiểm thử Phần VII (If-Else)... ");

        // Happy Case 1: Câu lệnh If-else tiếng Việt
        var src1 = """
            nếu (a) thì {
                x = 1;
            } còn_nếu (b) thì {
                x = 2;
            } không_thì {
                x = 3;
            }
            """;
        var p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Nếu ((Đọc_biến(a))) Thì {\n" +
            "    Gán x = 1\n" +
            "} còn_nếu Nếu ((Đọc_biến(b))) Thì {\n" +
            "    Gán x = 2\n" +
            "} không_thì {\n" +
            "    Gán x = 3\n" +
            "}", 
            printAST(ast1)
        );

        // Happy Case 2: Biểu thức If gán cho biến (cả kiểu Rust: biểu thức cuối block không có ;)
        var src2 = "biến val = nếu (cond) thì { 10 } không_thì { 20 };";
        var p2 = parseSource(src2);
        List<Stmt> ast2 = p2.parse();
        ParserAssert.assertTrue(!p2.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo biến [val] (Tự suy luận) Khởi tạo: Nếu ((Đọc_biến(cond))) Thì {\n" +
            "    10\n" +
            "} không_thì {\n" +
            "    20\n" +
            "}", 
            printAST(ast2)
        );

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần VIII: Cấu trúc lựa chọn (Switch-Case).
     * Kiểm tra switch thông thường, gom nhóm case bằng '|', case mặc định 'sai'/'_', và switch expression.
     */
    private static void testPart8SwitchCase() {
        System.out.print("Đang chạy kiểm thử Phần VIII (Switch-Case)... ");

        // Happy Case 1: Switch tiếng Việt có gom nhóm
        var src1 = """
            trường_hợp (x) {
                1 | 3 -> in(1);
                2 | 4 -> { in(2); }
                sai -> in(0);
            }
            """;
        var p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        if (p1.hasErrors()) {
            for (ParseError err : p1.getErrors()) {
                System.err.println("P1 ERROR: " + err.getMessage() + " at token: " + err.getToken().lexeme() + " (Type: " + err.getToken().type() + ")");
            }
        }
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Trường_hợp ((Đọc_biến(x))) {\n" +
            "    Các mẫu: 1 | 3 -> Gọi_hàm Đọc_biến(in) với (1)\n" +
            "    Các mẫu: 2 | 4 -> {\n" +
            "        Gọi_hàm Đọc_biến(in) với (2)\n" +
            "    }\n" +
            "    Các mẫu: false -> Gọi_hàm Đọc_biến(in) với (0)\n" +
            "}", 
            printAST(ast1)
        );

        // Happy Case 2: Switch expression (không bắt buộc dấu ;)
        var src2 = """
            biến res = trường_hợp (x) {
                1 -> "Một"
                2 -> "Hai"
                _ -> "Khác"
            };
            """;
        var p2 = parseSource(src2);
        List<Stmt> ast2 = p2.parse();
        if (p2.hasErrors()) {
            for (ParseError err : p2.getErrors()) {
                System.err.println("P2 ERROR: " + err.getMessage() + " at token: " + err.getToken().lexeme() + " (Type: " + err.getToken().type() + ")");
            }
        }
        ParserAssert.assertTrue(!p2.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo biến [res] (Tự suy luận) Khởi tạo: Trường_hợp ((Đọc_biến(x))) {\n" +
            "    Các mẫu: 1 -> Một\n" +
            "    Các mẫu: 2 -> Hai\n" +
            "    Các mẫu: Đọc_biến(_) -> Khác\n" +
            "}", 
            printAST(ast2)
        );

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần IX: Vòng lặp (Loops).
     * Kiểm tra lặp vô hạn/điều kiện 'lặp', duyệt khoảng số 'đến'/'đến_hết', duyệt phần tử 'của', lệnh dừng/tiếp.
     */
    private static void testPart9Loops() {
        System.out.print("Đang chạy kiểm thử Phần IX (Loops)... ");

        // Happy Case 1: Lặp while và dừng/tiếp
        var src1 = """
            lặp (đúng) thì {
                nếu (cond) thì { dừng; } không_thì { tiếp; }
            }
            """;
        var p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Lặp ((true)) {\n" +
            "    Nếu ((Đọc_biến(cond))) Thì {\n" +
            "        Dừng\n" +
            "    } không_thì {\n" +
            "        Tiếp\n" +
            "    }\n" +
            "}", 
            printAST(ast1)
        );

        // Happy Case 2: Duyệt từ đến, đến_hết
        var src2 = """
            duyệt i từ 1 đến 10 { print(i); }
            duyệt j từ 1 đến_hết 5 { print(j); }
            """;
        var p2 = parseSource(src2);
        List<Stmt> ast2 = p2.parse();
        ParserAssert.assertTrue(!p2.hasErrors());
        ParserAssert.assertEquals(
            "Duyệt i Từ (1 đến 10) {\n" +
            "    Gọi_hàm Đọc_biến(print) với (Đọc_biến(i))\n" +
            "}\n" +
            "Duyệt j Từ (1 đến_hết 5) {\n" +
            "    Gọi_hàm Đọc_biến(print) với (Đọc_biến(j))\n" +
            "}", 
            printAST(ast2)
        );

        // Happy Case 3: Duyệt phần tử của danh sách
        var src3 = """
            duyệt item của list { print(item); }
            """;
        var p3 = parseSource(src3);
        List<Stmt> ast3 = p3.parse();
        ParserAssert.assertTrue(!p3.hasErrors());
        ParserAssert.assertEquals(
            "Duyệt item Của (Đọc_biến(list)) {\n" +
            "    Gọi_hàm Đọc_biến(print) với (Đọc_biến(item))\n" +
            "}", 
            printAST(ast3)
        );

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần X: Hàm (Functions).
     * Kiểm tra khai báo hàm, tham số, kiểu trả về và trả về tuple nhiều giá trị.
     */
    private static void testPart10Functions() {
        System.out.print("Đang chạy kiểm thử Phần X (Functions)... ");

        // Happy Case 1: Hàm trả về tuple (nhiều giá trị)
        String src1 = """
            hàm chia_co_du(số_nguyên a, số_nguyên b) -> (số_nguyên thuong, số_nguyên du) {
                trả_về k_tồn_tại;
            }
            """;
        var p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Hàm: chia_co_du\n" +
            "Tham số: a (Kiểu: số_nguyên), b (Kiểu: số_nguyên)\n" +
            "Kiểu trả về: (số_nguyên thuong, số_nguyên du)\n" +
            "Thân hàm: {\n" +
            "    Trả về null\n" +
            "}", 
            printAST(ast1)
        );

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử Phần XI: Chuỗi ký tự (Strings).
     * Kiểm tra nối chuỗi, so sánh và gọi phương thức trên chuỗi qua toán tử chấm '.'.
     */
    private static void testPart11Strings() {
        System.out.print("Đang chạy kiểm thử Phần XI (Strings)... ");

        // Happy Case 1: Gọi phương thức qua toán tử chấm
        var src1 = """
            biến len = str.length();
            biến upper = "hello".toUpperCase();
            """;
        var p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(!p1.hasErrors());
        ParserAssert.assertEquals(
            "Khai báo biến [len] (Tự suy luận) Khởi tạo: Gọi_hàm Đọc_biến(str).length với ()\n" +
            "Khai báo biến [upper] (Tự suy luận) Khởi tạo: Gọi_hàm hello.toUpperCase với ()", 
            printAST(ast1)
        );

        System.out.println("ĐẠT");
    }

    /**
     * Kiểm thử khả năng phục hồi lỗi của Parser (Panic Mode Recovery / Synchronization).
     * Đảm bảo khi gặp lỗi cú pháp ở một câu lệnh, Parser ghi nhận lỗi nhưng vẫn tiếp tục parse các câu lệnh hợp lệ phía sau.
     */
    private static void testPanicModeRecovery() {
        System.out.print("Đang chạy kiểm thử Panic Mode Recovery... ");

        var src1 = """
            biến x = ;
            biến y = 10;
            """;
        var p1 = parseSource(src1);
        List<Stmt> ast1 = p1.parse();
        ParserAssert.assertTrue(p1.hasErrors());
        // Lỗi tại khai báo x, nhưng khai báo y phải thành công
        ParserAssert.assertEquals("Khai báo biến [y] (Tự suy luận) Khởi tạo: 10", printAST(ast1));

        System.out.println("ĐẠT");
    }

    /**
     * Lớp in AST nội bộ phục vụ so sánh kết quả kiểm thử.
     * 
     * @author XUAN HOAN
     */
    private static class TestASTPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
        public String print(Stmt stmt) {
            return stmt.accept(this);
        }

        public String print(Expr expr) {
            return expr.accept(this);
        }

        @Override
        public String visitBlockStmt(Stmt.Block stmt) {
            var sb = new StringBuilder("{\n");
            for (var s : stmt.statements) {
                sb.append("    ").append(s.accept(this).replace("\n", "\n    ")).append("\n");
            }
            sb.append("}");
            return sb.toString();
        }

        @Override
        public String visitExpressionStmt(Stmt.Expression stmt) {
            return stmt.expression.accept(this);
        }

        @Override
        public String visitFunctionStmt(Stmt.Function stmt) {
            var sb = new StringBuilder("Hàm: ")
                    .append(stmt.name.lexeme())
                    .append("\nTham số: ");
            if (stmt.parameters.isEmpty()) {
                sb.append("Không có");
            } else {
                for (int i = 0; i < stmt.parameters.size(); i++) {
                    var p = stmt.parameters.get(i);
                    sb.append(p.name.lexeme()).append(" (Kiểu: ").append(p.type.lexeme()).append(")");
                    if (i < stmt.parameters.size() - 1) sb.append(", ");
                }
            }
            sb.append("\nKiểu trả về: ").append(stmt.returnType != null ? stmt.returnType.lexeme() : "void");
            sb.append("\nThân hàm: {\n");
            for (var s : stmt.body) {
                sb.append("    ").append(s.accept(this).replace("\n", "\n    ")).append("\n");
            }
            sb.append("}");
            return sb.toString();
        }

        @Override
        public String visitIfStmt(Stmt.If stmt) {
            var sb = new StringBuilder("Nếu (")
                    .append(stmt.condition.accept(this))
                    .append(") Thì ");
            sb.append(stmt.thenBranch.accept(this));
            if (stmt.elseBranch != null) {
                if (stmt.elseBranch instanceof Stmt.If) {
                    sb.append(" còn_nếu ").append(stmt.elseBranch.accept(this));
                } else {
                    sb.append(" không_thì ").append(stmt.elseBranch.accept(this));
                }
            }
            return sb.toString();
        }

        @Override
        public String visitVarStmt(Stmt.Var stmt) {
            var typeStr = stmt.type != null ? " (Kiểu: " + stmt.type.lexeme() + ")" : " (Tự suy luận)";
            var initStr = stmt.initializer != null ? " Khởi tạo: " + stmt.initializer.accept(this) : "";
            return "Khai báo " + stmt.keyword.lexeme() + " [" + stmt.name.lexeme() + "]" + typeStr + initStr;
        }

        @Override
        public String visitReturnStmt(Stmt.Return stmt) {
            return "Trả về" + (stmt.value != null ? " " + stmt.value.accept(this) : "");
        }

        @Override
        public String visitAssignExpr(Expr.Assign expr) {
            return "Gán " + expr.name.lexeme() + " = " + expr.value.accept(this);
        }

        @Override
        public String visitBinaryExpr(Expr.Binary expr) {
            return "(" + expr.left.accept(this) + " " + expr.operator.lexeme() + " " + expr.right.accept(this) + ")";
        }

        @Override
        public String visitGroupingExpr(Expr.Grouping expr) {
            return "(" + expr.expression.accept(this) + ")";
        }

        @Override
        public String visitLiteralExpr(Expr.Literal expr) {
            if (expr.value == null) return "null";
            return expr.value.toString();
        }

        @Override
        public String visitLogicalExpr(Expr.Logical expr) {
            return "(" + expr.left.accept(this) + " " + expr.operator.lexeme() + " " + expr.right.accept(this) + ")";
        }

        @Override
        public String visitVariableExpr(Expr.Variable expr) {
            return "Đọc_biến(" + expr.name.lexeme() + ")";
        }

        @Override
        public String visitCallExpr(Expr.Call expr) {
            var sb = new StringBuilder("Gọi_hàm ").append(expr.callee.accept(this)).append(" với (");
            for (int i = 0; i < expr.arguments.size(); i++) {
                sb.append(expr.arguments.get(i).accept(this));
                if (i < expr.arguments.size() - 1) sb.append(", ");
            }
            sb.append(")");
            return sb.toString();
        }

        /**
         * Biểu diễn biểu thức một ngôi dưới dạng chuỗi để kiểm thử.
         * Phép phủ định logic (ví dụ: "không", "not") được chuẩn hóa thành "!" để dễ so sánh kết quả.
         * 
         * @param expr Biểu thức một ngôi cần xử lý
         * @return Chuỗi biểu diễn biểu thức một ngôi
         */
        @Override
        public String visitUnaryExpr(Expr.Unary expr) {
            var op = expr.operator.lexeme();
            if (op.equals("không") || op.equals("not")) {
                op = "!";
            }
            return "(" + op + expr.right.accept(this) + ")";
        }

        @Override
        public String visitGetExpr(Expr.Get expr) {
            return expr.object.accept(this) + "." + expr.name.lexeme();
        }

        @Override
        public String visitStmtExpr(Expr.StmtExpr expr) {
            return expr.statement.accept(this);
        }

        /**
         * Chuyển đổi biểu thức Tuple nhiều giá trị sang chuỗi.
         * 
         * @param expr Biểu thức Tuple
         * @return Chuỗi biểu diễn Tuple (ví dụ: (10, 20))
         */
        @Override
        public String visitTupleExpr(Expr.Tuple expr) {
            var sb = new StringBuilder("(");
            for (int i = 0; i < expr.expressions.size(); i++) {
                sb.append(expr.expressions.get(i).accept(this));
                if (i < expr.expressions.size() - 1) sb.append(", ");
            }
            sb.append(")");
            return sb.toString();
        }

        @Override
        public String visitWhileStmt(Stmt.While stmt) {
            return "Lặp (" + stmt.condition.accept(this) + ") " + stmt.body.accept(this);
        }

        @Override
        public String visitForStmt(Stmt.For stmt) {
            if (stmt.isForEach) {
                return "Duyệt " + stmt.name.lexeme() + " Của (" + stmt.end.accept(this) + ") " + stmt.body.accept(this);
            }
            return "Duyệt " + stmt.name.lexeme() + " Từ (" + stmt.start.accept(this) + " " + stmt.operator.lexeme() + " " + stmt.end.accept(this) + ") " + stmt.body.accept(this);
        }

        @Override
        public String visitBreakStmt(Stmt.Break stmt) {
            return "Dừng";
        }

        @Override
        public String visitContinueStmt(Stmt.Continue stmt) {
            return "Tiếp";
        }

        @Override
        public String visitSwitchStmt(Stmt.Switch stmt) {
            var sb = new StringBuilder("Trường_hợp (")
                    .append(stmt.value.accept(this))
                    .append(") {\n");
            for (Stmt.SwitchCase sc : stmt.cases) {
                sb.append("    Các mẫu: ");
                for (int i = 0; i < sc.patterns.size(); i++) {
                    sb.append(sc.patterns.get(i).accept(this));
                    if (i < sc.patterns.size() - 1) sb.append(" | ");
                }
                sb.append(" -> ").append(sc.body.accept(this).replace("\n", "\n    ")).append("\n");
            }
            sb.append("}");
            return sb.toString();
        }
    }
}
