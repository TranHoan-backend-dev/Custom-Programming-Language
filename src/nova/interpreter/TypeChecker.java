package nova.interpreter;

import nova.ast.Expr;
import nova.ast.Stmt;
import nova.lexer.Token;
import nova.lexer.TokenType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Lớp TypeChecker thực hiện phân tích kiểu tĩnh (Static Type Checking) và kiểm tra an toàn Null (Null Safety)
 * trên cây cú pháp trừu tượng (AST) của ngôn ngữ Nova trước khi chương trình được chạy thực sự.
 * 
 * @author XUAN HOAN
 */
public class TypeChecker implements Expr.Visitor<String>, Stmt.Visitor<Void> {
    private final Interpreter interpreter;
    private final Stack<Map<String, String>> scopes = new Stack<>();
    private final Map<String, String> globals = new HashMap<>();
    private final List<SemanticError> errors = new ArrayList<>();
    
    /** Kiểu trả về của hàm hiện tại đang phân tích (dùng để kiểm tra câu lệnh trả_về). */
    private String currentReturnType = null;

    /**
     * Khởi tạo trình kiểm tra kiểu liên kết với Interpreter chỉ định.
     * Đăng ký các hàm dựng sẵn hoặc môi trường cơ sở nếu cần.
     * 
     * @param interpreter Trình thông dịch chứa thông tin tầm vực tĩnh
     */
    public TypeChecker(Interpreter interpreter) {
        this.interpreter = interpreter;
        // Đăng ký các hàm in tiêu chuẩn vào global
        globals.put("print", "func(any) -> void");
        globals.put("println", "func(any) -> void");
        globals.put("in", "func(any) -> void");
        globals.put("in_dòng_mới", "func(any) -> void");
        globals.put("printf", "func(any) -> void");
        globals.put("in_định_dạng", "func(any) -> void");
    }

    /**
     * Đăng ký kiểu dữ liệu của một biến toàn cục (phục vụ cho kiểm thử).
     * 
     * @param name Tên biến toàn cục
     * @param type Kiểu dữ liệu tương ứng
     */
    public void defineGlobalType(String name, String type) {
        globals.put(name, normalizeType(type));
    }

    /**
     * Thực hiện kiểm tra kiểu trên danh sách các câu lệnh AST.
     * 
     * @param statements Danh sách câu lệnh cần kiểm tra
     * @return Danh sách các lỗi ngữ nghĩa/kiểu phát hiện được
     */
    public List<SemanticError> check(List<Stmt> statements) {
        // Pha 1: Đăng ký chữ ký hàm toàn cục trước để hỗ trợ gọi đệ quy hoặc gọi chéo
        for (Stmt stmt : statements) {
            if (stmt instanceof Stmt.Function) {
                Stmt.Function func = (Stmt.Function) stmt;
                globals.put(func.name.lexeme(), getFunctionSignature(func));
            }
        }
        
        // Pha 2: Duyệt kiểm tra kiểu chi tiết từng câu lệnh
        for (Stmt statement : statements) {
            try {
                resolve(statement);
            } catch (TypeException e) {
                // Đã thu thập lỗi trong danh sách errors, bỏ qua ngoại lệ cục bộ để phân tích tiếp
            }
        }
        return errors;
    }

    /**
     * Kiểm tra xem có lỗi kiểu nào được phát hiện hay không.
     * 
     * @return {@code true} nếu có lỗi, ngược lại {@code false}
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Báo lỗi kiểu tại một Token cụ thể.
     * 
     * @param token Token gây lỗi
     * @param message Thông điệp mô tả lỗi kiểu dữ liệu
     */
    private void error(Token token, String message) {
        errors.add(new SemanticError(token, message));
    }

    /**
     * Thực thi kiểm tra kiểu cho một câu lệnh.
     * 
     * @param stmt Câu lệnh cần kiểm tra
     */
    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    /**
     * Đánh giá kiểu dữ liệu của một biểu thức và trả về tên kiểu dạng chuỗi đã chuẩn hóa.
     * 
     * @param expr Biểu thức cần đánh giá kiểu
     * @return Chuỗi kiểu dữ liệu (ví dụ: "int", "string", "double")
     */
    private String resolve(Expr expr) {
        String type = expr.accept(this);
        return normalizeType(type);
    }

    /**
     * Tạo một tầm vực cục bộ mới.
     */
    private void beginScope() {
        scopes.push(new HashMap<>());
    }

    /**
     * Hủy bỏ tầm vực cục bộ hiện tại.
     */
    private void endScope() {
        scopes.pop();
    }

    /**
     * Đăng ký kiểu dữ liệu của một biến trong scope hiện tại.
     * 
     * @param name Tên biến
     * @param type Kiểu dữ liệu của biến
     */
    private void define(String name, String type) {
        if (!scopes.isEmpty()) {
            scopes.peek().put(name, normalizeType(type));
        } else {
            globals.put(name, normalizeType(type));
        }
    }

