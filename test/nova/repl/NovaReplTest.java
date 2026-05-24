package nova.repl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

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
        var outBytes = new ByteArrayOutputStream();
        PrintStream out;
        try {
            out = new PrintStream(outBytes, true, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        var in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        var repl = new NovaRepl(in, out);
        repl.start();
        try {
            return outBytes.toString(java.nio.charset.StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException e) {
            return outBytes.toString();
        }
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
        String input = "bi\u1EBFn a = 10;\na * 2;\n:exit\n";
        String output = runReplWithInput(input);
        if (!output.contains("20")) {
            throw new RuntimeException("Lỗi: Không in ra kết quả 20 từ biến a. Output thực tế: " + output);
        }
        System.out.println("ĐẠT");
    }

    private static void testMultilineInput() {
        System.out.print("Kiểm thử nhập nhiều dòng... ");
        var input = "n\u1EBFu (\u0111\u00FAng) th\u00EC {\nprint(100);\n}\n:exit\n";
        var output = runReplWithInput(input);
        if (!output.contains("100")) {
            throw new RuntimeException("Lỗi: Không in ra kết quả 100 từ khối lệnh nhiều dòng");
        }
        System.out.println("ĐẠT");
    }

    private static void testInternalCommands() {
        System.out.print("Kiểm thử lệnh nội bộ... ");
        var output = runReplWithInput("biến x = 99;\n:vars\n:reset\n:vars\n:exit\n");
        if (!output.contains("x = 99")) {
            throw new RuntimeException("Lỗi: :vars không hiển thị biến x");
        }
        if (!output.contains("Không có biến nào.")) {
            throw new RuntimeException("Lỗi: :reset không hoạt động");
        }
        System.out.println("ĐẠT");
    }
}
