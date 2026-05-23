package nova.interpreter;

/**
 * Ngoại lệ điều khiển dùng để lập tức ngắt vòng lặp hiện tại khi gặp câu lệnh 'dừng' / 'break'.
 * Thừa kế từ {@link RuntimeException} với cấu hình tối giản không sinh call stack hệ thống.
 * 
 * @author XUAN HOAN
 */
public class BreakException extends RuntimeException {
    /**
     * Khởi tạo ngoại lệ BreakException.
     */
    public BreakException() {
        super(null, null, false, false);
    }
}
