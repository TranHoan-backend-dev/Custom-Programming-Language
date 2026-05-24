import nova.cli.InitCommand;
import nova.cli.RunCommand;
import nova.cli.CheckCommand;
import nova.cli.TestCommand;
import nova.cli.CliCommand;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            String cmd = args[0];
            CliCommand command;
            
            switch (cmd) {
                case "init":
                    command = new InitCommand();
                    command.execute(args);
                    return;
                case "run":
                    command = new RunCommand();
                    command.execute(args);
                    return;
                case "check":
                    command = new CheckCommand();
                    command.execute(args);
                    return;
                case "test":
                    command = new TestCommand();
                    command.execute(args);
                    return;
                case "version":
                case "-v":
                case "--version":
                    printVersion();
                    return;
                case "help":
                case "-h":
                case "--help":
                    printHelp();
                    return;
                default:
                    // Chạy file trực tiếp
                    if (args.length > 1) {
                        System.out.println("Cách dùng: java Main [đường_dẫn_tới_file.nova]");
                        System.out.println("Gõ 'nova help' để xem thêm.");
                        System.exit(64);
                    } else {
                        RunCommand runCmd = new RunCommand();
                        runCmd.runFile(args[0]);
                    }
                    return;
            }
        }

        System.out.println("Vui lòng cung cấp một lệnh hoặc đường dẫn tới file .nova để thực thi.");
        System.out.println("Ví dụ: java Main hello.nova");
        System.out.println("Gõ 'nova help' để xem các lệnh khả dụng.");
    }

    private static void printVersion() {
        System.out.println("Nova Language v1.0.0 - Interpreter");
    }

    private static void printHelp() {
        System.out.println("Nova Language CLI - Cách dùng:");
        System.out.println("  nova init       : Khởi tạo một dự án Nova mới (tạo cấu trúc thư mục và file cơ bản).");
        System.out.println("  nova run        : Chạy toàn bộ dự án từ file src/main.nova.");
        System.out.println("  nova check      : Kiểm tra lỗi cú pháp và định danh (không thực thi code).");
        System.out.println("  nova test       : Chạy các file kiểm thử (kết thúc bằng _test.nova) trong thư mục src/.");
        System.out.println("  nova version    : Hiển thị phiên bản Nova hiện tại.");
        System.out.println("  nova help       : Hiển thị hướng dẫn này.");
        System.out.println("  nova <file>     : Chạy một file .nova độc lập.");
    }
}