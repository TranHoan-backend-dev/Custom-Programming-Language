package nova.interpreter;

import nova.lexer.Token;

/**
 * Lớp đại diện cho các lỗi xảy ra trong quá trình thông dịch thời gian chạy (runtime) của chương trình Nova.
 * Lưu giữ thông tin token gây lỗi để hỗ trợ thông báo chi tiết vị trí lỗi cho lập trình viên.
 * 
 * @author XUAN HOAN
 */
public class RuntimeError extends RuntimeException {
    /** Token liên quan trực tiếp đến lỗi runtime này. */
    public final Token token;

    /**
     * Khởi tạo một đối tượng RuntimeError mới với thông tin token và thông điệp lỗi.
     * 
     * @param token Token gây ra lỗi runtime
     * @param message Thông điệp chi tiết mô tả lỗi
     */
    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
