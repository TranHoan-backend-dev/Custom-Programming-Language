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
}
