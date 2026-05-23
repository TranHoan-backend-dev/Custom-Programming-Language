package nova.parser;

import nova.lexer.Token;
import nova.lexer.TokenType;
import nova.ast.Expr;
import nova.ast.Stmt;
import java.util.ArrayList;
import java.util.List;

/**
 * Bộ phân tích cú pháp (Parser) cho ngôn ngữ lập trình Nova.
 * Sử dụng phương pháp Recursive Descent (phân tích đi xuống đệ quy) để xây dựng cây cú pháp trừu tượng (AST) từ danh sách Token.
 * Hỗ trợ đồng bộ hóa lỗi cú pháp bằng cơ chế Panic Mode Recovery.
 * 
 * @author XUAN HOAN
 */
public class Parser {
    /** Luồng token đầu vào từ bộ Lexer */
    private final List<Token> tokens;
    /** Chỉ số token hiện tại đang xử lý */
    private int current = 0;
    /** Danh sách các lỗi cú pháp phát hiện được trong quá trình phân tích */
    private final List<ParseError> errors = new ArrayList<>();

    /**
     * Khởi tạo bộ phân tích cú pháp với danh sách các token đầu vào.
     * 
     * @param tokens Danh sách token nhận được từ Lexer
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Phương thức chính thực hiện phân tích cú pháp toàn bộ chương trình.
     * Hỗ trợ bắt lỗi dấu ngoặc đóng dư thừa ở cấp cao nhất để ngăn ngừa lặp vô hạn.
     * 
     * @return Danh sách các câu lệnh {@link Stmt} tương ứng với cây AST của chương trình
     */
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            try {
                if (check(TokenType.RIGHT_RACE)) {
                    Token brace = advance();
                    throw error(brace, "Dấu ngoặc nhọn đóng '}' không hợp lệ ngoài khối lệnh.");
                }
                Stmt decl = declaration();
                if (decl != null) {
                    statements.add(decl);
                }
            } catch (ParseError error) {
                errors.add(error);
                synchronize();
            }
        }
        return statements;
    }

    /**
     * Lấy danh sách các lỗi cú pháp thu thập được.
     * 
     * @return Danh sách lỗi cú pháp {@link ParseError}
     */
    public List<ParseError> getErrors() {
        return errors;
    }

    /**
     * Kiểm tra xem chương trình có lỗi cú pháp hay không.
     * 
     * @return {@code true} nếu có ít nhất một lỗi cú pháp, ngược lại là {@code false}
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    // =========================================================================
    // Các Quy tắc Phân tích Cú pháp (Grammar Rules)
    // =========================================================================

    /**
     * Phân tích một khai báo (hàm, biến/hằng hoặc câu lệnh).
     * <pre>
     * declaration → funcDecl | varDecl | statement ;
     * </pre>
     * 
     * @return Đối tượng {@link Stmt} tương ứng
     */
    private Stmt declaration() {
        while (match(TokenType.SEMICOLON)) {
            // Bỏ qua các dấu chấm phẩy trống (empty statement) dư thừa do Lexer tự chèn khi xuống dòng
        }
        if (isAtEnd() || check(TokenType.RIGHT_RACE)) return null;
        
        if (match(TokenType.FUNCTION)) return funcDecl();
        if (checkVarDecl()) return varDecl();
        return statement();
    }

    /**
     * Phân tích khai báo hàm.
     * <pre>
     * funcDecl → "hàm" IDENTIFIER "(" parameters? ")" "->" type block ;
     * </pre>
     * 
     * @return Đối tượng {@link Stmt.Function}
     */
    private Stmt funcDecl() {
        Token name = consume(TokenType.IDENTIFIER, "Yêu cầu tên hàm sau từ khóa 'hàm'.");
        consume(TokenType.LEFT_PAREN, "Yêu cầu dấu '(' sau tên hàm.");
        
        List<Stmt.Parameter> parameters = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                Token paramType = type();
                Token paramName = consume(TokenType.IDENTIFIER, "Yêu cầu tên tham số.");
                parameters.add(new Stmt.Parameter(paramName, paramType));
            } while (match(TokenType.COMMA));
        }
        consume(TokenType.RIGHT_PAREN, "Yêu cầu dấu ')' sau danh sách tham số.");
        
        consume(TokenType.ARROW, "Yêu cầu toán tử '->' để chỉ định kiểu trả về.");
        Token returnType = type();
        
        consume(TokenType.LEFT_RACE, "Yêu cầu dấu '{' trước thân hàm.");
        List<Stmt> body = block();
        
        return new Stmt.Function(name, parameters, returnType, body);
    }

    /**
     * Phân tích khai báo biến hoặc hằng số.
     * <pre>
     * varDecl → ( "biến" | "biến" "khả_biến" | "hằng_số" ) type? IDENTIFIER ( "=" expression )? ";" ;
     * </pre>
     * 
     * @return Đối tượng {@link Stmt.Var}
     */
    /**
     * Phân tích khai báo biến hoặc hằng số.
     * Hỗ trợ khai báo biến thông thường và khai báo giải cấu trúc Tuple (destructuring) dạng `biến (a, b) = biểu_thức;`.
     * 
     * @return Đối tượng {@link Stmt.Var} đại diện cho câu lệnh khai báo biến
     * @throws ParseError nếu phát hiện lỗi cú pháp trong quá trình phân tích
     */
    private Stmt varDecl() {
        Token keyword = advance(); // VAR hoặc CONST
        Token mutToken = null;
        
        if (keyword.type() == TokenType.VAR && match(TokenType.MUT)) {
            mutToken = previous();
        }
        
        Token type = null;
        Token name;
        
        if (check(TokenType.LEFT_PAREN)) {
            consume(TokenType.LEFT_PAREN, "Yêu cầu dấu '(' để bắt đầu khai báo giải cấu trúc (destructuring).");
            StringBuilder nameBuilder = new StringBuilder("(");
            boolean first = true;
            while (!check(TokenType.RIGHT_PAREN) && !isAtEnd()) {
                if (!first) {
                    consume(TokenType.COMMA, "Yêu cầu dấu ',' ngăn cách giữa các tên biến.");
                    nameBuilder.append(", ");
                }
                first = false;
                Token varName = consume(TokenType.IDENTIFIER, "Yêu cầu tên biến trong giải cấu trúc.");
                nameBuilder.append(varName.lexeme());
            }
            consume(TokenType.RIGHT_PAREN, "Yêu cầu dấu ')' để kết thúc khai báo giải cấu trúc.");
            nameBuilder.append(")");
            name = new Token(TokenType.IDENTIFIER, nameBuilder.toString());
        } else {
            if (isTypeSpecified()) {
                type = type();
                name = consume(TokenType.IDENTIFIER, "Yêu cầu tên biến sau kiểu dữ liệu.");
            } else {
                name = consume(TokenType.IDENTIFIER, "Yêu cầu tên biến.");
            }
        }
        
        Expr initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = expression();
        } else if (keyword.type() == TokenType.CONST) {
            throw error(keyword, "Hằng số bắt buộc phải được gán giá trị khởi tạo.");
        }
        
        consume(TokenType.SEMICOLON, "Yêu cầu dấu ';' sau khai báo biến.");
        
        Token finalKeyword = (mutToken != null) ? mutToken : keyword;
        return new Stmt.Var(finalKeyword, name, type, initializer);
    }

    /**
     * Phân tích một kiểu dữ liệu bao gồm cả cấu trúc Generic và Nullable.
     * <pre>
     * type → TYPE_KEYWORD "?"? ( "<" type ">" )? ;
     * </pre>
     * 
     * @return Token được ghép chuỗi kiểu đầy đủ để tương thích với AST hiện có
     */
    private Token type() {
        if (match(TokenType.LEFT_PAREN)) {
            StringBuilder builder = new StringBuilder("(");
            if (!check(TokenType.RIGHT_PAREN)) {
                do {
                    Token itemType = type();
                    Token itemName = consume(TokenType.IDENTIFIER, "Yêu cầu tên trường trong Tuple.");
                    builder.append(itemType.lexeme()).append(" ").append(itemName.lexeme());
                } while (match(TokenType.COMMA) && appendComma(builder));
            }
            consume(TokenType.RIGHT_PAREN, "Yêu cầu dấu ')' để đóng kiểu Tuple.");
            builder.append(")");
            
            if (match(TokenType.QUESTION)) {
                builder.append("?");
            }
            
            return new Token(TokenType.IDENTIFIER, builder.toString());
        }
        
        if (checkFunctionType()) {
            Token firstToken = advance();
            StringBuilder builder = new StringBuilder(firstToken.lexeme());
            consume(TokenType.LEFT_PAREN, "Yêu cầu dấu '(' sau từ khóa hàm.");
            builder.append("(");
            if (!check(TokenType.RIGHT_PAREN)) {
                do {
                    Token paramType = type();
                    builder.append(paramType.lexeme());
                } while (match(TokenType.COMMA) && appendComma(builder));
            }
            consume(TokenType.RIGHT_PAREN, "Yêu cầu dấu ')' để đóng danh sách tham số kiểu hàm.");
            builder.append(")");
            
            consume(TokenType.ARROW, "Yêu cầu toán tử '->' để chỉ định kiểu trả về.");
            builder.append(" -> ");
            
            Token returnType = type();
            builder.append(returnType.lexeme());
            
            if (match(TokenType.QUESTION)) {
                builder.append("?");
            }
            
            return new Token(TokenType.IDENTIFIER, builder.toString());
        }
        
        Token first = peek();
        if (checkTypeKeyword()) {
            Token typeToken = advance();
            StringBuilder builder = new StringBuilder(typeToken.lexeme());
            
            if (match(TokenType.LESS_THAN)) {
                builder.append("<");
                do {
                    Token innerType = type();
                    builder.append(innerType.lexeme());
                } while (match(TokenType.COMMA) && appendComma(builder));
                consume(TokenType.GREATER_THAN, "Yêu cầu dấu '>' để đóng kiểu generic.");
                builder.append(">");
            }
            
            if (match(TokenType.QUESTION)) {
                builder.append("?");
            }
            
            return new Token(typeToken.type(), builder.toString());
        }
        throw error(peek(), "Yêu cầu kiểu dữ liệu hợp lệ.");
    }

    /**
     * Kiểm tra xem token tiếp theo có bắt đầu một khai báo kiểu hàm (function type) hay không.
     * Kiểm tra từ khóa hàm (hàm/function/func) kết hợp dấu mở ngoặc đơn '('.
     * 
     * @return {@code true} nếu là kiểu hàm, ngược lại là {@code false}
     */
    private boolean checkFunctionType() {
        if (isAtEnd()) return false;
        Token first = peek();
        boolean isFuncKeyword = (first.type() == TokenType.FUNCTION) || 
                               (first.type() == TokenType.IDENTIFIER && "func".equals(first.lexeme()));
        if (!isFuncKeyword) return false;
        
        if (current + 1 >= tokens.size()) return false;
        return tokens.get(current + 1).type() == TokenType.LEFT_PAREN;
    }

    /**
     * Nối thêm dấu phẩy cho chuỗi generic.
     * 
     * @param builder StringBuilder đang dựng kiểu
     * @return {@code true} luôn trả về true để duy trì cấu trúc lặp
     */
    private boolean appendComma(StringBuilder builder) {
        builder.append(", ");
        return true;
    }

    /**
     * Phân tích một câu lệnh thực thi.
     * 
     * @return Đối tượng {@link Stmt} tương ứng
     */
    private Stmt statement() {
        if (match(TokenType.IF)) return ifStatement();
        if (match(TokenType.LOOP)) return loopStatement();
        if (match(TokenType.FOR)) return forStatement();
        if (match(TokenType.SWITCH)) return switchStatement();
        if (match(TokenType.RETURN)) return returnStatement();
        if (match(TokenType.BREAK)) return breakStatement();
        if (match(TokenType.CONTINUE)) return continueStatement();
        if (match(TokenType.LEFT_RACE)) return new Stmt.Block(block());
        
        return expressionStatement();
    }

    /**
     * Phân tích câu lệnh rẽ nhánh điều kiện 'nếu'.
     * Hỗ trợ cấu trúc lồng nhau 'còn_nếu' và 'không_thì'.
     * 
     * @return Đối tượng {@link Stmt.If}
     */
    private Stmt ifStatement() {
        Expr condition = expression();
        consume(TokenType.THEN, "Yêu cầu từ khóa 'thì' sau điều kiện 'nếu'.");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        
        if (match(TokenType.ELSE_IF)) {
            elseBranch = ifStatement();
        } else if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }
        
        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    /**
     * Phân tích câu lệnh vòng lặp 'lặp'.
     * Tiêu thụ từ khóa 'thì' (then) nếu có sau biểu thức điều kiện.
     * 
     * @return Đối tượng {@link Stmt.While}
     */
    private Stmt loopStatement() {
        Expr condition = expression();
        match(TokenType.THEN);
        Stmt body = statement();
        return new Stmt.While(condition, body);
    }

    /**
     * Phân tích câu lệnh vòng lặp 'duyệt'.
     * 
     * @return Đối tượng {@link Stmt.For}
     */
    private Stmt forStatement() {
        Token name = consume(TokenType.IDENTIFIER, "Yêu cầu tên biến lặp sau từ khóa 'duyệt'.");
        
        if (match(TokenType.IN)) { // từ khóa "từ"
            Expr start = expression();
            if (!check(TokenType.RANGE_EXCLUSIVE) && !check(TokenType.RANGE_INCLUSIVE)) {
                throw error(peek(), "Yêu cầu toán tử khoảng số ('đến' hoặc 'đến_hết').");
            }
            Token operator = advance();
            Expr end = expression();
            Stmt body = statement();
            return new Stmt.For(name, start, operator, end, body);
        } else if (match(TokenType.OF)) { // từ khóa "của"
            Expr collection = expression();
            Stmt body = statement();
            return new Stmt.For(name, collection, body);
        } else {
            throw error(peek(), "Yêu cầu từ khóa 'từ' hoặc 'của' trong vòng lặp 'duyệt'.");
        }
    }

    /**
     * Phân tích câu lệnh rẽ nhánh nhiều trường hợp 'trường_hợp'.
     * 
     * @return Đối tượng {@link Stmt.Switch}
     */
    private Stmt switchStatement() {
        Expr value = expression();
        consume(TokenType.LEFT_RACE, "Yêu cầu dấu '{' để bắt đầu khối 'trường_hợp'.");
        
        List<Stmt.SwitchCase> cases = new ArrayList<>();
        while (!check(TokenType.RIGHT_RACE) && !isAtEnd()) {
            while (match(TokenType.SEMICOLON)) {
                // Bỏ qua các dấu chấm phẩy trống hoặc dòng trống trong khối 'trường_hợp'
            }
            if (check(TokenType.RIGHT_RACE)) {
                break;
            }
            cases.add(switchCase());
        }
        consume(TokenType.RIGHT_RACE, "Yêu cầu dấu '}' để đóng khối 'trường_hợp'.");
        
        return new Stmt.Switch(value, cases);
    }

    /**
     * Phân tích một nhánh lựa chọn trong lệnh 'trường_hợp'.
     * 
     * @return Đối tượng {@link Stmt.SwitchCase}
     */
    private Stmt.SwitchCase switchCase() {
        List<Expr> patterns = new ArrayList<>();
        do {
            patterns.add(and());
        } while (match(TokenType.OR));
        
        consume(TokenType.ARROW, "Yêu cầu toán tử '->' sau các mẫu khớp.");
        
        Stmt body;
        if (check(TokenType.LEFT_RACE)) {
            body = statement();
        } else {
            Expr expr = expression();
            match(TokenType.SEMICOLON); // Tùy chọn tiêu thụ dấu chấm phẩy
            body = new Stmt.Expression(expr);
        }
        
        return new Stmt.SwitchCase(patterns, body);
    }

    /**
     * Phân tích câu lệnh 'trả_về'.
     * 
     * @return Đối tượng {@link Stmt.Return}
     */
    private Stmt returnStatement() {
        Token keyword = previous();
        Expr value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = expression();
        }
        consume(TokenType.SEMICOLON, "Yêu cầu dấu ';' sau câu lệnh 'trả_về'.");
        return new Stmt.Return(keyword, value);
    }

    /**
     * Phân tích câu lệnh dừng vòng lặp 'dừng'.
     * 
     * @return Đối tượng {@link Stmt.Break}
     */
    private Stmt breakStatement() {
        Token keyword = previous();
        consume(TokenType.SEMICOLON, "Yêu cầu dấu ';' sau câu lệnh 'dừng'.");
        return new Stmt.Break(keyword);
    }

    /**
     * Phân tích câu lệnh tiếp tục vòng lặp 'tiếp'.
     * 
     * @return Đối tượng {@link Stmt.Continue}
     */
    private Stmt continueStatement() {
        Token keyword = previous();
        consume(TokenType.SEMICOLON, "Yêu cầu dấu ';' sau câu lệnh 'tiếp'.");
        return new Stmt.Continue(keyword);
    }

    /**
     * Phân tích khối lệnh bọc trong ngoặc nhọn.
     * 
     * @return Danh sách các câu lệnh trong khối
     */
    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();
        while (!check(TokenType.RIGHT_RACE) && !isAtEnd()) {
            if (peek().type() != TokenType.FUNCTION && !checkVarDecl()) {
                int start = current;
                try {
                    Expr expr = expression();
                    if (check(TokenType.RIGHT_RACE)) {
                        statements.add(new Stmt.Expression(expr));
                        break;
                    } else {
                        current = start;
                    }
                } catch (Exception e) {
                    current = start;
                }
            }
            Stmt decl = declaration();
            if (decl != null) {
                statements.add(decl);
            }
        }
        consume(TokenType.RIGHT_RACE, "Yêu cầu dấu '}' để đóng khối lệnh.");
        return statements;
    }

    /**
     * Phân tích câu lệnh biểu thức.
     * 
     * @return Đối tượng {@link Stmt.Expression}
     */
    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "Yêu cầu dấu ';' sau câu lệnh biểu thức.");
        return new Stmt.Expression(expr);
    }

    // =========================================================================
    // Các Quy tắc Phân tích Biểu thức (Expression Rules)
    // =========================================================================

    /**
     * Phân tích biểu thức.
     * 
     * @return Đối tượng {@link Expr} tương ứng
     */
    private Expr expression() {
        return assignment();
    }

    /**
     * Phân tích phép gán.
     * 
     * @return Đối tượng {@link Expr} gán hoặc biểu thức ưu tiên tiếp theo
     */
    private Expr assignment() {
        Expr expr = or();
        
        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            Expr value = assignment();
            
            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }
            
            throw error(equals, "Đích gán không hợp lệ.");
        }
        
        return expr;
    }

    /**
     * Phân tích phép toán logic OR.
     * 
     * @return Đối tượng {@link Expr} logic OR hoặc biểu thức tiếp theo
     */
    private Expr or() {
        Expr expr = and();
        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    /**
     * Phân tích phép toán logic AND.
     * 
     * @return Đối tượng {@link Expr} logic AND hoặc biểu thức tiếp theo
     */
    private Expr and() {
        Expr expr = equality();
        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
        }
        return expr;
    }

    /**
     * Phân tích phép toán so sánh bằng/khác.
     * 
     * @return Đối tượng {@link Expr} hoặc biểu thức tiếp theo
     */
    private Expr equality() {
        Expr expr = comparison();
        while (match(TokenType.NOT_EQUAL, TokenType.EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * Phân tích phép so sánh quan hệ.
     * 
     * @return Đối tượng {@link Expr} hoặc biểu thức tiếp theo
     */
    private Expr comparison() {
        Expr expr = term();
        while (match(TokenType.GREATER_THAN, TokenType.GREATER_THAN_EQUAL, TokenType.LESS_THAN, TokenType.LESS_THAN_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * Phân tích phép toán cộng/trừ.
     * 
     * @return Đối tượng {@link Expr} hoặc biểu thức tiếp theo
     */
    private Expr term() {
        Expr expr = factor();
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * Phân tích phép toán nhân/chia/chia dư.
     * 
     * @return Đối tượng {@link Expr} hoặc biểu thức tiếp theo
     */
    private Expr factor() {
        Expr expr = unary();
        while (match(TokenType.SLASH, TokenType.STAR, TokenType.PERCENTAGE)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        return expr;
    }

    /**
     * Phân tích phép toán một ngôi (phủ định, dấu âm).
     * 
     * @return Đối tượng {@link Expr}
     */
    private Expr unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return call();
    }

    /**
     * Phân tích lời gọi hàm.
     * 
     * @return Đối tượng {@link Expr}
     */
    private Expr call() {
        Expr expr = primary();
        while (true) {
            if (match(TokenType.LEFT_PAREN)) {
                expr = finishCall(expr);
            } else if (match(TokenType.DOT)) {
                Token name = consume(TokenType.IDENTIFIER, "Yêu cầu tên thuộc tính hoặc phương thức sau dấu '.'.");
                expr = new Expr.Get(expr, name);
            } else {
                break;
            }
        }
        return expr;
    }

    /**
     * Phân tích hoàn tất danh sách tham số lời gọi hàm.
     * 
     * @param callee Biểu thức được gọi
     * @return Đối tượng {@link Expr.Call}
     */
    private Expr finishCall(Expr callee) {
        List<Expr> arguments = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(expression());
            } while (match(TokenType.COMMA));
        }
        Token paren = consume(TokenType.RIGHT_PAREN, "Yêu cầu dấu ')' sau danh sách đối số.");
        return new Expr.Call(callee, paren, arguments);
    }

    /**
     * Phân tích giá trị nguyên tử (Primary Expression).
     * Hỗ trợ các kiểu dữ liệu cơ bản, hằng số, biến, khối biểu thức lồng và Tuple nhiều giá trị.
     * 
     * @return Đối tượng {@link Expr} tương ứng với biểu thức nguyên tử
     * @throws ParseError nếu phát hiện biểu thức không hợp lệ hoặc thiếu dấu ngoặc đóng
     */
    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NULL)) return new Expr.Literal(null);
        
        if (match(TokenType.IF, TokenType.SWITCH)) {
            current--; // Quay lại để statement() nhận biết từ khóa
            Stmt stmt = statement();
            return new Expr.StmtExpr(stmt);
        }
        
        if (match(TokenType.LITERAL_INTEGER, TokenType.LITERAL_DOUBLE, TokenType.LITERAL_FLOAT, TokenType.LITERAL_STRING, TokenType.LITERAL_CHAR)) {
            return new Expr.Literal(getLiteralValue(previous()));
        }
        
        if (match(TokenType.IDENTIFIER, TokenType.IN)) {
            // Hỗ trợ cả TokenType.IN làm định danh trong biểu thức để cho phép gọi hàm xuất chuẩn 'in(...)' của tiếng Việt
            return new Expr.Variable(previous());
        }
        
        if (match(TokenType.LEFT_PAREN)) {
            if (match(TokenType.RIGHT_PAREN)) {
                return new Expr.Tuple(new ArrayList<>());
            }
            Expr expr = expression();
            if (match(TokenType.COMMA)) {
                List<Expr> expressions = new ArrayList<>();
                expressions.add(expr);
                do {
                    expressions.add(expression());
                } while (match(TokenType.COMMA));
                consume(TokenType.RIGHT_PAREN, "Yêu cầu dấu ')' để đóng Tuple.");
                return new Expr.Tuple(expressions);
            }
            consume(TokenType.RIGHT_PAREN, "Yêu cầu dấu ')' sau biểu thức nhóm.");
            return new Expr.Grouping(expr);
        }
        
        throw error(peek(), "Yêu cầu biểu thức hợp lệ.");
    }

    // =========================================================================
    // Các Helper Logic và Trạng thái
    // =========================================================================

    /**
     * Lấy giá trị thô tương ứng với kiểu token giá trị.
     * 
     * @param token Token giá trị cần phân tích
     * @return Giá trị thô kiểu {@code Object}
     */
    private Object getLiteralValue(Token token) {
        switch (token.type()) {
            case LITERAL_INTEGER:
                return Integer.parseInt(token.lexeme());
            case LITERAL_DOUBLE:
                return Double.parseDouble(token.lexeme());
            case LITERAL_FLOAT:
                return Float.parseFloat(token.lexeme());
            case LITERAL_STRING:
                String lexeme = token.lexeme();
                if (lexeme.length() >= 2 && lexeme.startsWith("\"") && lexeme.endsWith("\"")) {
                    return lexeme.substring(1, lexeme.length() - 1);
                }
                return lexeme;
            case LITERAL_CHAR:
                String charLexeme = token.lexeme();
                if (charLexeme.length() >= 2 && charLexeme.startsWith("'") && charLexeme.endsWith("'")) {
                    return charLexeme.substring(1, charLexeme.length() - 1);
                }
                return charLexeme;
            default:
                return null;
        }
    }

    /**
     * Kiểm tra xem khai báo biến hiện tại có ghi rõ kiểu dữ liệu hay không.
     * Thuật toán này peek ahead để phát hiện cấu trúc kiểu (bao gồm generic/nullable).
     * 
     * @return {@code true} nếu có định nghĩa kiểu rõ ràng, {@code false} nếu tự suy luận kiểu
     */
    private boolean isTypeSpecified() {
        int temp = current;
        if (!isTypeToken(temp)) return false;
        temp++;
        
        if (temp < tokens.size() && tokens.get(temp).type() == TokenType.LESS_THAN) {
            temp++;
            int count = 1;
            while (temp < tokens.size() && count > 0) {
                TokenType t = tokens.get(temp).type();
                if (t == TokenType.LESS_THAN) count++;
                else if (t == TokenType.GREATER_THAN) count--;
                temp++;
            }
        }
        
        if (temp < tokens.size() && tokens.get(temp).type() == TokenType.QUESTION) {
            temp++;
        }
        
        return temp < tokens.size() && tokens.get(temp).type() == TokenType.IDENTIFIER;
    }

    /**
     * Kiểm tra xem token tại index có phải là token kiểu dữ liệu tiềm năng không.
     * 
     * @param index Vị trí token cần kiểm tra
     * @return {@code true} nếu là token kiểu dữ liệu, ngược lại là {@code false}
     */
    private boolean isTypeToken(int index) {
        if (index >= tokens.size()) return false;
        TokenType t = tokens.get(index).type();
        return t == TokenType.IDENTIFIER ||
               t == TokenType.TYPE_STRING ||
               t == TokenType.TYPE_CHAR ||
               t == TokenType.TYPE_BOOLEAN ||
               t == TokenType.TYPE_INT ||
               t == TokenType.TYPE_INT16 ||
               t == TokenType.TYPE_INT32 ||
               t == TokenType.TYPE_INT64 ||
               t == TokenType.TYPE_DOUBLE ||
               t == TokenType.TYPE_FLOAT ||
               t == TokenType.VOID ||
               t == TokenType.NULL;
    }

    /**
     * Kiểm tra xem token hiện tại có phải từ khóa kiểu dữ liệu hay không.
     * 
     * @return {@code true} nếu khớp, ngược lại là {@code false}
     */
    private boolean checkTypeKeyword() {
        return check(TokenType.IDENTIFIER) ||
               check(TokenType.TYPE_STRING) ||
               check(TokenType.TYPE_CHAR) ||
               check(TokenType.TYPE_BOOLEAN) ||
               check(TokenType.TYPE_INT) ||
               check(TokenType.TYPE_INT16) ||
               check(TokenType.TYPE_INT32) ||
               check(TokenType.TYPE_INT64) ||
               check(TokenType.TYPE_DOUBLE) ||
               check(TokenType.TYPE_FLOAT) ||
               check(TokenType.VOID) ||
               check(TokenType.NULL);
    }

    /**
     * Kiểm tra xem câu lệnh kế tiếp có phải là khai báo biến/hằng không.
     * 
     * @return {@code true} nếu khớp, ngược lại là {@code false}
     */
    private boolean checkVarDecl() {
        return check(TokenType.VAR) || check(TokenType.CONST);
    }

    /**
     * Tiêu thụ token hiện tại nếu khớp với một trong các kiểu được truyền vào.
     * 
     * @param types Danh sách các kiểu token cần khớp
     * @return {@code true} nếu khớp và đã consume, {@code false} nếu không khớp
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    /**
     * Tiêu thụ một token có kiểu chỉ định, nếu không khớp sẽ ném ra lỗi cú pháp.
     * 
     * @param type Kiểu token yêu cầu
     * @param message Thông điệp lỗi hiển thị nếu không khớp
     * @return Token được tiêu thụ thành công
     * @throws ParseError nếu không khớp kiểu token
     */
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    /**
     * Kiểm tra xem token hiện tại có thuộc kiểu chỉ định hay không.
     * 
     * @param type Kiểu token cần kiểm tra
     * @return {@code true} nếu khớp, {@code false} nếu không khớp hoặc đã hết token
     */
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    /**
     * Di chuyển con trỏ sang token tiếp theo và trả về token trước đó.
     * 
     * @return Token vừa được duyệt qua
     */
    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    /**
     * Kiểm tra xem đã kết thúc luồng token chưa.
     * 
     * @return {@code true} nếu đã kết thúc, ngược lại là {@code false}
     */
    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    /**
     * Xem token hiện tại mà không tiêu thụ.
     * 
     * @return Token hiện tại
     */
    private Token peek() {
        return tokens.get(current);
    }

    /**
     * Lấy token vừa xử lý xong.
     * 
     * @return Token trước đó
     */
    private Token previous() {
        return tokens.get(current - 1);
    }

    /**
     * Tạo lỗi cú pháp dựa trên vị trí token và thông báo.
     * 
     * @param token Token gây ra lỗi
     * @param message Thông điệp lỗi
     * @return Đối tượng {@link ParseError} đại diện cho lỗi cú pháp
     */
    private ParseError error(Token token, String message) {
        String fullMessage;
        if (token.type() == TokenType.EOF) {
            fullMessage = "[Cuối file] " + message;
        } else {
            fullMessage = "[Token '" + token.lexeme() + "'] " + message;
        }
        return new ParseError(token, fullMessage);
    }

    /**
     * Đồng bộ hóa Parser sau khi gặp lỗi cú pháp (Panic Mode Recovery).
     * Bỏ qua các token bị lỗi cho đến khi gặp dấu chấm phẩy hoặc từ khóa bắt đầu câu lệnh mới.
     */
    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type() == TokenType.SEMICOLON) return;
            
            switch (peek().type()) {
                case IF:
                case LOOP:
                case FOR:
                case FUNCTION:
                case RETURN:
                case VAR:
                case CONST:
                case SWITCH:
                    return;
                default:
                    break;
            }
            advance();
        }
    }
}