    /**
     * Lấy kiểu dữ liệu của biến tĩnh thông qua khoảng cách tầm vực (distance) hoặc từ global.
     * 
     * @param name Tên biến cần truy vấn kiểu
     * @param expr Biểu thức biến chứa thông tin distance
     * @return Kiểu dữ liệu của biến
     */
    private String getVariableType(String name, Expr expr) {
        int distance = interpreter.getDistance(expr);
        if (distance >= 0) {
            return scopes.get(scopes.size() - 1 - distance).get(name);
        }
        return globals.get(name);
    }

    /**
     * Lấy kiểu dữ liệu của một biến trong scope cục bộ gần nhất bằng tên (dùng cho Smart Casting).
     * 
     * @param name Tên biến
     * @return Kiểu dữ liệu tương ứng, hoặc {@code null} nếu không tìm thấy
     */
    private String getVariableType(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return scopes.get(i).get(name);
            }
        }
        return globals.get(name);
    }

    /**
     * Tạm thời thiết lập lại kiểu dữ liệu của một biến (dùng cho Smart Casting).
     * 
     * @param name Tên biến
     * @param type Kiểu mới
     */
    private void setVariableType(String name, String type) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                scopes.get(i).put(name, normalizeType(type));
                return;
            }
        }
        globals.put(name, normalizeType(type));
    }

    /**
     * Chuẩn hóa kiểu dữ liệu từ tiếng Việt sang tiếng Anh và chuẩn hóa các khoảng trắng.
     * 
     * @param type Chuỗi kiểu dữ liệu thô
     * @return Kiểu dữ liệu chuẩn hóa dạng tiếng Anh
     */
    private String normalizeType(String type) {
        if (type == null) return "void";
        type = type.trim();
        if (type.equals("số_nguyên") || type.equals("số_nguyên_32")) return "int";
        if (type.equals("số_nguyên_16")) return "int16";
        if (type.equals("số_nguyên_64")) return "int64";
        if (type.equals("số_thực_kép")) return "double";
        if (type.equals("số_thực_đơn")) return "float";
        if (type.equals("ký_tự")) return "char";
        if (type.equals("chuỗi")) return "string";
        if (type.equals("logic")) return "boolean";
        if (type.equals("trống")) return "void";
        if (type.equals("k_tồn_tại")) return "null";

        if (type.endsWith("?")) {
            String base = type.substring(0, type.length() - 1);
            return normalizeType(base) + "?";
        }

        if (type.startsWith("func(") || type.startsWith("function(") || type.startsWith("hàm(")) {
            int startParen = type.indexOf('(');
            int endParen = -1;
            int depth = 0;
            for (int i = startParen; i < type.length(); i++) {
                char c = type.charAt(i);
                if (c == '(') depth++;
                else if (c == ')') {
                    depth--;
                    if (depth == 0) {
                        endParen = i;
                        break;
                    }
                }
            }
            if (endParen != -1) {
                String paramsStr = type.substring(startParen + 1, endParen).trim();
                List<String> params = new ArrayList<>();
                if (!paramsStr.isEmpty()) {
                    int pDepth = 0;
                    int start = 0;
                    for (int i = 0; i < paramsStr.length(); i++) {
                        char c = paramsStr.charAt(i);
                        if (c == '<' || c == '(') pDepth++;
                        else if (c == '>' || c == ')') pDepth--;
                        else if (c == ',' && pDepth == 0) {
                            params.add(paramsStr.substring(start, i).trim());
                            start = i + 1;
                        }
                    }
                    params.add(paramsStr.substring(start).trim());
                }
                
                int arrowIdx = type.indexOf("->", endParen);
                if (arrowIdx != -1) {
                    String returnStr = type.substring(arrowIdx + 2).trim();
                    StringBuilder sb = new StringBuilder("func(");
                    for (int i = 0; i < params.size(); i++) {
                        sb.append(normalizeType(params.get(i)));
                        if (i < params.size() - 1) sb.append(", ");
                    }
                    sb.append(") -> ");
                    sb.append(normalizeType(returnStr));
                    return sb.toString();
                }
            }
        }

        if (type.contains("<") && type.endsWith(">")) {
            int idx = type.indexOf('<');
            String genericName = type.substring(0, idx).trim();
            String inner = type.substring(idx + 1, type.length() - 1).trim();
            return genericName + "<" + normalizeType(inner) + ">";
        }

        if (type.startsWith("(") && type.endsWith(")")) {
            String content = type.substring(1, type.length() - 1).trim();
            if (content.isEmpty()) return "()";
            int depth = 0;
            int start = 0;
            List<String> parts = new ArrayList<>();
            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);
                if (c == '<' || c == '(') depth++;
                else if (c == '>' || c == ')') depth--;
                else if (c == ',' && depth == 0) {
                    parts.add(content.substring(start, i).trim());
                    start = i + 1;
                }
            }
            parts.add(content.substring(start).trim());

            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < parts.size(); i++) {
                String part = parts.get(i);
                int lastSpace = part.lastIndexOf(' ');
                if (lastSpace != -1) {
                    String t = part.substring(0, lastSpace).trim();
                    String name = part.substring(lastSpace).trim();
                    sb.append(normalizeType(t)).append(" ").append(name);
                } else {
                    sb.append(normalizeType(part));
                }
                if (i < parts.size() - 1) sb.append(", ");
            }
            sb.append(")");
            return sb.toString();
        }

        return type;
    }

    /**
     * Kiểm tra xem kiểu nguồn (source) có tương thích và gán được cho kiểu đích (target) hay không.
     * Hỗ trợ Null Safety và ép kiểu tự động số nguyên thành số thực.
     * 
     * @param target Kiểu dữ liệu đích mong muốn
     * @param source Kiểu dữ liệu nguồn truyền vào
     * @return {@code true} nếu tương thích, ngược lại {@code false}
     */
    private boolean isCompatible(String target, String source) {
        target = normalizeType(target);
        source = normalizeType(source);

        if (target.equals(source)) return true;
        if (target.startsWith("(") && target.endsWith(")") && source.startsWith("(") && source.endsWith(")")) {
            List<String> targetElements = splitTupleTypes(target);
            List<String> sourceElements = splitTupleTypes(source);
            if (targetElements.size() != sourceElements.size()) return false;
            for (int i = 0; i < targetElements.size(); i++) {
                if (!isCompatible(targetElements.get(i), sourceElements.get(i))) {
                    return false;
                }
            }
            return true;
        }
        if (target.equals("any") || source.equals("any")) return true;

        if (source.equals("null")) {
            return target.endsWith("?");
        }

        if (target.endsWith("?")) {
            String baseTarget = target.substring(0, target.length() - 1);
            if (source.endsWith("?")) {
                String baseSource = source.substring(0, source.length() - 1);
                return isCompatible(baseTarget, baseSource);
            } else {
                return isCompatible(baseTarget, source);
            }
        }

        if (source.endsWith("?")) {
            return false;
        }

        // Ép kiểu số học tự động (Numeric coercion/widening)
        if (target.equals("double")) {
            return source.equals("int") || source.equals("int16") || source.equals("int32") || source.equals("int64") || source.equals("float");
        }
        if (target.equals("float")) {
            return source.equals("int") || source.equals("int16") || source.equals("int32");
        }
        if (target.equals("int64")) {
            return source.equals("int") || source.equals("int16") || source.equals("int32");
        }
        if (target.equals("int") || target.equals("int32")) {
            return source.equals("int16");
        }

        return false;
    }

    /**
     * Trích xuất chữ ký kiểu của một hàm Stmt.Function.
     */
    private String getFunctionSignature(Stmt.Function function) {
        StringBuilder sb = new StringBuilder("func(");
        for (int i = 0; i < function.parameters.size(); i++) {
            sb.append(normalizeType(function.parameters.get(i).type.lexeme()));
            if (i < function.parameters.size() - 1) sb.append(", ");
        }
        sb.append(") -> ");
        sb.append(normalizeType(function.returnType != null ? function.returnType.lexeme() : "void"));
        return sb.toString();
    }

    /**
     * Trích xuất danh sách các kiểu tham số từ chuỗi chữ ký hàm.
     */
    private List<String> getParameterTypes(String signature) {
        List<String> types = new ArrayList<>();
        int startParen = signature.indexOf('(');
        int endParen = signature.indexOf(')');
        if (startParen == -1 || endParen == -1) return types;
        
        String paramsContent = signature.substring(startParen + 1, endParen).trim();
        if (paramsContent.isEmpty()) return types;
        
        int depth = 0;
        int start = 0;
        for (int i = 0; i < paramsContent.length(); i++) {
            char c = paramsContent.charAt(i);
            if (c == '<' || c == '(') depth++;
            else if (c == '>' || c == ')') depth--;
            else if (c == ',' && depth == 0) {
                types.add(paramsContent.substring(start, i).trim());
                start = i + 1;
            }
        }
        types.add(paramsContent.substring(start).trim());
        return types;
    }

    /**
     * Trích xuất kiểu trả về từ chuỗi chữ ký hàm.
     */
    private String getReturnType(String signature) {
        int arrow = signature.indexOf("->");
        if (arrow == -1) return "void";
        return signature.substring(arrow + 2).trim();
    }

    /**
     * Trích xuất danh sách các kiểu từ chuỗi kiểu Tuple.
     */
    private List<String> splitTupleTypes(String tupleStr) {
        List<String> types = new ArrayList<>();
        if (!tupleStr.startsWith("(") || !tupleStr.endsWith(")")) {
            types.add(tupleStr);
            return types;
        }
        String content = tupleStr.substring(1, tupleStr.length() - 1).trim();
        if (content.isEmpty()) return types;

        int depth = 0;
        int start = 0;
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '<' || c == '(') depth++;
            else if (c == '>' || c == ')') depth--;
            else if (c == ',' && depth == 0) {
                parts.add(content.substring(start, i).trim());
                start = i + 1;
            }
        }
        parts.add(content.substring(start).trim());

        for (String part : parts) {
            int lastSpace = part.lastIndexOf(' ');
            if (lastSpace != -1) {
                types.add(part.substring(0, lastSpace).trim());
            } else {
                types.add(part);
            }
        }
        return types;
    }

    /**
     * Trích xuất các tên biến riêng lẻ từ biểu thức giải cấu trúc Tuple (ví dụ: "(a, b)").
     * 
     * @param parent Token chứa chuỗi giải cấu trúc dạng "(a, b)"
     * @return Danh sách các Token định danh riêng lẻ của từng biến
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
     * Phân tích điều kiện If để lấy biến đang được so sánh khác null.
     */
    private String getNonNullCheckedVariable(Expr expr) {
        if (expr instanceof Expr.Binary) {
            Expr.Binary binary = (Expr.Binary) expr;
            String op = binary.operator.lexeme();
            if (op.equals("!=") || op.equals("khác") || op.equals("is_not")) {
                if (binary.left instanceof Expr.Variable && isNullLiteral(binary.right)) {
                    return ((Expr.Variable) binary.left).name.lexeme();
                }
                if (binary.right instanceof Expr.Variable && isNullLiteral(binary.left)) {
                    return ((Expr.Variable) binary.right).name.lexeme();
                }
            }
        }
        if (expr instanceof Expr.Grouping) {
            return getNonNullCheckedVariable(((Expr.Grouping) expr).expression);
        }
        return null;
    }

    private boolean isNullLiteral(Expr expr) {
        if (expr instanceof Expr.Literal) {
            return ((Expr.Literal) expr).value == null;
        }
        return false;
    }

    private Token getConditionToken(Expr expr) {
        if (expr instanceof Expr.Binary) return ((Expr.Binary) expr).operator;
        if (expr instanceof Expr.Variable) return ((Expr.Variable) expr).name;
        return new Token(nova.lexer.TokenType.IDENTIFIER, "điều kiện");
    }

    // --- CÁC PHƯƠNG THỨC DUYỆT CÂU LỆNH (Stmt.Visitor) ---

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        for (Stmt s : stmt.statements) {
            resolve(s);
        }
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
        String sig = getFunctionSignature(stmt);
        define(stmt.name.lexeme(), sig);
        
        String previousReturnType = currentReturnType;
        currentReturnType = normalizeType(stmt.returnType != null ? stmt.returnType.lexeme() : "void");
        
        beginScope();
        for (Stmt.Parameter param : stmt.parameters) {
            define(param.name.lexeme(), param.type.lexeme());
        }
        for (Stmt bodyStmt : stmt.body) {
            resolve(bodyStmt);
        }
        endScope();
        
        currentReturnType = previousReturnType;
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        String condType = resolve(stmt.condition);
        if (!isCompatible("boolean", condType)) {
            error(getConditionToken(stmt.condition), "Điều kiện của câu lệnh 'nếu' phải là kiểu logic (boolean).");
        }

        String nonNullVar = getNonNullCheckedVariable(stmt.condition);
        String originalType = null;
        if (nonNullVar != null) {
            originalType = getVariableType(nonNullVar);
            if (originalType != null && originalType.endsWith("?")) {
                setVariableType(nonNullVar, originalType.substring(0, originalType.length() - 1));
            } else {
                nonNullVar = null;
            }
        }

        resolve(stmt.thenBranch);

        if (nonNullVar != null) {
            setVariableType(nonNullVar, originalType);
        }

        if (stmt.elseBranch != null) {
            resolve(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        if (stmt.name.lexeme().startsWith("(") && stmt.name.lexeme().endsWith(")")) {
            if (stmt.initializer == null) {
                error(stmt.name, "Khai báo giải cấu trúc bắt buộc phải được gán giá trị khởi tạo.");
                return null;
            }
            String initType = resolve(stmt.initializer);
            if (!initType.startsWith("(") || !initType.endsWith(")")) {
                error(stmt.name, "Biểu thức khởi tạo cho khai báo giải cấu trúc phải là kiểu Tuple, nhận được kiểu '" + initType + "'.");
                return null;
            }
            List<Token> names = extractDestructuredNames(stmt.name);
            List<String> innerTypes = splitTupleTypes(initType);
            if (names.size() != innerTypes.size()) {
                error(stmt.name, "Số lượng biến giải cấu trúc (" + names.size() + ") không khớp với số lượng phần tử trong Tuple (" + innerTypes.size() + ").");
                return null;
            }
            for (int i = 0; i < names.size(); i++) {
                Token varName = names.get(i);
                String varType = innerTypes.get(i);
                
                // Nếu tên biến kết thúc bằng dấu '?', và kiểu tự suy luận không nullable, biến nó thành nullable
                if (varName.lexeme().endsWith("?") && !varType.endsWith("?")) {
                    varType = varType + "?";
                }
                define(varName.lexeme(), varType);
            }
            return null;
        }

        String initType = "void";
        if (stmt.initializer != null) {
            initType = resolve(stmt.initializer);
        }

        String declaredType = null;
        if (stmt.type != null) {
            declaredType = normalizeType(stmt.type.lexeme());
            if (stmt.initializer != null && !isCompatible(declaredType, initType)) {
                error(stmt.name, "Kiểu dữ liệu gán không tương thích. Khai báo kiểu '" + declaredType + "' nhưng nhận kiểu '" + initType + "'.");
            }
        } else {
            // Tự suy luận kiểu (Type Inference)
            if (stmt.initializer == null) {
                error(stmt.name, "Không thể tự suy luận kiểu dữ liệu khi thiếu biểu thức khởi tạo.");
                declaredType = "any";
            } else {
                declaredType = initType;
                // Nếu tên biến kết thúc bằng dấu '?', và kiểu tự suy luận không nullable, biến nó thành nullable
                if (stmt.name.lexeme().endsWith("?") && !declaredType.endsWith("?")) {
                    declaredType = declaredType + "?";
                }
            }
        }

        // Kiểm tra Null Safety khi khai báo không nullable nhưng gán null
        if (!declaredType.endsWith("?") && initType.equals("null")) {
            error(stmt.name, "Không thể gán giá trị rỗng (null) cho biến phi-nullable '" + stmt.name.lexeme() + "'.");
        }

        define(stmt.name.lexeme(), declaredType);
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        String valType = "void";
        if (stmt.value != null) {
            valType = resolve(stmt.value);
        }
        
        if (currentReturnType == null) {
            error(stmt.keyword, "Không thể sử dụng câu lệnh 'trả_về' ở ngoài thân hàm.");
            return null;
        }

        if (!isCompatible(currentReturnType, valType)) {
            error(stmt.keyword, "Kiểu trả về không khớp với chữ ký hàm. Chữ ký yêu cầu '" + currentReturnType + "' nhưng trả về '" + valType + "'.");
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        String condType = resolve(stmt.condition);
        if (!isCompatible("boolean", condType)) {
            error(getConditionToken(stmt.condition), "Điều kiện vòng lặp 'lặp' phải là kiểu logic (boolean).");
        }
        resolve(stmt.body);
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        if (stmt.isForEach) {
            String colType = resolve(stmt.end); // Kiểu của collection
            String itemType = "any";
            if (colType.startsWith("DanhSách<") && colType.endsWith(">")) {
                itemType = colType.substring(9, colType.length() - 1);
            } else if (colType.startsWith("List<") && colType.endsWith(">")) {
                itemType = colType.substring(5, colType.length() - 1);
            } else {
                error(stmt.name, "Vòng lặp duyệt phần tử yêu cầu một DanhSách (List), nhận được kiểu '" + colType + "'.");
            }
            beginScope();
            define(stmt.name.lexeme(), itemType);
            resolve(stmt.body);
            endScope();
        } else {
            String startType = resolve(stmt.start);
            String endType = resolve(stmt.end);
            if (!isCompatible("int", startType) || !isCompatible("int", endType)) {
                error(stmt.name, "Khoảng lặp duyệt số yêu cầu các giới hạn phải là kiểu số nguyên (int).");
            }
            beginScope();
            define(stmt.name.lexeme(), "int");
            resolve(stmt.body);
            endScope();
        }
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        return null;
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        return null;
    }

    /**
     * Thực hiện kiểm tra kiểu cho câu lệnh lựa chọn (switch statement).
     * Phân tích kiểu dữ liệu của biểu thức điều kiện đầu vào và so sánh với kiểu dữ liệu của từng mẫu trường hợp.
     * Báo lỗi nếu kiểu dữ liệu của mẫu trường hợp không tương thích với kiểu dữ liệu của biểu thức điều kiện,
     * ngoại trừ các mẫu mặc định hoặc wildcard (mẫu '_' hoặc 'sai' cho kiểu phi-logic).
     *
     * @param stmt Câu lệnh switch cần kiểm tra kiểu dữ liệu
     * @return {@code null}
     */
    @Override
    public Void visitSwitchStmt(Stmt.Switch stmt) {
        String valType = resolve(stmt.value);
        for (Stmt.SwitchCase sc : stmt.cases) {
            for (Expr pattern : sc.patterns) {
                String patType = resolve(pattern);
                if (isDefaultPattern(pattern, valType)) {
                    continue;
                }
                if (!isCompatible(valType, patType)) {
                    error(stmt.value instanceof Expr.Variable ? ((Expr.Variable) stmt.value).name : new Token(nova.lexer.TokenType.IDENTIFIER, "switch"),
                        "Kiểu của mẫu trường hợp '" + patType + "' không khớp với biểu thức switch '" + valType + "'.");
                }
            }
            resolve(sc.body);
        }
        return null;
    }

    /**
     * Kiểm tra xem một biểu thức mẫu trong câu lệnh switch có phải là mẫu mặc định (wildcard) hay không.
     * Mẫu mặc định bao gồm mẫu tự do '_' hoặc mẫu logic 'sai' / 'false' khi biểu thức switch không phải kiểu logic.
     * 
     * @param pattern Biểu thức mẫu cần kiểm tra
     * @param valType Kiểu dữ liệu của biểu thức switch
     * @return {@code true} nếu là mẫu mặc định, ngược lại {@code false}
     */
    private boolean isDefaultPattern(Expr pattern, String valType) {
        if (pattern instanceof Expr.Variable) {
            Expr.Variable varExpr = (Expr.Variable) pattern;
            if (varExpr.name.lexeme().equals("_")) {
                return true;
            }
        }
        if (pattern instanceof Expr.Literal) {
            Expr.Literal litExpr = (Expr.Literal) pattern;
            if (Boolean.FALSE.equals(litExpr.value) && !valType.equals("boolean")) {
                return true;
            }
        }
        return false;
    }

    // --- CÁC PHƯƠNG THỨC DUYỆT BIỂU THỨC (Expr.Visitor) ---

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        String varType = getVariableType(expr.name.lexeme(), expr);
        if (varType == null) {
            error(expr.name, "Biến '" + expr.name.lexeme() + "' chưa được khai báo.");
            return "any";
        }
        String valType = resolve(expr.value);
        if (!isCompatible(varType, valType)) {
            error(expr.name, "Không thể gán giá trị kiểu '" + valType + "' cho biến '" + expr.name.lexeme() + "' kiểu '" + varType + "'.");
        }
        if (!varType.endsWith("?") && valType.equals("null")) {
            error(expr.name, "Không thể gán giá trị rỗng (null) cho biến phi-nullable '" + expr.name.lexeme() + "'.");
        }
        return varType;
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        String leftType = resolve(expr.left);
        String rightType = resolve(expr.right);
        String op = expr.operator.lexeme();

        // 1. Phép nối chuỗi
        if (op.equals("+") && (leftType.equals("string") || rightType.equals("string"))) {
            return "string";
        }

        // 2. Phép toán số học cơ bản
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/") || op.equals("%")) {
            boolean leftNum = isCompatible("double", leftType);
            boolean rightNum = isCompatible("double", rightType);
            if (!leftNum || !rightNum) {
                error(expr.operator, "Phép toán số học yêu cầu hai toán hạng đều phải là kiểu số. Nhận được kiểu '" + leftType + "' và '" + rightType + "'.");
                return "any";
            }
            if (leftType.equals("double") || rightType.equals("double")) return "double";
            if (leftType.equals("float") || rightType.equals("float")) return "float";
            if (leftType.equals("int64") || rightType.equals("int64")) return "int64";
            return "int";
        }

        // 3. Toán tử so sánh logic số học
        if (op.equals("<") || op.equals(">") || op.equals("<=") || op.equals(">=")) {
            boolean leftNum = isCompatible("double", leftType) || leftType.equals("char");
            boolean rightNum = isCompatible("double", rightType) || rightType.equals("char");
            if (!leftNum || !rightNum) {
                error(expr.operator, "Toán tử so sánh '" + op + "' yêu cầu các toán hạng là kiểu số hoặc ký tự.");
            }
            return "boolean";
        }

        // 4. Toán tử so sánh đẳng trị
        if (op.equals("==") || op.equals("!=") || op.equals("khác") || op.equals("is_not")) {
            if (!isCompatible(leftType, rightType) && !isCompatible(rightType, leftType)) {
                error(expr.operator, "Không thể so sánh đẳng trị giữa hai kiểu không tương thích: '" + leftType + "' và '" + rightType + "'.");
            }
            return "boolean";
        }

        error(expr.operator, "Không hỗ trợ toán tử hai ngôi '" + op + "'.");
        return "any";
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return resolve(expr.expression);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        Object val = expr.value;
        if (val == null) return "null";
        if (val instanceof Integer) return "int";
        if (val instanceof Double) return "double";
        if (val instanceof Float) return "float";
        if (val instanceof Long) return "int64";
        if (val instanceof Boolean) return "boolean";
        if (val instanceof Character) return "char";
        if (val instanceof String) return "string";
        return "any";
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        String leftType = resolve(expr.left);
        String rightType = resolve(expr.right);
        if (!isCompatible("boolean", leftType) || !isCompatible("boolean", rightType)) {
            error(expr.operator, "Các toán tử logic 'và' / 'hoặc' yêu cầu các toán hạng phải là kiểu logic (boolean).");
        }
        return "boolean";
    }

    /**
     * Thực hiện kiểm tra kiểu cho biểu thức biến (Variable Expression).
     * Trả về kiểu dữ liệu của biến tương ứng nếu đã được khai báo, hoặc báo lỗi nếu biến chưa khai báo.
     * Biến đặc biệt đại diện cho wildcard (mẫu trùng khớp mọi giá trị '_') được bỏ qua kiểm tra khai báo và trả về kiểu 'any'.
     * 
     * @param expr Biểu thức biến cần kiểm tra kiểu
     * @return Kiểu dữ liệu tương ứng của biến hoặc 'any' nếu lỗi hoặc là biến wildcard
     */
    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        if (expr.name.lexeme().equals("_")) {
            return "any";
        }
        String varType = getVariableType(expr.name.lexeme(), expr);
        if (varType == null) {
            error(expr.name, "Biến '" + expr.name.lexeme() + "' chưa được khai báo.");
            return "any";
        }
        return varType;
    }

    /**
     * Thực hiện kiểm tra kiểu cho biểu thức gọi hàm (function call).
     * Xác minh đối tượng được gọi thực sự là một hàm, kiểm tra số lượng đối số truyền vào,
     * và tính tương thích kiểu dữ liệu giữa các đối số thực tế với kiểu tham số trong chữ ký hàm.
     * Đối với các hàm đặc biệt chấp nhận kiểu 'any', tất cả các đối số vẫn được duyệt qua
     * và phân tích kiểu để phát hiện lỗi bên trong chúng.
     * 
     * @param expr Biểu thức gọi hàm cần kiểm tra
     * @return Kiểu dữ liệu trả về của hàm được gọi, hoặc "any" nếu có lỗi xảy ra
     */
    @Override
    public String visitCallExpr(Expr.Call expr) {
        String calleeType = resolve(expr.callee);
        if (calleeType.startsWith("func(")) {
            List<String> paramTypes = getParameterTypes(calleeType);
            String returnType = getReturnType(calleeType);
            
            // Nếu là hàm nhận any làm đối số, bỏ qua kiểm tra số lượng và kiểu nhưng vẫn phân tích các đối số
            if (paramTypes.size() == 1 && paramTypes.get(0).equals("any")) {
                for (Expr argument : expr.arguments) {
                    resolve(argument);
                }
                return returnType;
            }

            if (expr.arguments.size() != paramTypes.size()) {
                error(expr.paren, "Sai số lượng tham số truyền vào hàm. Yêu cầu " + paramTypes.size() + " nhưng nhận " + expr.arguments.size() + ".");
                return returnType;
            }

            for (int i = 0; i < expr.arguments.size(); i++) {
                String argType = resolve(expr.arguments.get(i));
                if (!isCompatible(paramTypes.get(i), argType)) {
                    error(expr.paren, "Tham số thứ " + (i + 1) + " không khớp kiểu. Yêu cầu kiểu '" + paramTypes.get(i) + "' nhưng nhận kiểu '" + argType + "'.");
                }
            }
            return returnType;
        }

        error(expr.paren, "Đối tượng được gọi không phải là một hàm (nhận được kiểu '" + calleeType + "').");
        return "any";
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        String rightType = resolve(expr.right);
        String op = expr.operator.lexeme();
        if (op.equals("-")) {
            if (!isCompatible("double", rightType)) {
                error(expr.operator, "Toán tử đổi dấu '-' yêu cầu toán hạng phải là kiểu số.");
            }
            return rightType;
        }
        if (op.equals("!") || op.equals("không") || op.equals("not")) {
            if (!isCompatible("boolean", rightType)) {
                error(expr.operator, "Toán tử phủ định logic yêu cầu toán hạng phải là kiểu logic (boolean).");
            }
            return "boolean";
        }
        return "any";
    }

    @Override
    public String visitGetExpr(Expr.Get expr) {
        String objType = resolve(expr.object);
        
        // Chặn gọi thuộc tính trên kiểu nullable
        if (objType.endsWith("?")) {
            error(expr.name, "Không thể gọi thuộc tính hoặc phương thức '" + expr.name.lexeme() + "' trên đối tượng nullable '" + objType + "' khi chưa kiểm tra khác null.");
            return "any";
        }

        String propName = expr.name.lexeme();
        if (objType.equals("string")) {
            if (propName.equals("length")) {
                return "func() -> int";
            }
            if (propName.equals("toUpperCase") || propName.equals("to_string")) {
                return "func() -> string";
            }
        }

        if (propName.equals("to_string")) {
            if (objType.equals("int") || objType.equals("int16") || objType.equals("int32") || objType.equals("int64") ||
                objType.equals("double") || objType.equals("float") || objType.equals("char") || objType.equals("boolean")) {
                return "func() -> string";
            }
        }

        error(expr.name, "Không tìm thấy thuộc tính hoặc phương thức '" + expr.name.lexeme() + "' trên đối tượng kiểu '" + objType + "'.");
        return "any";
    }

    /**
     * Thực hiện kiểm tra kiểu cho biểu thức bao đóng câu lệnh (Statement Expression).
     * Giải quyết và kiểm tra kiểu của các câu lệnh nội bộ trong biểu thức, như câu lệnh rẽ nhánh 'nếu'
     * hoặc cấu trúc lựa chọn 'trường_hợp' khi được sử dụng như một biểu thức trả về giá trị.
     *
     * @param expr Biểu thức câu lệnh cần kiểm tra kiểu dữ liệu
     * @return Kiểu dữ liệu của biểu thức câu lệnh sau khi giải quyết
     */
    @Override
    public String visitStmtExpr(Expr.StmtExpr expr) {
        Stmt stmt = expr.statement;
        if (stmt instanceof Stmt.If) {
            Stmt.If ifStmt = (Stmt.If) stmt;
            resolve(ifStmt);
            String thenType = getStatementExpressionType(ifStmt.thenBranch);
            String elseType = ifStmt.elseBranch != null ? getStatementExpressionType(ifStmt.elseBranch) : "void";
            if (!isCompatible(thenType, elseType) && !isCompatible(elseType, thenType)) {
                error(getConditionToken(ifStmt.condition), "Kiểu dữ liệu trả về của hai nhánh trong biểu thức 'nếu' không tương thích ('" + thenType + "' và '" + elseType + "').");
            }
            return thenType;
        }
        if (stmt instanceof Stmt.Block) {
            Stmt.Block block = (Stmt.Block) stmt;
            resolve(block);
            if (block.statements.isEmpty()) return "void";
            Stmt last = block.statements.get(block.statements.size() - 1);
            return getStatementExpressionType(last);
        }
        if (stmt instanceof Stmt.Switch) {
            Stmt.Switch sw = (Stmt.Switch) stmt;
            resolve(sw);
            if (sw.cases.isEmpty()) return "void";
            String firstType = getStatementExpressionType(sw.cases.get(0).body);
            for (Stmt.SwitchCase sc : sw.cases) {
                String scType = getStatementExpressionType(sc.body);
                if (!isCompatible(firstType, scType) && !isCompatible(scType, firstType)) {
                    error(new Token(nova.lexer.TokenType.IDENTIFIER, "switch"), "Các nhánh của switch biểu thức trả về các kiểu không tương thích.");
                }
            }
            return firstType;
        }
        resolve(stmt);
        return "void";
    }

    /**
     * Suy đoán kiểu trả về của một câu lệnh khi nó được xem là biểu thức (ví dụ câu lệnh cuối cùng trong block).
     */
    private String getStatementExpressionType(Stmt stmt) {
        if (stmt instanceof Stmt.Expression) {
            return resolve(((Stmt.Expression) stmt).expression);
        }
        if (stmt instanceof Stmt.Block) {
            Stmt.Block block = (Stmt.Block) stmt;
            if (block.statements.isEmpty()) return "void";
            return getStatementExpressionType(block.statements.get(block.statements.size() - 1));
        }
        if (stmt instanceof Stmt.If) {
            Stmt.If ifStmt = (Stmt.If) stmt;
            return getStatementExpressionType(ifStmt.thenBranch);
        }
        return "void";
    }

    /**
     * Thực hiện kiểm tra kiểu cho biểu thức Tuple.
     * Đánh giá kiểu dữ liệu của từng biểu thức thành phần và định dạng kết quả dưới dạng chuỗi kiểu Tuple.
     * 
     * @param expr Biểu thức Tuple cần kiểm tra kiểu dữ liệu
     * @return Chuỗi kiểu dữ liệu của Tuple (ví dụ: "(int, string)")
     */
    @Override
    public String visitTupleExpr(Expr.Tuple expr) {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < expr.expressions.size(); i++) {
            sb.append(resolve(expr.expressions.get(i)));
            if (i < expr.expressions.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Ngoại lệ nội bộ dùng để ngắt luồng kiểm tra kiểu khi gặp lỗi nặng.
     */
    private static class TypeException extends RuntimeException {}
}
