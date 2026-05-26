package nova.interpreter.exception;

/**
 * Ngoại lệ điều khiển dùng để lập tức bỏ qua phần thân còn lại của vòng lặp và tiến tới lần lặp kế tiếp
 * khi gặp câu lệnh 'tiếp' / 'continue'. Thừa kế từ {@link RuntimeException} với cấu hình tối giản không sinh call stack.
 * 
 * @author XUAN HOAN
 */
public class ContinueException extends RuntimeException {
    /**
     * Khởi tạo ngoại lệ ContinueException.
     */
    public ContinueException() {
        super(null, null, false, false);
    }
}
