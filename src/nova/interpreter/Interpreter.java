package nova.interpreter;

import nova.ast.Expr;
import nova.ast.Stmt;
import nova.lexer.Token;
import nova.lexer.TokenType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Trình thông dịch cây cú pháp trừu tượng (AST) cho ngôn ngữ Nova.
 * Lớp này chịu trách nhiệm thực thi các câu lệnh và đánh giá các biểu thức runtime.
 * Đồng thời, nó lưu trữ thông tin độ sâu tầm vực tĩnh được phân tích bởi {@link Resolver}.
 * 
 * @author XUAN HOAN
 */
public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    /** Bản đồ lưu trữ độ sâu tầm vực tĩnh của các biểu thức cục bộ. */
    private final Map<Expr, Integer> locals = new HashMap<>();

    /** Môi trường toàn cầu của chương trình. */
    public final Environment globals = new Environment();

    /** Môi trường thực thi hiện tại. */
    private Environment environment = globals;

    /** Stream xuất dữ liệu đầu ra của trình thông dịch (mặc định là System.out). */
    private final java.io.PrintStream out;

    /**
     * Khởi tạo một trình thông dịch Nova mới xuất ra System.out.
     */
    public Interpreter() {
        this(System.out);
    }

    /**
     * Khởi tạo một trình thông dịch Nova mới với PrintStream chỉ định.
     * Đăng ký các hàm dựng sẵn như print, println, in, in_dòng_mới, printf, in_định_dạng.
     * 
     * @param out PrintStream dùng để ghi đầu ra của các câu lệnh in
     */
    public Interpreter(java.io.PrintStream out) {
        this.out = out;
        defineBuiltInFunctions();
    }

    /**
     * Đăng ký các hàm dựng sẵn vào môi trường toàn cục.
     */
    private void defineBuiltInFunctions() {
        // Đăng ký các hàm in tiêu chuẩn tiếng Anh
        globals.define("print", new NovaCallable() {
            @Override
            public int arity() {
                return -1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                printFormatted(arguments, false);
                return null;
            }
        });

        globals.define("println", new NovaCallable() {
            @Override
            public int arity() {
                return -1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                printFormatted(arguments, true);
                return null;
            }
        });

        // Đăng ký các hàm in tiếng Việt tương ứng
        globals.define("in", globals.getByName("print"));
        globals.define("in_dòng_mới", globals.getByName("println"));

        // Đăng ký hàm in định dạng
        globals.define("printf", new NovaCallable() {
            @Override
            public int arity() {
                return -1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                printFormatted(arguments, false);
                return null;
            }
        });

        globals.define("in_định_dạng", globals.getByName("printf"));
    }

    /**
     * Đặt lại môi trường thực thi (xóa sạch biến cục bộ, toàn cục, chỉ giữ lại các hàm dựng sẵn).
     */
    public void reset() {
        this.globals.getValues().clear();
        this.environment = this.globals;
        defineBuiltInFunctions();
    }

    /**
     * Lấy danh sách các biến toàn cục hiện tại.
     */
    public Map<String, Object> getGlobals() {
        return this.globals.getValues();
    }

    /**
     * Đánh giá biểu thức dành riêng cho REPL để có thể in kết quả.
     */
    public Object evaluateExpressionForRepl(Expr expr) {
        return evaluate(expr);
    }

    /**
     * Đăng ký thông tin độ sâu tầm vực tĩnh của một biểu thức cục bộ.
     * Phương thức này được gọi bởi {@link Resolver} trong pha phân tích tĩnh.
     * 
     * @param expr Biểu thức cần giải quyết tầm vực
     * @param depth Độ sâu tầm vực (số lượng scope cục bộ nằm giữa scope hiện tại và scope khai báo)
     */
    public void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    /**
     * Lấy khoảng cách tầm vực tĩnh đã được giải quyết của một biểu thức.
     * 
     * @param expr Biểu thức cần truy vấn khoảng cách
     * @return Khoảng cách đến scope khai báo, hoặc -1 nếu là tầm vực toàn cầu
     */
    public int getDistance(Expr expr) {
        return locals.getOrDefault(expr, -1);
    }

    /**
     * Thực thi danh sách các câu lệnh AST.
     * 
     * @param statements Danh sách các câu lệnh cần thực thi
     */
    public void interpret(List<Stmt> statements) {
        try {
            for (var statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            System.err.println(error.getMessage());
        }
    }

    /**
     * Thực thi một câu lệnh đơn lẻ.
     * 
     * @param stmt Câu lệnh cần thực thi
     */
    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    /**
     * Đánh giá một biểu thức và trả về giá trị runtime tương ứng.
     * 
     * @param expr Biểu thức cần đánh giá
     * @return Kết quả đánh giá biểu thức
     */
    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    /**
     * Thực thi một khối câu lệnh trong một môi trường cụ thể.
     * Môi trường hiện tại sẽ được thay thế tạm thời bằng môi trường mới,
     * và được khôi phục sau khi khối lệnh thực thi xong.
     * 
     * @param statements Danh sách các câu lệnh trong khối
     * @param environment Môi trường cục bộ mới để thực thi khối
     */
    public void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;
            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    /**
     * Hỗ trợ định dạng đầu ra và in dữ liệu cho các hàm print/println/printf/in/in_dòng_mới/in_định_dạng.
     * Xử lý cả hai cơ chế: nội suy trực tiếp chuỗi chứa {tên_biến} và định dạng theo vị trí với {}.
     * 
     * @param arguments Danh sách các đối số truyền vào hàm in
     * @param newline Cờ xác định có xuống dòng sau khi in hay không
     */
    private void printFormatted(List<Object> arguments, boolean newline) {
        if (arguments.isEmpty()) {
            if (newline) {
                out.println();
            }
            return;
        }

        Object firstArg = arguments.get(0);
        if (firstArg instanceof String) {
            String format = (String) firstArg;

            // Bước 1: Nội suy trực tiếp các chuỗi dạng {tên_biến}
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{([^}]+)\\}");
            java.util.regex.Matcher matcher = pattern.matcher(format);
            StringBuilder sb = new StringBuilder();
            while (matcher.find()) {
                String varName = matcher.group(1);
                Object val = environment.getByName(varName);
                String valStr = val == null ? "null" : stringify(val);
                matcher.appendReplacement(sb, java.util.regex.Matcher.quoteReplacement(valStr));
            }
            matcher.appendTail(sb);
            String interpolated = sb.toString();

            // Bước 2: Định dạng theo vị trí {}
            StringBuilder finalSb = new StringBuilder();
            int argIndex = 1;
            int i = 0;
            while (i < interpolated.length()) {
                if (i < interpolated.length() - 1 && interpolated.charAt(i) == '{' && interpolated.charAt(i + 1) == '}') {
                    if (argIndex < arguments.size()) {
                        finalSb.append(stringify(arguments.get(argIndex++)));
                    } else {
                        finalSb.append("{}");
                    }
                    i += 2;
                } else {
                    finalSb.append(interpolated.charAt(i));
                    i++;
                }
            }

            if (argIndex < arguments.size()) {
                for (int idx = argIndex; idx < arguments.size(); idx++) {
                    if (finalSb.length() > 0 && finalSb.charAt(finalSb.length() - 1) != ' ') {
                        finalSb.append(" ");
                    }
                    finalSb.append(stringify(arguments.get(idx)));
                }
            }

            if (newline) {
                out.println(finalSb.toString());
            } else {
                out.print(finalSb.toString());
            }
        } else {
            // Không phải chuỗi định dạng, in ra chuỗi đại diện phân tách bởi dấu cách
            StringBuilder sb = new StringBuilder();
            for (int idx = 0; idx < arguments.size(); idx++) {
                sb.append(stringify(arguments.get(idx)));
                if (idx < arguments.size() - 1) {
                    sb.append(" ");
                }
            }
            if (newline) {
                out.println(sb.toString());
            } else {
                out.print(sb.toString());
            }
        }
    }

    /**
     * Chuyển đổi một đối tượng runtime Java thành chuỗi biểu diễn tương ứng trong Nova.
     * Hỗ trợ định dạng kiểu số thực (loại bỏ .0 thừa) và định dạng Tuple (List) thành chuỗi dạng (a, b, ...).
     * 
     * @param object Đối tượng cần chuyển đổi
     * @return Chuỗi đại diện của đối tượng
     */
    public static String stringify(Object object) {
        if (object == null) return "null";

        if (object instanceof List) {
            List<?> list = (List<?>) object;
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for (int i = 0; i < list.size(); i++) {
                sb.append(stringify(list.get(i)));
                if (i < list.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            return sb.toString();
        }

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        if (object instanceof Float) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    /**
     * Đánh giá xem một đối tượng có mang giá trị chân trị (truthy) hay không.
     * Chỉ có null và Boolean.FALSE là mang giá trị Falsy, các đối tượng khác đều là Truthy.
     * 
     * @param object Đối tượng cần kiểm tra chân trị
     * @return {@code true} nếu là Truthy, ngược lại {@code false}
     */
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (Boolean) object;
        return true;
    }

    /**
     * So sánh đẳng trị giữa hai đối tượng runtime trong Nova.
     * Tự động ép kiểu rộng hơn (coercion) khi so sánh hai số khác kiểu.
     * 
     * @param a Đối tượng thứ nhất
     * @param b Đối tượng thứ hai
     * @return {@code true} nếu hai đối tượng bằng nhau, ngược lại {@code false}
     */
    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;

        if (a instanceof Number && b instanceof Number) {
            return ((Number) a).doubleValue() == ((Number) b).doubleValue();
        }

        return a.equals(b);
    }

    /**
     * Xác định xem một giá trị runtime có khớp với một biểu thức mẫu trong switch-case hay không.
     * Hỗ trợ mẫu mặc định '_' và 'sai' (từ biểu thức Literal(false)).
     * 
     * @param value Giá trị so khớp
     * @param pattern Biểu thức mẫu
     * @return {@code true} nếu khớp, ngược lại {@code false}
     */
    private boolean isMatch(Object value, Expr pattern) {
        if (pattern instanceof Expr.Variable) {
            Expr.Variable varExpr = (Expr.Variable) pattern;
            if (varExpr.name.lexeme().equals("_")) {
                return true;
            }
        }

        Object patternVal = evaluate(pattern);

        if (Boolean.FALSE.equals(patternVal) && !(value instanceof Boolean)) {
            return true;
        }

        return isEqual(value, patternVal);
    }

    /**
     * Đánh giá một câu lệnh như là một biểu thức để lấy giá trị trả về cuối cùng (Rust-style block value).
     * Phục vụ cho biểu thức if-else, block values và switch expression.
     * 
     * @param stmt Câu lệnh cần đánh giá
     * @return Giá trị trả về của câu lệnh
     */
    private Object evaluateStatementAsExpr(Stmt stmt) {
        if (stmt instanceof Stmt.Expression) {
            return evaluate(((Stmt.Expression) stmt).expression);
        }
        if (stmt instanceof Stmt.Block) {
            Stmt.Block block = (Stmt.Block) stmt;
            if (block.statements.isEmpty()) return null;

            Environment previous = this.environment;
            try {
                this.environment = new Environment(previous);
                for (int i = 0; i < block.statements.size() - 1; i++) {
                    execute(block.statements.get(i));
                }
                return evaluateStatementAsExpr(block.statements.get(block.statements.size() - 1));
            } finally {
                this.environment = previous;
            }
        }
        if (stmt instanceof Stmt.If) {
            Stmt.If ifStmt = (Stmt.If) stmt;
            Object condition = evaluate(ifStmt.condition);
            if (isTruthy(condition)) {
                return evaluateStatementAsExpr(ifStmt.thenBranch);
            } else if (ifStmt.elseBranch != null) {
                return evaluateStatementAsExpr(ifStmt.elseBranch);
            }
            return null;
        }
        if (stmt instanceof Stmt.Switch) {
            Stmt.Switch switchStmt = (Stmt.Switch) stmt;
            Object switchValue = evaluate(switchStmt.value);
            for (Stmt.SwitchCase sc : switchStmt.cases) {
                for (Expr pattern : sc.patterns) {
                    if (isMatch(switchValue, pattern)) {
                        return evaluateStatementAsExpr(sc.body);
                    }
                }
            }
            return null;
        }
        execute(stmt);
        return null;
    }

    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object val = evaluate(expr.value);
        int distance = locals.getOrDefault(expr, -1);
        if (distance != -1) {
            environment.assignAt(distance, expr.name, val);
        } else {
            globals.assign(expr.name, val);
        }
        return val;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        String op = expr.operator.lexeme();

        switch (op) {
            case "+":
                if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                }
                if (left instanceof Double || right instanceof Double) {
                    return ((Number) left).doubleValue() + ((Number) right).doubleValue();
                }
                if (left instanceof Float || right instanceof Float) {
                    return ((Number) left).floatValue() + ((Number) right).floatValue();
                }
                if (left instanceof Long || right instanceof Long) {
                    return ((Number) left).longValue() + ((Number) right).longValue();
                }
                return ((Number) left).intValue() + ((Number) right).intValue();
            case "-":
                if (left instanceof Double || right instanceof Double) {
                    return ((Number) left).doubleValue() - ((Number) right).doubleValue();
                }
                if (left instanceof Float || right instanceof Float) {
                    return ((Number) left).floatValue() - ((Number) right).floatValue();
                }
                if (left instanceof Long || right instanceof Long) {
                    return ((Number) left).longValue() - ((Number) right).longValue();
                }
                return ((Number) left).intValue() - ((Number) right).intValue();
            case "*":
                if (left instanceof Double || right instanceof Double) {
                    return ((Number) left).doubleValue() * ((Number) right).doubleValue();
                }
                if (left instanceof Float || right instanceof Float) {
                    return ((Number) left).floatValue() * ((Number) right).floatValue();
                }
                if (left instanceof Long || right instanceof Long) {
                    return ((Number) left).longValue() * ((Number) right).longValue();
                }
                return ((Number) left).intValue() * ((Number) right).intValue();
            case "/":
                if (left instanceof Double || right instanceof Double) {
                    double r = ((Number) right).doubleValue();
                    if (r == 0) throw new RuntimeError(expr.operator, "Lỗi chia cho 0.");
                    return ((Number) left).doubleValue() / r;
                }
                if (left instanceof Float || right instanceof Float) {
                    float r = ((Number) right).floatValue();
                    if (r == 0) throw new RuntimeError(expr.operator, "Lỗi chia cho 0.");
                    return ((Number) left).floatValue() / r;
                }
                if (left instanceof Long || right instanceof Long) {
                    long r = ((Number) right).longValue();
                    if (r == 0) throw new RuntimeError(expr.operator, "Lỗi chia cho 0.");
                    return ((Number) left).longValue() / r;
                }
                {
                    int r = ((Number) right).intValue();
                    if (r == 0) throw new RuntimeError(expr.operator, "Lỗi chia cho 0.");
                    return ((Number) left).intValue() / r;
                }
            case "%":
                if (left instanceof Double || right instanceof Double) {
                    double r = ((Number) right).doubleValue();
                    if (r == 0) throw new RuntimeError(expr.operator, "Lỗi chia cho 0.");
                    return ((Number) left).doubleValue() % r;
                }
                if (left instanceof Float || right instanceof Float) {
                    float r = ((Number) right).floatValue();
                    if (r == 0) throw new RuntimeError(expr.operator, "Lỗi chia cho 0.");
                    return ((Number) left).floatValue() % r;
                }
                if (left instanceof Long || right instanceof Long) {
                    long r = ((Number) right).longValue();
                    if (r == 0) throw new RuntimeError(expr.operator, "Lỗi chia cho 0.");
                    return ((Number) left).longValue() % r;
                }
                {
                    int r = ((Number) right).intValue();
                    if (r == 0) throw new RuntimeError(expr.operator, "Lỗi chia cho 0.");
                    return ((Number) left).intValue() % r;
                }
            case "<":
                if (left instanceof Character && right instanceof Character) {
                    return (Character) left < (Character) right;
                }
                if (left instanceof Double || right instanceof Double) {
                    return ((Number) left).doubleValue() < ((Number) right).doubleValue();
                }
                if (left instanceof Float || right instanceof Float) {
                    return ((Number) left).floatValue() < ((Number) right).floatValue();
                }
                if (left instanceof Long || right instanceof Long) {
                    return ((Number) left).longValue() < ((Number) right).longValue();
                }
                return ((Number) left).intValue() < ((Number) right).intValue();
            case ">":
                if (left instanceof Character && right instanceof Character) {
                    return (Character) left > (Character) right;
                }
                if (left instanceof Double || right instanceof Double) {
                    return ((Number) left).doubleValue() > ((Number) right).doubleValue();
                }
                if (left instanceof Float || right instanceof Float) {
                    return ((Number) left).floatValue() > ((Number) right).floatValue();
                }
                if (left instanceof Long || right instanceof Long) {
                    return ((Number) left).longValue() > ((Number) right).longValue();
                }
                return ((Number) left).intValue() > ((Number) right).intValue();
            case "<=":
                if (left instanceof Character && right instanceof Character) {
                    return (Character) left <= (Character) right;
                }
                if (left instanceof Double || right instanceof Double) {
                    return ((Number) left).doubleValue() <= ((Number) right).doubleValue();
                }
                if (left instanceof Float || right instanceof Float) {
                    return ((Number) left).floatValue() <= ((Number) right).floatValue();
                }
                if (left instanceof Long || right instanceof Long) {
                    return ((Number) left).longValue() <= ((Number) right).longValue();
                }
                return ((Number) left).intValue() <= ((Number) right).intValue();
            case ">=":
                if (left instanceof Character && right instanceof Character) {
                    return (Character) left >= (Character) right;
                }
                if (left instanceof Double || right instanceof Double) {
                    return ((Number) left).doubleValue() >= ((Number) right).doubleValue();
                }
                if (left instanceof Float || right instanceof Float) {
                    return ((Number) left).floatValue() >= ((Number) right).floatValue();
                }
                if (left instanceof Long || right instanceof Long) {
                    return ((Number) left).longValue() >= ((Number) right).longValue();
                }
                return ((Number) left).intValue() >= ((Number) right).intValue();
            case "==":
                return isEqual(left, right);
            case "!=":
            case "khác":
            case "is_not":
                return !isEqual(left, right);
        }
        return null;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if (expr.operator.lexeme().equals("||") || expr.operator.lexeme().equals("hoặc") || expr.operator.lexeme().equals("or")) {
            if (isTruthy(left)) return left;
        } else {
            if (!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        int distance = locals.getOrDefault(expr, -1);
        if (distance != -1) {
            return environment.getAt(distance, expr.name.lexeme());
        } else {
            return globals.get(expr.name);
        }
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object calleeVal = evaluate(expr.callee);
        java.util.List<Object> args = new java.util.ArrayList<>();
        for (Expr arg : expr.arguments) {
            args.add(evaluate(arg));
        }

        if (!(calleeVal instanceof NovaCallable)) {
            throw new RuntimeError(expr.paren, "Đối tượng được gọi không phải là hàm.");
        }

        NovaCallable function = (NovaCallable) calleeVal;
        if (function.arity() != -1 && args.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Sai số lượng đối số truyền vào hàm. Yêu cầu " + function.arity() + " nhưng nhận " + args.size() + ".");
        }
        return function.call(this, args);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        String op = expr.operator.lexeme();

        switch (op) {
            case "-":
                if (right instanceof Double) return -((Double) right);
                if (right instanceof Float) return -((Float) right);
                if (right instanceof Long) return -((Long) right);
                return -((Integer) right);
            case "!":
            case "không":
            case "not":
                return !isTruthy(right);
        }
        return null;
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object obj = evaluate(expr.object);
        String propName = expr.name.lexeme();

        if (obj instanceof String) {
            String str = (String) obj;
            if (propName.equals("length")) {
                return new NovaCallable() {
                    @Override
                    public int arity() {
                        return 0;
                    }

                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        return str.length();
                    }
                };
            }
            if (propName.equals("toUpperCase")) {
                return new NovaCallable() {
                    @Override
                    public int arity() {
                        return 0;
                    }

                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        return str.toUpperCase();
                    }
                };
            }
            if (propName.equals("to_string")) {
                return new NovaCallable() {
                    @Override
                    public int arity() {
                        return 0;
                    }

                    @Override
                    public Object call(Interpreter interpreter, List<Object> arguments) {
                        return str;
                    }
                };
            }
        }

        if (propName.equals("to_string")) {
            return new NovaCallable() {
                @Override
                public int arity() {
                    return 0;
                }

                @Override
                public Object call(Interpreter interpreter, List<Object> arguments) {
                    return stringify(obj);
                }
            };
        }

        throw new RuntimeError(expr.name, "Không tìm thấy thuộc tính hoặc phương thức '" + propName + "' trên đối tượng.");
    }

    @Override
    public Object visitStmtExpr(Expr.StmtExpr expr) {
        return evaluateStatementAsExpr(expr.statement);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        NovaFunction function = new NovaFunction(stmt, environment);
        environment.define(stmt.name.lexeme(), function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        Object condition = evaluate(stmt.condition);
        if (isTruthy(condition)) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    /**
     * Trích xuất các tên biến riêng lẻ từ biểu thức giải cấu trúc Tuple (ví dụ: "(a, b)").
     * Phương thức này phân tích chuỗi lexeme dạng "(a, b)" để lấy danh sách các Token định danh tương ứng.
     * 
     * @param parent Token chứa chuỗi giải cấu trúc cần phân tách
     * @return Danh sách các Token định danh cho từng biến được giải cấu trúc
     */
    private List<Token> extractDestructuredNames(Token parent) {
        List<Token> result = new ArrayList<>();
        String lexeme = parent.lexeme();
        if (lexeme.startsWith("(") && lexeme.endsWith(")")) {
            String content = lexeme.substring(1, lexeme.length() - 1);
            String[] parts = content.split(",");
            for (String part : parts) {
                String nameStr = part.trim();
                if (!nameStr.isEmpty()) {
                    result.add(new Token(TokenType.IDENTIFIER, nameStr));
                }
            }
        }
        return result;
    }

    /**
     * Thực hiện đánh giá biểu thức Tuple nhiều giá trị.
     * Phương thức này duyệt qua tất cả các biểu thức thành phần trong Tuple,
     * thực hiện đánh giá từng biểu thức và gom chúng lại thành một danh sách các giá trị.
     * 
     * @param expr Biểu thức Tuple cần đánh giá
     * @return Danh sách chứa các giá trị sau khi đã được đánh giá từ các biểu thức thành phần
     */
    @Override
    public Object visitTupleExpr(Expr.Tuple expr) {
        List<Object> elements = new ArrayList<>();
        for (Expr element : expr.expressions) {
            elements.add(evaluate(element));
        }
        return elements;
    }

    /**
     * Thực thi câu lệnh khai báo biến hoặc hằng số.
     * Hỗ trợ cả khai báo biến thông thường và khai báo giải cấu trúc Tuple (destructuring).
     * Đối với khai báo giải cấu trúc, biểu thức khởi tạo bắt buộc phải trả về một Tuple (List),
     * và từng phần tử của Tuple sẽ được gán cho các biến thành phần tương ứng.
     * 
     * @param stmt Câu lệnh khai báo biến cần thực thi
     * @return {@code null} vì đây là câu lệnh không trả về giá trị
     * @throws RuntimeError nếu giá trị khởi tạo cho giải cấu trúc không phải là một Tuple
     */
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object val = null;
        if (stmt.initializer != null) {
            val = evaluate(stmt.initializer);
        }

        if (stmt.name.lexeme().startsWith("(") && stmt.name.lexeme().endsWith(")")) {
            List<Token> names = extractDestructuredNames(stmt.name);
            if (val instanceof List) {
                List<?> values = (List<?>) val;
                for (int i = 0; i < names.size(); i++) {
                    Object itemVal = i < values.size() ? values.get(i) : null;
                    environment.define(names.get(i).lexeme(), itemVal);
                }
            } else {
                throw new RuntimeError(stmt.name, "Giá trị gán cho khai báo giải cấu trúc phải là một Tuple.");
            }
        } else {
            environment.define(stmt.name.lexeme(), val);
        }
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object val = null;
        if (stmt.value != null) {
            val = evaluate(stmt.value);
        }
        throw new ReturnException(val);
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        try {
            while (isTruthy(evaluate(stmt.condition))) {
                try {
                    execute(stmt.body);
                } catch (ContinueException e) {
                    // Tiếp tục vòng lặp tiếp theo
                }
            }
        } catch (BreakException e) {
            // Thoát khỏi vòng lặp
        }
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        if (stmt.isForEach) {
            Object col = evaluate(stmt.end);
            if (col instanceof List) {
                List<?> list = (List<?>) col;
                Environment previous = this.environment;
                try {
                    for (Object item : list) {
                        this.environment = new Environment(previous);
                        this.environment.define(stmt.name.lexeme(), item);
                        try {
                            execute(stmt.body);
                        } catch (ContinueException e) {
                            // continue
                        }
                    }
                } catch (BreakException e) {
                    // break
                } finally {
                    this.environment = previous;
                }
            } else {
                throw new RuntimeError(stmt.name, "Chỉ có thể duyệt qua một DanhSách.");
            }
        } else {
            int startVal = ((Number) evaluate(stmt.start)).intValue();
            int endVal = ((Number) evaluate(stmt.end)).intValue();
            boolean inclusive = stmt.operator.lexeme().equals("đến_hết");

            Environment previous = this.environment;
            try {
                if (startVal <= endVal) {
                    for (int i = startVal; inclusive ? i <= endVal : i < endVal; i++) {
                        this.environment = new Environment(previous);
                        this.environment.define(stmt.name.lexeme(), i);
                        try {
                            execute(stmt.body);
                        } catch (ContinueException e) {
                            // continue
                        }
                    }
                } else {
                    for (int i = startVal; inclusive ? i >= endVal : i > endVal; i--) {
                        this.environment = new Environment(previous);
                        this.environment.define(stmt.name.lexeme(), i);
                        try {
                            execute(stmt.body);
                        } catch (ContinueException e) {
                            // continue
                        }
                    }
                }
            } catch (BreakException e) {
                // break
            } finally {
                this.environment = previous;
            }
        }
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new BreakException();
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        throw new ContinueException();
    }

    @Override
    public Void visitSwitchStmt(Stmt.Switch stmt) {
        Object value = evaluate(stmt.value);
        for (Stmt.SwitchCase sc : stmt.cases) {
            boolean matched = false;
            for (Expr pattern : sc.patterns) {
                if (isMatch(value, pattern)) {
                    matched = true;
                    break;
                }
            }
            if (matched) {
                execute(sc.body);
                break;
            }
        }
        return null;
    }
}
