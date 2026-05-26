package nova.interpreter;

import nova.interpreter.exception.RuntimeError;
import nova.lexer.Token;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp đại diện cho môi trường (tầm vực) lưu trữ biến và hằng số tại thời điểm chạy (runtime).
 * Hỗ trợ liên kết tầm vực lồng nhau (lexical scoping) thông qua liên kết trỏ về môi trường cha (enclosing).
 * 
 * @author XUAN HOAN
 */
public class Environment {
    /** Môi trường cha bao ngoài tầm vực hiện tại. Bằng {@code null} đối với môi trường toàn cầu (global). */
    final Environment enclosing;
    
    /** Bảng ánh xạ lưu trữ tên biến và giá trị tương ứng của chúng tại thời điểm chạy. */
    private final Map<String, Object> values = new HashMap<>();

    /**
     * Khởi tạo một môi trường toàn cầu mới (không có môi trường cha bao ngoài).
     */
    public Environment() {
        this.enclosing = null;
    }

    /**
     * Khởi tạo một môi trường cục bộ mới với môi trường cha chỉ định.
     * 
     * @param enclosing Môi trường cha bao ngoài của môi trường cục bộ mới
     */
    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    /**
     * Định nghĩa một biến mới trong môi trường hiện tại.
     * Cho phép gán giá trị khởi tạo ban đầu cho biến.
     * 
     * @param name Tên của biến cần định nghĩa
     * @param value Giá trị khởi tạo của biến
     */
    public void define(String name, Object value) {
        values.put(name, value);
    }

    /**
     * Truy xuất giá trị của một biến từ môi trường hiện tại hoặc các môi trường cha bao ngoài.
     * Phương thức này thực hiện tìm kiếm động theo chuỗi Lexical Scope.
     * 
     * @param name Token tên biến cần lấy giá trị
     * @return Giá trị của biến tương ứng
     * @throws RuntimeError nếu biến chưa được khai báo trong bất kỳ tầm vực khả dụng nào
     */
    public Object get(Token name) {
        if (values.containsKey(name.lexeme())) {
            return values.get(name.lexeme());
        }

        if (enclosing != null) {
            return enclosing.get(name);
        }

        throw new RuntimeError(name, "Biến '" + name.lexeme() + "' chưa được khai báo.");
    }

    /**
     * Tra cứu một biến bằng tên chuỗi từ môi trường hiện tại hoặc các môi trường cha bao ngoài.
     * Phương thức này phục vụ cho việc nội suy chuỗi trực tiếp runtime.
     * 
     * @param name Tên biến cần tra cứu
     * @return Giá trị của biến, hoặc {@code null} nếu không tìm thấy trong bất kỳ tầm vực nào
     */
    public Object getByName(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        }
        if (enclosing != null) {
            return enclosing.getByName(name);
        }
        return null;
    }

    /**
     * Truy xuất giá trị của một biến từ một môi trường tổ tiên nằm cách môi trường hiện tại một khoảng cách xác định.
     * Phương thức này được tối ưu hóa dựa trên thông tin tầm vực tĩnh được phân tích bởi {@code Resolver}.
     * 
     * @param distance Khoảng cách từ môi trường hiện tại đến môi trường chứa biến (0 nghĩa là scope hiện tại)
     * @param name Tên của biến cần lấy giá trị
     * @return Giá trị của biến tương ứng
     */
    public Object getAt(int distance, String name) {
        return ancestor(distance).values.get(name);
    }

    /**
     * Gán giá trị mới cho một biến đã tồn tại trong môi trường hiện tại hoặc các môi trường cha.
     * 
     * @param name Token tên biến cần gán giá trị
     * @param value Giá trị mới cần gán cho biến
     * @throws RuntimeError nếu biến chưa được khai báo trong bất kỳ tầm vực khả dụng nào
     */
    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme())) {
            values.put(name.lexeme(), value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Biến '" + name.lexeme() + "' chưa được khai báo.");
    }

    /**
     * Gán giá trị mới cho một biến nằm ở môi trường tổ tiên cách môi trường hiện tại một khoảng cách xác định.
     * Phương thức này được tối ưu hóa dựa trên thông tin tầm vực tĩnh được phân tích bởi {@code Resolver}.
     * 
     * @param distance Khoảng cách từ môi trường hiện tại đến môi trường chứa biến cần gán
     * @param name Token tên biến cần gán giá trị
     * @param value Giá trị mới cần gán cho biến
     */
    public void assignAt(int distance, Token name, Object value) {
        ancestor(distance).values.put(name.lexeme(), value);
    }

    /**
     * Tìm kiếm môi trường tổ tiên nằm cách môi trường hiện tại một khoảng cách xác định.
     * 
     * @param distance Khoảng cách cần di chuyển lên trên chuỗi tầm vực (0 nghĩa là môi trường hiện tại)
     * @return Môi trường tổ tiên tương ứng
     */
    private Environment ancestor(int distance) {
        Environment environment = this;
        for (int i = 0; i < distance; i++) {
            environment = environment.enclosing;
        }
        return environment;
    }

    /**
     * Trả về bảng ánh xạ các biến hiện tại trong môi trường này.
     * @return Map chứa các biến.
     */
    public Map<String, Object> getValues() {
        return values;
    }
}
