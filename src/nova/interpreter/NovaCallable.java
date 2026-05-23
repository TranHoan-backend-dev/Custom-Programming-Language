package nova.interpreter;

import java.util.List;

/**
 * Interface đại diện cho các đối tượng có thể gọi được (callable) trong Nova runtime
 * (ví dụ: các hàm tự định nghĩa, các hàm dựng sẵn hoặc closure).
 * 
 * @author XUAN HOAN
 */
public interface NovaCallable {
    /**
     * Trả về số lượng tham số (arity) của hàm.
     * 
     * @return Số lượng tham số yêu cầu
     */
    int arity();

    /**
     * Thực thi lời gọi hàm với các đối số được truyền vào.
     * 
     * @param interpreter Trình thông dịch hiện tại đang thực thi chương trình
     * @param arguments Danh sách các giá trị đối số runtime truyền vào hàm
     * @return Kết quả trả về sau khi thực thi hàm
     */
    Object call(Interpreter interpreter, List<Object> arguments);
}
