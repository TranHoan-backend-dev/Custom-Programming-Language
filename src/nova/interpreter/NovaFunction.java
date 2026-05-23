package nova.interpreter;

import nova.ast.Stmt;
import java.util.List;

/**
 * Lớp đại diện cho một hàm tự định nghĩa trong ngôn ngữ Nova tại thời điểm chạy (runtime).
 * Hỗ trợ lưu trữ cấu trúc AST của hàm, danh sách tham số, kiểu trả về và môi trường bao đóng (closure)
 * để thực hiện cơ chế closures (các hàm lồng nhau có thể truy cập các biến cục bộ của hàm bao ngoài).
 * 
 * @author XUAN HOAN
 */
public class NovaFunction implements NovaCallable {
    /** Khai báo AST của hàm. */
    private final Stmt.Function declaration;
    
    /** Môi trường bao đóng tĩnh nơi hàm được khai báo (tầm vực tĩnh). */
    private final Environment closure;

    /**
     * Khởi tạo một đối tượng hàm NovaFunction mới.
     * 
     * @param declaration Đối tượng khai báo hàm trong AST
     * @param closure Môi trường bao đóng (lexical closure environment) của hàm
     */
    public NovaFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        return declaration.parameters.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.parameters.size(); i++) {
            environment.define(declaration.parameters.get(i).name.lexeme(), arguments.get(i));
        }

        try {
            interpreter.executeBlock(declaration.body, environment);
        } catch (ReturnException returnValue) {
            return returnValue.getValue();
        }

        return null;
    }

    @Override
    public String toString() {
        return "<hàm " + declaration.name.lexeme() + ">";
    }
}
