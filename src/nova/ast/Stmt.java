package nova.ast;

import nova.lexer.Token;
import java.util.List;

/**
 * Bểu diễn tất cả các câu lệnh thực thi hành động trong ngôn ngữ
 * @author XUAN HOAN
 */
public abstract class Stmt {
    public interface Visitor<R> {
        R visitBlockStmt(Block stmt);
        R visitExpressionStmt(Expression stmt);
        R visitFunctionStmt(Function stmt);
        R visitIfStmt(If stmt);
        R visitVarStmt(Var stmt);
        R visitReturnStmt(Return stmt);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    /**
     * Khối lệnh bọc trong dấu ngoặc nhọn
     * @author XUAN HOAN
     */
    public static class Block extends Stmt {
        public final List<Stmt> statements;

        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    /**
     * Lớp bọc 1 biểu thức đơn lẻ làm câu lệnh
     * @author XUAN HOAN
     */
    public static class Expression extends Stmt {
        public final Expr expression;

        public Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    /**
     * Cấu trúc định nghĩa tham số hàm.<br/>
     * Tham số hàm gồm tên và kiểu dữ liệu.
     * @author XUAN HOAN
     */
    public static class Parameter {
        public final Token name;
        public final Token type;

        public Parameter(Token name, Token type) {
            this.name = name;
            this.type = type;
        }
    }

    /**
     * Khai báo hàm mới. <br/>
     * Hàm mới gồm tên, Danh sách tham số, kiểu dữ liệu trả về và khối lệnh thân hàm
     */
    public static class Function extends Stmt {
        public final Token name;
        public final List<Parameter> parameters;
        public final Token returnType;
        public final List<Stmt> body;

        public Function(Token name, List<Parameter> parameters, Token returnType, List<Stmt> body) {
            this.name = name;
            this.parameters = parameters;
            this.returnType = returnType;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunctionStmt(this);
        }
    }

    /**
     * Lệnh điều kiện rẽ nhánh
     */
    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenBranch;
        public final Stmt elseBranch;

        public If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    /**
     * Khai báo biến tự suy luận hoặc cụ thể.<br/>
     * Có hỗ trợ tính khả biến (mut)
     * @author XUAN HOAN
     */
    public static class Var extends Stmt {
        public final Token keyword; // VAR, MUT, CONST
        public final Token name;
        public final Token type; // Có thể null nếu tự suy luận kiểu (Type Inference)
        public final Expr initializer; // Có thể null nếu khai báo không khởi tạo

        public Var(Token keyword, Token name, Token type, Expr initializer) {
            this.keyword = keyword;
            this.name = name;
            this.type = type;
            this.initializer = initializer;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    /**
     * Lệnh trả v giá trị kèm theo biểu thức tùy chọn
     * @author XUAN HOAN
     */
    public static class Return extends Stmt {
        public final Token keyword; // Từ khóa 'return' / 'trả_về'
        public final Expr value; // Có thể null nếu trả về trống (void)

        public Return(Token keyword, Expr value) {
            this.keyword = keyword;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }
}
