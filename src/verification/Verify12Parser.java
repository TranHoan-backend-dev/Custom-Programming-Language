//package verification;
//
//import nova.lexer.Lexer;
//import nova.lexer.Token;
//import nova.lexer.TokenType;
//import nova.lexer.LexerError;
//import nova.parser.Parser;
//import nova.ast.Expr;
//import nova.ast.Stmt;
//import java.util.ArrayList;
//import java.util.List;
//
// * Lớp kiểm thử tích hợp chuyên biệt cho bộ phân tích cú pháp (Parser).
// * Chứa các ca kiểm thử kiểm tra tính đúng đắn của việc phân tích cú pháp (Unary, Loops, Switch-case, Generics, Nullable).
// *
// * @author XUAN HOAN
// */
//public class Verify12Parser {
//    /**
//     * Phương thức entry point để chạy toàn bộ các ca kiểm thử của Parser.
//     *
//     * @param args Các đối số dòng lệnh (không sử dụng)
//     */
//    public static void main(String[] args) {
//        System.out.println("================================================================================");
//        System.out.println("RUNNING PARSER INTEGRATION TESTS");
//        System.out.println("================================================================================\n");
//
//        // Ca kiểm thử 1: Khai báo biến, hằng và kiểu generic, nullable
//        verifyTestCase(
//            "Ca kiểm thử 1: Khai báo biến, kiểu Generic và Nullable",
//            """
//            biến DanhSách<số_nguyên> danh_sách = null;
//            biến chuỗi? tên = "Nova";
//            hằng_số số_thực_kép PI = 3.14;
//            """
//        );
//
//        // Ca kiểm thử 2: Biểu thức một ngôi (Unary) và toán tử so sánh
//        verifyTestCase(
//            "Ca kiểm thử 2: Biểu thức một ngôi (Unary) và logic",
//            """
//            biến logic x = !đúng;
//            biến số_nguyên y = -10;
//            """
//        );
//
//        // Ca kiểm thử 3: Vòng lặp while (lặp) và for (duyệt)
//        verifyTestCase(
//            "Ca kiểm thử 3: Các loại vòng lặp (lặp, duyệt)",
//            """
//            lặp (x < 10) thì {
//                x = x + 1;
//                nếu (x == 5) thì {
//                    tiếp;
//                }
//                nếu (x == 8) thì {
//                    dừng;
//                }
//            }
//            duyệt n của danh_sách {
//                in_dòng_mới(n);
//            }
//            duyệt i từ 0 đến 5 {
//                in_dòng_mới(i);
//            }
//            """
//        );
//
//        // Ca kiểm thử 4: Cấu trúc rẽ nhánh switch-case (trường_hợp)
//        verifyTestCase(
//            "Ca kiểm thử 4: Cấu trúc rẽ nhánh trường_hợp (switch-case)",
//            """
//            trường_hợp (điểm) {
//                10 | 9 -> in("Xuất sắc");
//                8 | 7 -> in("Khá");
//                6 | 5 -> in("Trung bình");
//                sai -> in("Yếu");
//            }
//            """
//        );
//
//        // Ca kiểm thử 5: Khai báo hàm đầy đủ cấu trúc
//        verifyTestCase(
//            "Ca kiểm thử 5: Khai báo hàm tiếng Việt",
//            """
//            hàm tính_tổng(số_nguyên a, số_nguyên b) -> số_nguyên {
//                trả_về a + b;
//            }
//            """
//        );
//    }
//
//    /**
//     * Thực thi một ca kiểm thử phân tích cú pháp, in mã nguồn và kết quả cây AST hoặc lỗi.
//     *
//     * @param title Tiêu đề của ca kiểm thử
//     * @param input Mã nguồn Nova đầu vào
//     */
//    private static void verifyTestCase(String title, String input) {
//        System.out.println("Ca kiểm thử: " + title);
//        System.out.println("---------------- Mã nguồn ----------------");
//        System.out.println(input.stripIndent().trim());
//        System.out.println("---------------- Cây AST -----------------");
//
//        // 1. Chạy Lexer để sinh luồng Token
//        Lexer lexer = new Lexer(input);
//        List<Token> tokens = new ArrayList<>();
//        try {
//            while (true) {
//                Token token = lexer.nextToken();
//                tokens.add(token);
//                if (token.type() == TokenType.EOF) {
//                    break;
//                }
//            }
//        } catch (LexerError e) {
//            System.out.println("LỖI PHÂN TÍCH TỪ VỰNG: " + e.getMessage());
//            System.out.println("Trạng thái: THẤT BẠI");
//            System.out.println("================================================================================\n");
//            return;
//        }
//
//        // 2. Chạy Parser để xây dựng AST
//        Parser parser = new Parser(tokens);
//        try {
//            List<Stmt> statements = parser.parse();
//            if (parser.hasErrors()) {
//                System.out.println("Phát hiện lỗi cú pháp:");
//                for (var err : parser.getErrors()) {
//                    System.out.println("Lỗi: " + err.getMessage());
//                }
//                System.out.println("Trạng thái: THẤT BẠI");
//            } else {
//                var printer = new VerifyASTPrinter();
//                for (Stmt stmt : statements) {
//                    System.out.println(printer.print(stmt));
//                }
//                System.out.println("Trạng thái: THÀNH CÔNG");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Trạng thái: THẤT BẠI (Ngoại lệ)");
//        }
//        System.out.println("================================================================================\n");
//    }
//
//    /**
//     * Lớp in AST nội bộ phục vụ cho quá trình kiểm thử Parser.
//     *
//     * @author XUAN HOAN
//     */
//    private static class VerifyASTPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
//        public String print(Stmt stmt) {
//            return stmt.accept(this);
//        }
//
//        public String print(Expr expr) {
//            return expr.accept(this);
//        }
//
//        @Override
//        public String visitBlockStmt(Stmt.Block stmt) {
//            StringBuilder sb = new StringBuilder("{\n");
//            for (Stmt s : stmt.statements) {
//                sb.append("    ").append(s.accept(this).replace("\n", "\n    ")).append("\n");
//            }
//            sb.append("}");
//            return sb.toString();
//        }
//
//        @Override
//        public String visitExpressionStmt(Stmt.Expression stmt) {
//            return stmt.expression.accept(this);
//        }
//
//        @Override
//        public String visitFunctionStmt(Stmt.Function stmt) {
//            StringBuilder sb = new StringBuilder("Hàm: ")
//                    .append(stmt.name.lexeme())
//                    .append("\nTham số: ");
//            if (stmt.parameters.isEmpty()) {
//                sb.append("Không có");
//            } else {
//                for (int i = 0; i < stmt.parameters.size(); i++) {
//                    var p = stmt.parameters.get(i);
//                    sb.append(p.name.lexeme()).append(" (Kiểu: ").append(p.type.lexeme()).append(")");
//                    if (i < stmt.parameters.size() - 1) sb.append(", ");
//                }
//            }
//            sb.append("\nKiểu trả về: ").append(stmt.returnType != null ? stmt.returnType.lexeme() : "void");
//            sb.append("\nThân hàm: {\n");
//            for (Stmt s : stmt.body) {
//                sb.append("    ").append(s.accept(this).replace("\n", "\n    ")).append("\n");
//            }
//            sb.append("}");
//            return sb.toString();
//        }
//
//        @Override
//        public String visitIfStmt(Stmt.If stmt) {
//            StringBuilder sb = new StringBuilder("Nếu (")
//                    .append(stmt.condition.accept(this))
//                    .append(") Thì ");
//            sb.append(stmt.thenBranch.accept(this));
//            if (stmt.elseBranch != null) {
//                if (stmt.elseBranch instanceof Stmt.If) {
//                    sb.append(" còn_nếu ").append(stmt.elseBranch.accept(this));
//                } else {
//                    sb.append(" không_thì ").append(stmt.elseBranch.accept(this));
//                }
//            }
//            return sb.toString();
//        }
//
//        @Override
//        public String visitVarStmt(Stmt.Var stmt) {
//            String typeStr = stmt.type != null ? " (Kiểu: " + stmt.type.lexeme() + ")" : " (Tự suy luận)";
//            String initStr = stmt.initializer != null ? " Khởi tạo: " + stmt.initializer.accept(this) : "";
//            return "Khai báo " + stmt.keyword.lexeme() + " [" + stmt.name.lexeme() + "]" + typeStr + initStr;
//        }
//
//        @Override
//        public String visitReturnStmt(Stmt.Return stmt) {
//            return "Trả về" + (stmt.value != null ? " " + stmt.value.accept(this) : "");
//        }
//
//        @Override
//        public String visitAssignExpr(Expr.Assign expr) {
//            return "Gán " + expr.name.lexeme() + " = " + expr.value.accept(this);
//        }
//
//        @Override
//        public String visitBinaryExpr(Expr.Binary expr) {
//            return "(" + expr.left.accept(this) + " " + expr.operator.lexeme() + " " + expr.right.accept(this) + ")";
//        }
//
//        @Override
//        public String visitGroupingExpr(Expr.Grouping expr) {
//            return "(" + expr.expression.accept(this) + ")";
//        }
//
//        @Override
//        public String visitLiteralExpr(Expr.Literal expr) {
//            if (expr.value == null) return "null";
//            return expr.value.toString();
//        }
//
//        @Override
//        public String visitLogicalExpr(Expr.Logical expr) {
//            return "(" + expr.left.accept(this) + " " + expr.operator.lexeme() + " " + expr.right.accept(this) + ")";
//        }
//
//        @Override
//        public String visitVariableExpr(Expr.Variable expr) {
//            return "Đọc_biến(" + expr.name.lexeme() + ")";
//        }
//
//        @Override
//        public String visitCallExpr(Expr.Call expr) {
//            StringBuilder sb = new StringBuilder("Gọi_hàm ").append(expr.callee.accept(this)).append(" với (");
//            for (int i = 0; i < expr.arguments.size(); i++) {
//                sb.append(expr.arguments.get(i).accept(this));
//                if (i < expr.arguments.size() - 1) sb.append(", ");
//            }
//            sb.append(")");
//            return sb.toString();
//        }
//
//        /**
//         * Chuyển đổi biểu thức một ngôi sang chuỗi.
//         *
//         * @param expr Biểu thức một ngôi
//         * @return Chuỗi biểu diễn biểu thức một ngôi
//         */
//        @Override
//        public String visitUnaryExpr(Expr.Unary expr) {
//            return "(" + expr.operator.lexeme() + expr.right.accept(this) + ")";
//        }
//
//        /**
//         * Chuyển đổi biểu thức truy cập thuộc tính/phương thức sang chuỗi.
//         *
//         * @param expr Biểu thức truy cập thuộc tính/phương thức
//         * @return Chuỗi biểu diễn biểu thức truy cập
//         */
//        @Override
//        public String visitGetExpr(Expr.Get expr) {
//            return expr.object.accept(this) + "." + expr.name.lexeme();
//        }
//
//        /**
//         * Chuyển đổi biểu thức StmtExpr (biểu thức bọc câu lệnh) sang chuỗi.
//         *
//         * @param expr Biểu thức StmtExpr
//         * @return Chuỗi biểu diễn câu lệnh được bọc bên trong
//         */
//        @Override
//        public String visitStmtExpr(Expr.StmtExpr expr) {
//            return expr.statement.accept(this);
//        }
//
//        /**
//         * Chuyển đổi biểu thức Tuple nhiều giá trị sang chuỗi.
//         *
//         * @param expr Biểu thức Tuple
//         * @return Chuỗi biểu diễn Tuple (ví dụ: (10, 20))
//         */
//        @Override
//        public String visitTupleExpr(Expr.Tuple expr) {
//            StringBuilder sb = new StringBuilder("(");
//            for (int i = 0; i < expr.expressions.size(); i++) {
//                sb.append(expr.expressions.get(i).accept(this));
//                if (i < expr.expressions.size() - 1) sb.append(", ");
//            }
//            sb.append(")");
//            return sb.toString();
//        }
//
//        /**
//         * Chuyển đổi câu lệnh vòng lặp while sang chuỗi.
//         *
//         * @param stmt Câu lệnh while
//         * @return Chuỗi biểu diễn vòng lặp while
//         */
//        @Override
//        public String visitWhileStmt(Stmt.While stmt) {
//            return "Lặp (" + stmt.condition.accept(this) + ") " + stmt.body.accept(this);
//        }
//
//        /**
//         * Chuyển đổi câu lệnh vòng lặp for sang chuỗi.
//         *
//         * @param stmt Câu lệnh for
//         * @return Chuỗi biểu diễn vòng lặp for
//         */
//        @Override
//        public String visitForStmt(Stmt.For stmt) {
//            if (stmt.isForEach) {
//                return "Duyệt " + stmt.name.lexeme() + " Của (" + stmt.end.accept(this) + ") " + stmt.body.accept(this);
//            }
//            return "Duyệt " + stmt.name.lexeme() + " Từ (" + stmt.start.accept(this) + " " + stmt.operator.lexeme() + " " + stmt.end.accept(this) + ") " + stmt.body.accept(this);
//        }
//
//        /**
//         * Chuyển đổi câu lệnh break sang chuỗi.
//         *
//         * @param stmt Câu lệnh break
//         * @return Chuỗi "Dừng"
//         */
//        @Override
//        public String visitBreakStmt(Stmt.Break stmt) {
//            return "Dừng";
//        }
//
//        /**
//         * Chuyển đổi câu lệnh continue sang chuỗi.
//         *
//         * @param stmt Câu lệnh continue
//         * @return Chuỗi "Tiếp"
//         */
//        @Override
//        public String visitContinueStmt(Stmt.Continue stmt) {
//            return "Tiếp";
//        }
//
//        /**
//         * Chuyển đổi câu lệnh switch sang chuỗi.
//         *
//         * @param stmt Câu lệnh switch
//         * @return Chuỗi biểu diễn câu lệnh switch
//         */
//        @Override
//        public String visitSwitchStmt(Stmt.Switch stmt) {
//            StringBuilder sb = new StringBuilder("Trường_hợp (")
//                    .append(stmt.value.accept(this))
//                    .append(") {\n");
//            for (Stmt.SwitchCase sc : stmt.cases) {
//                sb.append("    Các mẫu: ");
//                for (int i = 0; i < sc.patterns.size(); i++) {
//                    sb.append(sc.patterns.get(i).accept(this));
//                    if (i < sc.patterns.size() - 1) sb.append(" | ");
//                }
//                sb.append(" -> ").append(sc.body.accept(this).replace("\n", "\n    ")).append("\n");
//            }
//            sb.append("}");
//            return sb.toString();
//        }
//    }
//}
