package nova.interpreter.exception;

import nova.lexer.Token;

/**
 * Lớp đại diện cho lỗi ngữ nghĩa (Semantic Error) phát hiện được trong quá trình phân tích tĩnh (phân tích tầm vực, kiểm tra kiểu).
 * 
 * @author XUAN HOAN
 */
public class SemanticError {
    /** Token liên quan trực tiếp đến lỗi ngữ nghĩa. */
    private final Token token;
    /** Thông điệp chi tiết mô tả lỗi. */
    private final String message;

    /**
     * Khởi tạo một đối tượng lỗi ngữ nghĩa mới.
     * 
     * @param token Token gây lỗi
     * @param message Thông điệp mô tả lỗi
     */
    public SemanticError(Token token, String message) {
        this.token = token;
        this.message = message;
    }

    /**
     * Lấy token liên quan đến lỗi.
     * 
     * @return Đối tượng Token
     */
    public Token getToken() {
        return token;
    }

    /**
     * Lấy thông điệp lỗi chi tiết.
     * 
     * @return Chuỗi mô tả lỗi
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[Token '" + token.lexeme() + "'] Lỗi: " + message;
    }
}
