package nova.lexer;

import nova.lexer.keyword.DefaultKeyword;
import nova.lexer.keyword.KeywordRegistry;

public class Lexer {
    private final String src;
    private int pos = 0; // tong so ky tu trong file duoc quet
    private int col = 1; // vi tri cua cac ky tu trong dong hien tai
    private int line = 1; // vi tri cua cac dong
    private static final KeywordRegistry registry = new KeywordRegistry();
    private TokenType lastTokenType = null;

    static {
        DefaultKeyword.load(registry);
    }

    public Lexer(String src) {
        this.src = src;
    }

    public Token nextToken() throws LexerError {
        Token token = scanToken();
        lastTokenType = token.type();
        return token;
    }

    private Token scanToken() throws LexerError {
        if (skipWhiteSpace()) {
            advance(); // tiêu thụ '\n'
            moveToNextLine();
            return new Token(TokenType.SEMICOLON, "\n");
        }

        if (isAtEnd()) {
            if (shouldInsertSemicolon(lastTokenType)) {
                lastTokenType = TokenType.SEMICOLON;
                return new Token(TokenType.SEMICOLON, "");
            }
            return new Token(TokenType.EOF, "");
        }

        char c = advance();

        var illegalCharacter = "Ky tu khong hop le %c tai dong %d, cot %d";
        switch (c) {
            case '(':
                return new Token(TokenType.LEFT_PAREN, "(");
            case ')':
                return new Token(TokenType.RIGHT_PAREN, ")");
            case '{':
                return new Token(TokenType.LEFT_RACE, "{");
            case '}':
                return new Token(TokenType.RIGHT_RACE, "}");
            case '[':
                return new Token(TokenType.LEFT_BRACKET, "[");
            case ']':
                return new Token(TokenType.RIGHT_BRACKET, "]");
            case ';':
                return new Token(TokenType.SEMICOLON, ";");
            case '.':
                if (match('.')) {
                    if (match('=')) {
                        return new Token(TokenType.RANGE_INCLUSIVE, "..=");
                    }
                    return new Token(TokenType.RANGE_EXCLUSIVE, "..");
                }
                return new Token(TokenType.DOT, ".");
            case ',':
                return new Token(TokenType.COMMA, ",");
            case ':':
                return new Token(TokenType.COLON, ":");
            case '+':
                return new Token(TokenType.PLUS, "+");
            case '-':
                if (match('>')) return new Token(TokenType.ARROW, "->");
                return new Token(TokenType.MINUS, "-");
            case '*':
                return new Token(TokenType.STAR, "*");
            case '/':
                return new Token(TokenType.SLASH, "/");
            case '%':
                return new Token(TokenType.PERCENTAGE, "%");
            case '=':
                return match('=') ?
                        new Token(TokenType.EQUAL, "==") : new Token(TokenType.ASSIGN, "=");
            case '!':
                return match('=') ?
                        new Token(TokenType.NOT_EQUAL, "!=") : new Token(TokenType.NOT, "!");
            case '?':
                return new Token(TokenType.QUESTION, "?");
            case '<':
                return match('=') ?
                        new Token(TokenType.LESS_THAN_EQUAL, "<=") : new Token(TokenType.LESS_THAN, "<");
            case '>':
                return match('=') ?
                        new Token(TokenType.GREATER_THAN_EQUAL, ">=") : new Token(TokenType.GREATER_THAN, ">");
            case '&':
                if (match('&')) return new Token(TokenType.AND, "&&");
                throw new LexerError(String.format(illegalCharacter, '&', line, col));
            case '|':
                if (match('|')) return new Token(TokenType.OR, "||");
                return new Token(TokenType.OR, "|");
            case '"': // read string
                return readString();
            case '\'': // read character
                return readChar();
            default:
                if (Character.isDigit(c)) {
                    return readNumber(c);
                }
                if (Character.isLetter(c) || c == '_') {
                    return readIdentifierOrKeyword(c);
                }
                throw new LexerError(String.format(illegalCharacter, c, line, col));
        }
    }

    /**
     * Kiem tra xem pos da tro den ky tu cuoi cung hay chua
     *
     * @return boolean
     */
    private boolean isAtEnd() {
        return pos >= src.length();
    }

