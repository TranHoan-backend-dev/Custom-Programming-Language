package nova.ast;

import nova.lexer.Token;
import java.util.List;

/**
 * Lớp trừu tượng biểu diễn các biểu thức trả về giá trị
 * @author XUAN HOAN
 */
public abstract class Expr {
    public interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitVariableExpr(Variable expr);
        R visitCallExpr(Call expr);
        R visitUnaryExpr(Unary expr);
        R visitGetExpr(Get expr);
        R visitStmtExpr(StmtExpr expr);
        /**
         * Xử lý biểu thức Tuple nhiều giá trị.
         * 
         * @param expr Biểu thức Tuple cần xử lý
         * @return Kết quả xử lý kiểu {@code R}
         */
        R visitTupleExpr(Tuple expr);
    }

    public abstract <R> R accept(Visitor<R> visitor);

    /**
     * Phép gán giá trị cho biến
     * @author XUAN HOAN
     */
    public static class Assign extends Expr {
        public final Token name;
        public final Expr value;

        public Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    /**
     * Các phép toán 2 ngôi.<br/>
     * <ul>
     *     <li>Phép cộng</li>
     *     <li>Phép trừ</li>
     *     <li>Phép nhân</li>
     *     <li>Phép chia</li>
     *     <li>So sánh</li>
     * </ul>
     * @author XUAN HOAN
     */
    public static class Binary extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;

        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    /**
     * Biểu thức được bọc trong dấu ngặc đơn
     * @author XUAN HOAN
     */
    public static class Grouping extends Expr {
        public final Expr expression;

        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    /**
     * Giá trị thô hoặc hằng số
     * @author XUAN HOAN
     */
    public static class Literal extends Expr {
        public final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    /**
     * Phép toán logic &&, ||
     * @author XUAN HOAN
     */
    public static class Logical extends Expr {
        public final Expr left;
        public final Token operator;
        public final Expr right;

        public Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    /**
     * Biến hoặc biến môi trường
     * @author XUAN HOAN
     */
    public static class Variable extends Expr {
        public final Token name;

        public Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    /**
     * Lời gọi hàm, gồm biểu thức được gọi và tham số (arg)
     * @author XUAN HOAN
     */
    public static class Call extends Expr {
        public final Expr callee;
        public final Token paren; // Dùng để xác định vị trí báo lỗi (ví dụ: ngoặc đóng ')')
        public final List<Expr> arguments;

        public Call(Expr callee, Token paren, List<Expr> arguments) {
            this.callee = callee;
            this.paren = paren;
            this.arguments = arguments;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }
    }

    /**
     * Biểu thức một ngôi như phủ định logic (!) hoặc dấu âm (-).
     * 
     * @author XUAN HOAN
     */
    public static class Unary extends Expr {
        /** Toán tử một ngôi (ví dụ: ! hoặc -) */
        public final Token operator;
        /** Biểu thức chịu tác động ở phía bên phải */
        public final Expr right;

        /**
         * Khởi tạo một biểu thức một ngôi.
         * 
         * @param operator Token toán tử một ngôi (! hoặc -)
         * @param right Biểu thức chịu tác động phía bên phải
         */
        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    /**
     * Biểu thức truy cập thuộc tính hoặc phương thức bằng toán tử chấm '.' (ví dụ: đối_tượng.thuộc_tính)
     * 
     * @author XUAN HOAN
     */
    public static class Get extends Expr {
        /** Biểu thức đối tượng được truy cập */
        public final Expr object;
        /** Token tên thuộc tính hoặc phương thức */
        public final Token name;

        /**
         * Khởi tạo biểu thức truy cập thuộc tính/phương thức.
         * 
         * @param object Biểu thức đối tượng
         * @param name Token tên thuộc tính hoặc phương thức
         */
        public Get(Expr object, Token name) {
            this.object = object;
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGetExpr(this);
        }
    }

    /**
     * Biểu thức bọc câu lệnh (ví dụ câu lệnh if hoặc switch hoạt động như biểu thức)
     * 
     * @author XUAN HOAN
     */
    public static class StmtExpr extends Expr {
        /** Câu lệnh được bọc bên trong biểu thức */
        public final Stmt statement;

        /**
         * Khởi tạo biểu thức bọc câu lệnh.
         * 
         * @param statement Câu lệnh cần bọc
         */
        public StmtExpr(Stmt statement) {
            this.statement = statement;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitStmtExpr(this);
        }
    }

    /**
     * Biểu thức Tuple đại diện cho danh sách nhiều biểu thức ngăn cách bởi dấu phẩy đặt trong dấu ngoặc đơn (ví dụ: (10, 20)).
     * 
     * @author XUAN HOAN
     */
    public static class Tuple extends Expr {
        /** Danh sách các biểu thức thành phần trong Tuple */
        public final List<Expr> expressions;

        /**
         * Khởi tạo một biểu thức Tuple.
         * 
         * @param expressions Danh sách các biểu thức thành phần
         */
        public Tuple(List<Expr> expressions) {
            this.expressions = expressions;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitTupleExpr(this);
        }
    }
}
