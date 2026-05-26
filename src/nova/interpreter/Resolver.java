package nova.interpreter;

import nova.ast.Expr;
import nova.ast.Stmt;
import nova.interpreter.exception.SemanticError;
import nova.lexer.Token;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static nova.interpreter.Interpreter.getTokens;

/**
 * Lớp Resolver thực hiện phân tích tĩnh (Static Analysis) ngay sau khi có cây AST và trước khi chạy trình thông dịch.
 * Nhiệm vụ chính của Resolver là liên kết các tham chiếu biến với khai báo tầm vực cục bộ của chúng (Lexical Scope Resolution).
 * Đồng thời, lớp này kiểm tra các quy tắc ngữ nghĩa khác như tính khả biến (mutability), hằng số, và tính hợp lệ của lệnh nhảy (break/continue).
 * 
 * @author XUAN HOAN
 */
public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    
    /** Stack đại diện cho các tầm vực cục bộ lồng nhau. Mỗi phần tử chứa bảng ánh xạ tên biến với trạng thái của nó. */
    private final Stack<Map<String, VariableState>> scopes = new Stack<>();
    
    /** Danh sách các lỗi ngữ nghĩa phát hiện được trong quá trình phân tích tĩnh. */
    private final List<SemanticError> errors = new ArrayList<>();

    /** Bảng ánh xạ lưu trữ trạng thái của các biến toàn cục để giải quyết tĩnh tầm vực biến toàn cục. */
    private final Map<String, VariableState> globals = new HashMap<>();

    /** Loại hàm hiện tại đang phân tích (dùng để kiểm tra tính hợp lệ của câu lệnh return). */
    private FunctionType currentFunction = FunctionType.NONE;

    /** Cờ đánh dấu xem Resolver có đang ở trong thân của một vòng lặp hay không (kiểm tra break/continue). */
    private boolean isInLoop = false;

    /**
     * Enum định nghĩa các loại hàm để kiểm soát câu lệnh return.
     */
    private enum FunctionType {
        NONE,
        FUNCTION
    }

    /**
     * Lớp lưu trữ trạng thái của một biến trong quá trình phân tích tầm vực tĩnh.
     */
    private static class VariableState {
        /** Token tên của biến. */
        final Token name;
        /** Token từ khóa khai báo biến (ví dụ: var, mut, const, biến, khả_biến, hằng_số hoặc kiểu dữ liệu). */
        final Token keyword;
        /** Trạng thái đã được khởi tạo hoàn toàn hay chưa. */
        boolean isReady;

        VariableState(Token name, Token keyword, boolean isReady) {
            this.name = name;
            this.keyword = keyword;
            this.isReady = isReady;
        }
    }

    /**
     * Khởi tạo một đối tượng Resolver liên kết với Interpreter chỉ định.
     * 
     * @param interpreter Trình thông dịch cần liên kết để lưu trữ thông tin tầm vực
     */
    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * Thực hiện phân tích toàn bộ danh sách câu lệnh trong chương trình.
     * 
     * @param statements Danh sách các câu lệnh AST cần giải quyết
     * @return Danh sách lỗi ngữ nghĩa phát hiện được (nếu có)
     */
    public List<SemanticError> resolve(List<Stmt> statements) {
        for (var statement : statements) {
            resolve(statement);
        }
        return errors;
    }

    /**
     * Kiểm tra xem Resolver có phát hiện lỗi ngữ nghĩa nào hay không.
     * 
     * @return {@code true} nếu có lỗi, ngược lại {@code false}
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Báo lỗi ngữ nghĩa mới tại một token chỉ định.
     * 
     * @param token Token gây lỗi
     * @param message Thông điệp mô tả chi tiết lỗi
     */
    private void error(Token token, String message) {
        errors.add(new SemanticError(token, message));
    }

    /**
     * Giải quyết câu lệnh AST bằng cách cho nó chấp nhận Visitor Resolver.
     * 
     * @param stmt Câu lệnh cần phân tích
     */
    private void resolve(Stmt stmt) {
        if (stmt != null) {
            stmt.accept(this);
        }
    }

    /**
     * Giải quyết biểu thức AST bằng cách cho nó chấp nhận Visitor Resolver.
     * 
     * @param expr Biểu thức cần phân tích
     */
    private void resolve(Expr expr) {
        if (expr != null) {
            expr.accept(this);
        }
    }

    /**
     * Tạo một tầm vực cục bộ mới (mở khối block).
     */
    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Hủy bỏ tầm vực cục bộ hiện tại (đóng khối block).
     */
    private void endScope() {
        scopes.pop();
    }

    /**
     * Trích xuất các tên biến riêng lẻ từ biểu thức giải cấu trúc Tuple (ví dụ: "(a, b)").
     * 
     * @param parent Token chứa chuỗi giải cấu trúc dạng "(a, b)"
     * @return Danh sách các Token định danh riêng lẻ của từng biến
     */
    private List<Token> extractDestructuredNames(Token parent) {
        return getTokens(parent);
    }

    /**
     * Thực hiện khai báo biến trong tầm vực. Hỗ trợ khai báo giải cấu trúc.
     * 
     * @param name Token tên biến hoặc chuỗi giải cấu trúc
     * @param keyword Token từ khóa khai báo
     */
    private void declare(Token name, Token keyword) {
        if (name.lexeme().startsWith("(") && name.lexeme().endsWith(")")) {
            List<Token> names = extractDestructuredNames(name);
            for (var n : names) {
                declareSingle(n, keyword);
            }
        } else {
            declareSingle(name, keyword);
        }
    }

    /**
     * Khai báo một biến đơn lẻ trong tầm vực hiện tại hoặc toàn cục.
     * 
     * @param name Token tên biến đơn
     * @param keyword Token từ khóa khai báo
     */
    private void declareSingle(Token name, Token keyword) {
        if (scopes.isEmpty()) {
            globals.put(name.lexeme(), new VariableState(name, keyword, false));
            return;
        }

        Map<String, VariableState> scope = scopes.peek();
        if (scope.containsKey(name.lexeme())) {
            error(name, "Đã có biến tên '" + name.lexeme() + "' được khai báo trong tầm vực này.");
        }

        scope.put(name.lexeme(), new VariableState(name, keyword, false));
    }

    /**
     * Định nghĩa biến trong tầm vực (đánh dấu sẵn sàng). Hỗ trợ giải cấu trúc.
     * 
     * @param name Token tên biến hoặc chuỗi giải cấu trúc
     */
    private void define(Token name) {
        if (name.lexeme().startsWith("(") && name.lexeme().endsWith(")")) {
            List<Token> names = extractDestructuredNames(name);
            for (var n : names) {
                defineSingle(n);
            }
        } else {
            defineSingle(name);
        }
    }

    /**
     * Định nghĩa một biến đơn lẻ (đánh dấu đã khởi tạo xong và sẵn sàng sử dụng).
     * 
     * @param name Token tên biến đơn
     */
    private void defineSingle(Token name) {
        if (scopes.isEmpty()) {
            var state = globals.get(name.lexeme());
            if (state != null) {
                state.isReady = true;
            }
            return;
        }
        var state = scopes.peek().get(name.lexeme());
        if (state != null) {
            state.isReady = true;
        }
    }

    /**
     * Giải quyết tầm vực cho một biến cục bộ hoặc toàn cầu.
     * Tìm kiếm biến từ trong ra ngoài qua các scope cục bộ, nếu tìm thấy sẽ thông báo khoảng cách cho Interpreter.
     * 
     * @param expr Biểu thức biến cần giải quyết
     * @param name Token tên biến
     */
    private void resolveLocal(Expr expr, Token name) {
        for (var i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
        }
        // Nếu không tìm thấy trong scope cục bộ, mặc định là scope toàn cầu (Global)
    }

    /**
     * Giải quyết tầm vực cho một hàm (bao gồm tạo scope mới cho các tham số và phân tích thân hàm).
     *
     * @param function Hàm cần giải quyết
     */
    private void resolveFunction(Stmt.Function function) {
        var enclosingFunction = currentFunction;
        currentFunction = FunctionType.FUNCTION;

        beginScope();
        for (Stmt.Parameter param : function.parameters) {
            declare(param.name, param.type);
            define(param.name);
        }
        for (var stmt : function.body) {
            resolve(stmt);
        }
        endScope();

        currentFunction = enclosingFunction;
    }

    /**
     * Kiểm tra xem một từ khóa khai báo biến có đại diện cho biến khả biến hay không.
     * 
     * @param keyword Token từ khóa cần kiểm tra
     * @return {@code true} nếu biến khả biến, ngược lại {@code false}
     */
    private boolean isMutable(Token keyword) {
        if (keyword == null) return false;
        var lexeme = keyword.lexeme();
        return lexeme.equals("mut") || lexeme.equals("khả_biến");
    }

    /**
     * Kiểm tra xem một từ khóa khai báo biến có đại diện cho hằng số hay không.
     * 
     * @param keyword Token từ khóa cần kiểm tra
     * @return {@code true} nếu là hằng số, ngược lại {@code false}
     */
    private boolean isConst(Token keyword) {
        if (keyword == null) return false;
        var lexeme = keyword.lexeme();
        return lexeme.equals("const") || lexeme.equals("hằng_số");
    }

    /**
     * Tìm kiếm thông tin khai báo của một biến trong các tầm vực cục bộ và toàn cục.
     * 
     * @param name Token tên biến
     * @return VariableState của biến nếu tìm thấy, ngược lại {@code null}
     */
    private VariableState lookupVariableState(Token name) {
        for (var i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name.lexeme())) {
                return scopes.get(i).get(name.lexeme());
            }
        }
        return globals.get(name.lexeme());
    }

    // --- CÁC PHƯƠNG THỨC DUYỆT CÂU LỆNH (Stmt.Visitor) ---

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        declare(stmt.name, stmt.name);
        define(stmt.name);
        resolveFunction(stmt);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name, stmt.keyword);
        if (stmt.initializer != null) {
            resolve(stmt.initializer);
        } else {
            // Nếu là hằng số thì bắt buộc phải có giá trị khởi tạo
            if (isConst(stmt.keyword)) {
                error(stmt.name, "Hằng số '" + stmt.name.lexeme() + "' bắt buộc phải được gán giá trị khởi tạo khi khai báo.");
            }
        }
        define(stmt.name);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        if (currentFunction == FunctionType.NONE) {
            error(stmt.keyword, "Không thể sử dụng câu lệnh 'trả_về' / 'return' ở ngoài thân hàm.");
        }
        if (stmt.value != null) {
            resolve(stmt.value);
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);

        var wasInLoop = isInLoop;
        isInLoop = true;
        resolve(stmt.body);
        isInLoop = wasInLoop;
        
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        if (stmt.isForEach) {
            resolve(stmt.end); // Danh sách duyệt
        } else {
            resolve(stmt.start);
            resolve(stmt.end);
        }

        beginScope();
        declare(stmt.name, stmt.name); // Biến lặp là biến bất biến cục bộ trong thân loop
        define(stmt.name);

        var wasInLoop = isInLoop;
        isInLoop = true;
        resolve(stmt.body);
        isInLoop = wasInLoop;

        endScope();
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        if (!isInLoop) {
            error(stmt.keyword, "Không thể sử dụng câu lệnh 'dừng' / 'break' ở ngoài vòng lặp.");
        }
        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        if (!isInLoop) {
            error(stmt.keyword, "Không thể sử dụng câu lệnh 'tiếp' / 'continue' ở ngoài vòng lặp.");
        }
        return null;
    }

    @Override
    public Void visitSwitchStmt(Stmt.Switch stmt) {
        resolve(stmt.value);
        for (Stmt.SwitchCase sc : stmt.cases) {
            for (var pattern : sc.patterns) {
                resolve(pattern);
            }
            resolve(sc.body);
        }
        return null;
    }

    // --- CÁC PHƯƠNG THỨC DUYỆT BIỂU THỨC (Expr.Visitor) ---

    @Override
    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);

        // Kiểm tra tĩnh tính khả biến (Mutability)
        var state = lookupVariableState(expr.name);
        if (state != null) {
            if (isConst(state.keyword)) {
                error(expr.name, "Hằng số '" + expr.name.lexeme() + "' không thể bị thay đổi giá trị.");
            } else if (!isMutable(state.keyword)) {
                error(expr.name, "Biến bất biến '" + expr.name.lexeme() + "' không được phép gán lại giá trị (yêu cầu từ khóa 'khả_biến' / 'mut').");
            }
        }
        return null;
    }

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expression);
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    @Override
    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitVariableExpr(Expr.Variable expr) {
        if (!scopes.isEmpty()) {
            var state = scopes.peek().get(expr.name.lexeme());
            if (state != null && !state.isReady) {
                error(expr.name, "Không thể đọc biến cục bộ '" + expr.name.lexeme() + "' trong biểu thức khởi tạo của chính nó.");
            }
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    @Override
    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);
        for (var argument : expr.arguments) {
            resolve(argument);
        }
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    @Override
    public Void visitGetExpr(Expr.Get expr) {
        resolve(expr.object);
        return null;
    }

    @Override
    public Void visitStmtExpr(Expr.StmtExpr expr) {
        resolve(expr.statement);
        return null;
    }

    /**
     * Giải quyết tầm vực cho biểu thức Tuple.
     * 
     * @param expr Biểu thức Tuple cần giải quyết
     * @return {@code null}
     */
    @Override
    public Void visitTupleExpr(Expr.Tuple expr) {
        for (var expression : expr.expressions) {
            resolve(expression);
        }
        return null;
    }
}
