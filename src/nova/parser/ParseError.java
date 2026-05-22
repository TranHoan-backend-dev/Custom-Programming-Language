package nova.parser;

import nova.lexer.Token;

/**
 * Lớp biểu diễn lỗi cú pháp (Syntax Error) xảy ra trong quá trình phân tích cú pháp.
 * Lớp này kế thừa {@link RuntimeException} và chứa token lỗi để định vị vị trí dòng/cột.
 * 
 * @author XUAN HOAN
 */
public class ParseError extends RuntimeException {
    /** Token gây ra lỗi cú pháp */
    private final Token token;

    /**
     * Khởi tạo một lỗi phân tích cú pháp.
     * 
     * @param token Token gây ra lỗi cú pháp
     * @param message Thông điệp mô tả chi tiết lỗi
     */
    public ParseError(Token token, String message) {
        super(message);
        this.token = token;
    }

    /**
     * Lấy token gây ra lỗi.
     * 
     * @return Token bị lỗi
     */
    public Token getToken() {
        return token;
    }
}
