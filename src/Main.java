import nova.lexer.Lexer;
import nova.lexer.TokenType;
import nova.lexer.Token;
import nova.lexer.LexerError;
import nova.ast.Expr;
import nova.ast.Stmt;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var input = """
                hàm a() -> số_nguyên {
                    biến x = 10
                    nếu (x > 5) thì {
                        x = 20
                    }
                }""";

        System.out.println("--- Chạy phân tích từ vựng ---");
        System.out.println("Mã nguồn:\n" + input);
        System.out.println("----------------------------------");

        var lexer = new Lexer(input);
        List<Token> tokens = new java.util.ArrayList<>();
        try {
            while (true) {
                var token = lexer.nextToken();
                tokens.add(token);
                System.out.println(token);
                if (token.type() == TokenType.EOF) {
                    break;
                }
            }
        } catch (LexerError e) {
            System.err.println(e.getMessage());
        }

        // --- MÔ PHỎNG DỰNG VÀ DUYỆT AST THỦ CÔNG ---
        System.out.println("\n--- Khởi dựng AST thủ công từ luồng token trên ---");

        // 1. Khởi dựng biểu thức: biến x = 10
        var varKeyword = new Token(TokenType.VAR, "biến");
        var varName = new Token(TokenType.IDENTIFIER, "x");
        var varValue = new Expr.Literal(10);
        var varDecl = new Stmt.Var(varKeyword, varName, null, varValue);

        // 2. Khởi dựng điều kiện: nếu (x > 5)
        var condition = new Expr.Binary(
            new Expr.Variable(new Token(TokenType.IDENTIFIER, "x")),
            new Token(TokenType.GREATER_THAN, ">"),
            new Expr.Literal(5)
        );

        // 3. Nhánh 'then': x = 20
        var assignStmt = new Stmt.Expression(
            new Expr.Assign(
                new Token(TokenType.IDENTIFIER, "x"),
                new Expr.Literal(20)
            )
        );
        Stmt thenBranch = new Stmt.Block(List.of(assignStmt));
        Stmt ifStmt = new Stmt.If(condition, thenBranch, null);

        // 4. Khai báo hàm: hàm a() -> số_nguyên { ... }
        var functionName = new Token(TokenType.IDENTIFIER, "a");
        var returnType = new Token(TokenType.TYPE_INT, "số_nguyên");
        var functionAST = new Stmt.Function(
            functionName,
            List.of(),
            returnType,
            List.of(varDecl, ifStmt)
        );

        // 5. Sử dụng Visitor để in AST
        var printer = new ASTPrinter();
        System.out.println("Kết quả in AST (thông qua Visitor Pattern):");
        System.out.println(printer.print(functionAST));

        System.out.println("\n--- Chạy phân tích cú pháp thực tế (Parser) ---");
        var realParser = new nova.parser.Parser(tokens);
        try {
            var parsedStmts = realParser.parse();
            if (realParser.hasErrors()) {
                System.out.println("Phát hiện lỗi cú pháp:");
                for (var err : realParser.getErrors()) {
                    System.out.println("Lỗi: " + err.getMessage());
                }
            } else {
                System.out.println("Kết quả phân tích cú pháp thực tế:");
                for (var stmt : parsedStmts) {
                    System.out.println(printer.print(stmt));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lớp in AST sử dụng mẫu thiết kế Visitor Pattern
    static class ASTPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {
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
            for (Stmt s : stmt.body) {
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
         * Chuyển đổi biểu thức một ngôi sang chuỗi.
         * 
         * @param expr Biểu thức một ngôi
         * @return Chuỗi biểu diễn biểu thức một ngôi
         */
        @Override
        public String visitUnaryExpr(Expr.Unary expr) {
            return "(" + expr.operator.lexeme() + expr.right.accept(this) + ")";
        }

        /**
         * Chuyển đổi biểu thức Get (truy cập phương thức hoặc thuộc tính qua dấu chấm) sang chuỗi.
         * 
         * @param expr Biểu thức Get cần chuyển đổi
         * @return Chuỗi biểu diễn biểu thức Get dạng đối_tượng.thuộc_tính
         */
        @Override
        public String visitGetExpr(Expr.Get expr) {
            return expr.object.accept(this) + "." + expr.name.lexeme();
        }

        /**
         * Chuyển đổi biểu thức StmtExpr (biểu thức bọc câu lệnh) sang chuỗi.
         * 
         * @param expr Biểu thức StmtExpr
         * @return Chuỗi biểu diễn câu lệnh được bọc bên trong
         */
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

        /**
         * Chuyển đổi câu lệnh vòng lặp while sang chuỗi.
         * 
         * @param stmt Câu lệnh while
         * @return Chuỗi biểu diễn vòng lặp while
         */
        @Override
        public String visitWhileStmt(Stmt.While stmt) {
            return "Lặp (" + stmt.condition.accept(this) + ") " + stmt.body.accept(this);
        }

        /**
         * Chuyển đổi câu lệnh vòng lặp for sang chuỗi.
         * 
         * @param stmt Câu lệnh for
         * @return Chuỗi biểu diễn vòng lặp for
         */
        @Override
        public String visitForStmt(Stmt.For stmt) {
            if (stmt.isForEach) {
                return "Duyệt " + stmt.name.lexeme() + " Của (" + stmt.end.accept(this) + ") " + stmt.body.accept(this);
            }
            return "Duyệt " + stmt.name.lexeme() + " Từ (" + stmt.start.accept(this) + " " + stmt.operator.lexeme() + " " + stmt.end.accept(this) + ") " + stmt.body.accept(this);
        }

        /**
         * Chuyển đổi câu lệnh break sang chuỗi.
         * 
         * @param stmt Câu lệnh break
         * @return Chuỗi "Dừng"
         */
        @Override
        public String visitBreakStmt(Stmt.Break stmt) {
            return "Dừng";
        }

        /**
         * Chuyển đổi câu lệnh continue sang chuỗi.
         * 
         * @param stmt Câu lệnh continue
         * @return Chuỗi "Tiếp"
         */
        @Override
        public String visitContinueStmt(Stmt.Continue stmt) {
            return "Tiếp";
        }

        /**
         * Chuyển đổi câu lệnh switch sang chuỗi.
         * 
         * @param stmt Câu lệnh switch
         * @return Chuỗi biểu diễn câu lệnh switch
         */
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