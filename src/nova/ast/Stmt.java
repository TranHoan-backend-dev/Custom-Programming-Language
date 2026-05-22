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
        R visitWhileStmt(While stmt);
        R visitForStmt(For stmt);
        R visitBreakStmt(Break stmt);
        R visitContinueStmt(Continue stmt);
        R visitSwitchStmt(Switch stmt);
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

    /**
     * Lệnh vòng lặp điều kiện 'lặp' (while loop).
     * 
     * @author XUAN HOAN
     */
    public static class While extends Stmt {
        /** Biểu thức điều kiện kiểm tra trước mỗi vòng lặp */
        public final Expr condition;
        /** Thân vòng lặp cần thực thi */
        public final Stmt body;

        /**
         * Khởi tạo một câu lệnh vòng lặp while.
         * 
         * @param condition Biểu thức điều kiện
         * @param body Thân vòng lặp
         */
        public While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    /**
     * Lệnh vòng lặp 'duyệt' (for loop).
     * Hỗ trợ cả hai hình thức: duyệt qua khoảng số (range-based) và duyệt qua danh sách (collection-based).
     * 
     * @author XUAN HOAN
     */
    public static class For extends Stmt {
        /** Tên biến lặp */
        public final Token name;
        /** Biểu thức bắt đầu của khoảng số (null nếu duyệt qua danh sách) */
        public final Expr start;
        /** Toán tử khoảng số RANGE hoặc RANGE_INCLUSIVE (null nếu duyệt qua danh sách) */
        public final Token operator;
        /** Biểu thức kết thúc của khoảng số hoặc biểu thức danh sách cần duyệt */
        public final Expr end;
        /** Thân vòng lặp */
        public final Stmt body;
        /** Cờ xác định đây là vòng lặp duyệt qua danh sách (collection-based) */
        public final boolean isForEach;

        /**
         * Khởi tạo câu lệnh vòng lặp duyệt khoảng số.
         * 
         * @param name Tên biến lặp
         * @param start Biểu thức bắt đầu
         * @param operator Toán tử khoảng
         * @param end Biểu thức kết thúc
         * @param body Thân vòng lặp
         */
        public For(Token name, Expr start, Token operator, Expr end, Stmt body) {
            this.name = name;
            this.start = start;
            this.operator = operator;
            this.end = end;
            this.body = body;
            this.isForEach = false;
        }

        /**
         * Khởi tạo câu lệnh vòng lặp duyệt qua danh sách.
         * 
         * @param name Tên biến lặp
         * @param collection Biểu thức danh sách cần duyệt
         * @param body Thân vòng lặp
         */
        public For(Token name, Expr collection, Stmt body) {
            this.name = name;
            this.start = null;
            this.operator = null;
            this.end = collection;
            this.body = body;
            this.isForEach = true;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitForStmt(this);
        }
    }

    /**
     * Lệnh dừng vòng lặp 'dừng' (break).
     * 
     * @author XUAN HOAN
     */
    public static class Break extends Stmt {
        /** Token của từ khóa 'dừng' */
        public final Token keyword;

        /**
         * Khởi tạo câu lệnh dừng.
         * 
         * @param keyword Token của từ khóa 'dừng'
         */
        public Break(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }
    }

    /**
     * Lệnh tiếp tục vòng lặp 'tiếp' (continue).
     * 
     * @author XUAN HOAN
     */
    public static class Continue extends Stmt {
        /** Token của từ khóa 'tiếp' */
        public final Token keyword;

        /**
         * Khởi tạo câu lệnh tiếp tục.
         * 
         * @param keyword Token của từ khóa 'tiếp'
         */
        public Continue(Token keyword) {
            this.keyword = keyword;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStmt(this);
        }
    }

    /**
     * Cấu trúc nhánh rẽ trong lệnh 'trường_hợp' (Switch case).
     * 
     * @author XUAN HOAN
     */
    public static class SwitchCase {
        /** Danh sách các mẫu khớp (phân tách bởi dấu gạch đứng |) */
        public final List<Expr> patterns;
        /** Câu lệnh thực thi tương ứng khi khớp mẫu */
        public final Stmt body;

        /**
         * Khởi tạo một nhánh rẽ trong switch.
         * 
         * @param patterns Danh sách các biểu thức mẫu
         * @param body Câu lệnh thực thi
         */
        public SwitchCase(List<Expr> patterns, Stmt body) {
            this.patterns = patterns;
            this.body = body;
        }
    }

    /**
     * Lệnh rẽ nhánh nhiều trường hợp 'trường_hợp' (Switch statement).
     * 
     * @author XUAN HOAN
     */
    public static class Switch extends Stmt {
        /** Biểu thức cần kiểm tra giá trị */
        public final Expr value;
        /** Danh sách các nhánh rẽ trường hợp */
        public final List<SwitchCase> cases;

        /**
         * Khởi tạo câu lệnh switch.
         * 
         * @param value Biểu thức cần kiểm tra
         * @param cases Danh sách các nhánh rẽ
         */
        public Switch(Expr value, List<SwitchCase> cases) {
            this.value = value;
            this.cases = cases;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitSwitchStmt(this);
        }
    }
}
