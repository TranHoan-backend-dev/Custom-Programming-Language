package nova.interpreter;

/**
 * Ngoại lệ điều khiển đặc biệt dùng để thoát sớm khỏi hàm và chuyển giá trị trả về
 * về nơi gọi hàm (lời gọi Expr.Call). Thừa kế từ {@link RuntimeException} mà không
 * ghi nhận call stack hệ thống để đạt hiệu năng tối ưu.
 * 
 * @author XUAN HOAN
 */
public class ReturnException extends RuntimeException {
    /** Giá trị trả về runtime của hàm. */
    private final Object value;

    /**
     * Khởi tạo một ngoại lệ Return với giá trị trả về cụ thể.
     * 
     * @param value Giá trị runtime trả về từ câu lệnh trả_về
     */
    public ReturnException(Object value) {
        super(null, null, false, false);
        this.value = value;
    }

    /**
     * Lấy giá trị trả về của hàm.
     * 
     * @return Giá trị runtime tương ứng
     */
    public Object getValue() {
        return value;
    }
}
