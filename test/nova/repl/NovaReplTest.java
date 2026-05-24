package nova.repl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class NovaReplTest {

    public static void main(String[] args) {
        System.out.println("BẮT ĐẦU CHẠY KIỂM THỬ REPL...");
        try {
            testBasicExpression();
            testVariables();
            testMultilineInput();
            testInternalCommands();
            System.out.println("\nTẤT CẢ CA KIỂM THỬ REPL ĐẠT YÊU CẦU!");
        } catch (Throwable t) {
            System.err.println("\nKIỂM THỬ REPL THẤT BẠI:");
            t.printStackTrace();
            System.exit(1);
        }
    }

    private static String runReplWithInput(String input) {
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outBytes);
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        NovaRepl repl = new NovaRepl(in, out);
        repl.start();
        return outBytes.toString();
    }

    private static void testBasicExpression() {
        System.out.print("Kiểm thử biểu thức cơ bản... ");
        String output = runReplWithInput("1 + 2;\n:exit\n");
        if (!output.contains("3")) {
            throw new RuntimeException("Lỗi: Không in ra kết quả 3");
        }
        System.out.println("ĐẠT");
    }

    private static void testVariables() {
        System.out.print("Kiểm thử khai báo biến... ");
        String output = runReplWithInput("biến a = 10;\na * 2;\n:exit\n");
        if (!output.contains("20")) {
            throw new RuntimeException("Lỗi: Không in ra kết quả 20 từ biến a");
        }
        System.out.println("ĐẠT");
    }

    private static void testMultilineInput() {
        System.out.print("Kiểm thử nhập nhiều dòng... ");
        String input = "nếu (true) thì {\nprint(100);\n}\n:exit\n";
        String output = runReplWithInput(input);
        if (!output.contains("100")) {
            throw new RuntimeException("Lỗi: Không in ra kết quả 100 từ khối lệnh nhiều dòng");
        }
        System.out.println("ĐẠT");
    }

    private static void testInternalCommands() {
        System.out.print("Kiểm thử lệnh nội bộ... ");
        String output = runReplWithInput("biến x = 99;\n:vars\n:reset\n:vars\n:exit\n");
        if (!output.contains("x = 99")) {
            throw new RuntimeException("Lỗi: :vars không hiển thị biến x");
        }
        if (!output.contains("Không có biến nào.")) {
            throw new RuntimeException("Lỗi: :reset không hoạt động");
        }
        System.out.println("ĐẠT");
    }
}
