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
                a() -> số_nguyên {
                    biến x = 10
                    nếu (x > 5) {
                        x = 20
                    }
                }""";

        System.out.println("--- Chạy phân tích từ vựng ---");
        System.out.println("Mã nguồn:\n" + input);
        System.out.println("----------------------------------");

        var lexer = new Lexer(input);
        try {
            while (true) {
                var token = lexer.nextToken();
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
        Token varKeyword = new Token(TokenType.VAR, "biến");
        Token varName = new Token(TokenType.IDENTIFIER, "x");
        Expr varValue = new Expr.Literal(10);
        Stmt varDecl = new Stmt.Var(varKeyword, varName, null, varValue);

        // 2. Khởi dựng điều kiện: nếu (x > 5)
        Expr condition = new Expr.Binary(
            new Expr.Variable(new Token(TokenType.IDENTIFIER, "x")),
            new Token(TokenType.GREATER_THAN, ">"),
            new Expr.Literal(5)
        );

        // 3. Nhánh 'then': x = 20
        Stmt assignStmt = new Stmt.Expression(
            new Expr.Assign(
                new Token(TokenType.IDENTIFIER, "x"),
                new Expr.Literal(20)
            )
        );
        Stmt thenBranch = new Stmt.Block(List.of(assignStmt));
        Stmt ifStmt = new Stmt.If(condition, thenBranch, null);

        // 4. Khai báo hàm: hàm a() -> số_nguyên { ... }
        Token functionName = new Token(TokenType.IDENTIFIER, "a");
        Token returnType = new Token(TokenType.TYPE_INTEGER, "số_nguyên");
        Stmt functionAST = new Stmt.Function(
            functionName,
            List.of(),
            returnType,
            List.of(varDecl, ifStmt)
        );

        // 5. Sử dụng Visitor để in AST
        ASTPrinter printer = new ASTPrinter();
        System.out.println("Kết quả in AST (thông qua Visitor Pattern):");
        System.out.println(printer.print(functionAST));
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
            StringBuilder sb = new StringBuilder("{\n");
            for (Stmt s : stmt.statements) {
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
            StringBuilder sb = new StringBuilder("Hàm: ")
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
            StringBuilder sb = new StringBuilder("Nếu (")
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
            String typeStr = stmt.type != null ? " (Kiểu: " + stmt.type.lexeme() + ")" : " (Tự suy luận)";
            String initStr = stmt.initializer != null ? " Khởi tạo: " + stmt.initializer.accept(this) : "";
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
            StringBuilder sb = new StringBuilder("Gọi_hàm ").append(expr.callee.accept(this)).append(" với (");
            for (int i = 0; i < expr.arguments.size(); i++) {
                sb.append(expr.arguments.get(i).accept(this));
                if (i < expr.arguments.size() - 1) sb.append(", ");
            }
            sb.append(")");
            return sb.toString();
        }
    }
}