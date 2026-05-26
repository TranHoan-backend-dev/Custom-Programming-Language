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
                        System.out.println("Usage: java Main [path_to_file.nova]");
                        System.out.println("Type 'nova help' to see more.");
                        System.exit(64);
                    } else {
                        RunCommand runCmd = new RunCommand();
                        runCmd.runFile(args[0]);
                    }
                    return;
            }
        }

        System.out.println("Please provide a command or a path to a .nova file to execute.");
        System.out.println("Example: java Main hello.nova");
        System.out.println("Type 'nova help' to see available commands.");
    }

    private static void printVersion() {
        System.out.println("Nova Language v1.0.0 - Interpreter");
    }

    private static void printHelp() {
        System.out.println("Nova Language CLI - How to use:");
        System.out.println("  nova init       : Initialize new Nova project (creating structure and basic files).");
        System.out.println("  nova run        : Run project from src/main.nova.");
        System.out.println("  nova check      : Check syntax and identifier errors (without executing code).");
        System.out.println("  nova test       : Run test files (ending with _test.nova) in src/ directory.");
        System.out.println("  nova version    : Display the current Nova version.");
        System.out.println("  nova help       : Show this help guide.");
        System.out.println("  nova <file>     : Run an independent .nova file.");
    }
}