    /**
     * Lay ra ky tu o vi tri pos
     *
     * @return ky tu o vi tri pos
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return src.charAt(pos);
    }

    /**
     * Kiem tra xem ky tu ke tiep co phai la ky tu ky vong hay khong
     *
     * @param expected Ky tu ky vong
     * @return boolean
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (src.charAt(pos) != expected) return false;
        pos++;
        col++;
        return true;
    }

    private char advance() {
        char c = src.charAt(pos++);
        col++;
        return c;
    }

    private boolean shouldInsertSemicolon(TokenType type) {
        if (type == null) return false;
        switch (type) {
            case IDENTIFIER:
            case TYPE_STRING:
            case TYPE_CHAR:
            case TYPE_BOOLEAN:
            case TYPE_INT:
            case TYPE_INT16:
            case TYPE_INT32:
            case TYPE_INT64:
            case TYPE_DOUBLE:
            case TYPE_FLOAT:
            case VOID:
            case NULL:
            case LITERAL_STRING:
            case LITERAL_CHAR:
            case LITERAL_BOOLEAN:
            case TRUE:
            case FALSE:
            case LITERAL_INTEGER:
            case LITERAL_DOUBLE:
            case LITERAL_FLOAT:
            case RIGHT_PAREN:
            case RIGHT_BRACKET:
            case RIGHT_RACE:
            case BREAK:
            case CONTINUE:
            case RETURN:
                return true;
            default:
                return false;
        }
    }

    /**
     * Bo qua cac ky tu khoang trang. Neu nhu ky tu hien tai la khoang trang,
     * se bo qua chung va tra ve vi tri (pos) cua ky tu thong thuong
     */
    private boolean skipWhiteSpace() {
        while (!isAtEnd()) {
            char c = peek(); // lay ra ky tu o vi tri hien tai
            switch (c) {
                case ' ':
                case '\t': // tab
                case '\r': // quay ve dau dong
                    advance();
                    break;
                case '\n':
                    if (shouldInsertSemicolon(lastTokenType)) {
                        // Dung lai de scanToken() xu ly thanh SEMICOLON
                        return true;
                    }
                    advance();
                    moveToNextLine();
                    break;
                case '/':
                    // comment 1 dong
                    if (peekNext() == '/') {
                        while (peek() != '\n' && !isAtEnd()) {
                            advance(); // bo qua cho den het dong
                        }
                    }
                    // Cmt nhieu dong
                    else if (peekNext() == '*') {
                        advance(); // nuot /
                        advance(); // nuot *
                        while (!isAtEnd()) {
                            if (peek() == '*' && peekNext() == '/') {
                                advance(); // nuot *
                                advance(); // nuot /
                                break;
                            }
                            char next = advance();
                            if (next == '\n') {
                                moveToNextLine();
                            }
                        }
                    } else {
                        return false;
                    }
                    break;
                default:
                    return false; // ky tu thong thuong
            }
        }
        return false;
    }

    /**
     * Lay ky tu ke tiep
     *
     * @return char
     */
    private char peekNext() {
        if (pos + 1 >= src.length()) return '\0';
        return src.charAt(pos + 1);
    }

    private Token readNumber(char firstDigit) {
        var builder = new StringBuilder();
        builder.append(firstDigit);

        while (Character.isDigit(peek())) {
            builder.append(advance());
        }

        // Kiem tra xem day co phai so thuc hay khong
        if (peek() == '.' && Character.isDigit(peekNext())) {
            builder.append(advance());
            while (Character.isDigit(peek())) {
                builder.append(advance());
            }
            return new Token(TokenType.LITERAL_DOUBLE, builder.toString());
        }
        return new Token(TokenType.LITERAL_INTEGER, builder.toString());
    }

    private Token readString() throws LexerError {
        var builder = new StringBuilder();

        while (!isAtEnd()) {
            char c = peek();
            if (c == '"') {
                break; // Gặp dấu nháy kép kết thúc thực sự
            }

            if (c == '\\') {
                builder.append(advance()); // Nuốt dấu gạch chéo '\'
                if (isAtEnd()) {
                    throw new LexerError("Chuỗi chưa được đóng tại dòng " + line + ", cột " + col);
                }
                builder.append(advance()); // Nuốt ký tự được escape đi sau nó (ví dụ '"', 'n', 't')
            } else {
                char next = advance();
                builder.append(next);
                if (next == '\n') {
                    moveToNextLine();
                }
            }
        }

        if (isAtEnd()) {
            throw new LexerError("Chuỗi chưa được đóng lại bằng dấu ngoặc kép tại dòng " + line + ", cột " + col);
        }
        advance(); // Bỏ qua dấu nháy kép đóng '"'
        return new Token(TokenType.LITERAL_STRING, builder.toString());
    }

    private Token readIdentifierOrKeyword(char firstChar) {
        var builder = new StringBuilder();
        builder.append(firstChar);

        while (Character.isLetterOrDigit(peek()) || peek() == '_') {
            builder.append(advance());
        }

        var lexeme = builder.toString();

        var registeredKeyword = registry.getKeyword(lexeme);
        if (registeredKeyword != null) {
            return new Token(registeredKeyword.type(), lexeme);
        }

        return new Token(TokenType.IDENTIFIER, lexeme);
    }

    private Token readChar() throws LexerError {
        // Lưu lại vị trí bắt đầu (vị trí của dấu nháy đơn mở '\'')
        int startPos = pos - 1;

        if (isAtEnd()) {
            throw new LexerError("Thiếu ký tự và dấu nháy đơn đóng tại dòng " + line + ", cột " + col);
        }

        char c = advance();

        // 1. Xử lý ký tự Escape (như \n, \t, \r, \', \\)
        if (c == '\\') {
            if (isAtEnd()) {
                throw new LexerError("Ký tự escape chưa hoàn thành tại dòng " + line + ", cột " + col);
            }
            advance(); // Đi qua ký tự đứng sau dấu gạch chéo (ví dụ: 'n', 't', ''')
        }
        // 2. Báo lỗi nếu gặp ký tự trống (Empty char literal: '')
        else if (c == '\'') {
            throw new LexerError("Ký tự trống không hợp lệ tại dòng " + line + ", cột " + col);
        }

        // 3. Kiểm tra dấu nháy đơn đóng '\''
        if (isAtEnd() || peek() != '\'') {
            throw new LexerError("Ký tự không hợp lệ (vượt quá 1 ký tự) hoặc thiếu dấu nháy đơn đóng tại dòng " + line + ", cột " + col);
        }

        advance(); // Đi qua dấu nháy đơn đóng '\''

        // Lấy chuỗi gốc làm Lexeme (ví dụ: "'a'" hoặc "'\n'")
        String lexeme = src.substring(startPos, pos);

        return new Token(TokenType.LITERAL_CHAR, lexeme);
    }

    private void moveToNextLine() {
        line++;
        col = 1;
    }
}
