package nova.repl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nova.lexer.Lexer;
import nova.lexer.Token;
import nova.lexer.TokenType;
import nova.parser.Parser;
import nova.interpreter.Interpreter;
import nova.ast.Stmt;
import nova.interpreter.exception.RuntimeError;

import nova.utils.NovaLogger;

/**
 * Read-Eval-Print Loop (REPL) cho ngôn ngữ Nova.
 */
public class NovaRepl {
    private static final String PROMPT = ">>> ";
    private static final String MULTILINE_PROMPT = "... ";
    private static final String HELP_TEXT = """
            Các lệnh nội bộ của Nova REPL:
              :help   – Hiển thị trợ giúp này
              :exit   – Thoát REPL
              :reset  – Xóa toàn bộ biến môi trường hiện hành
              :vars   – Hiển thị danh sách các biến đang có trong bộ nhớ""";

    private final Interpreter interpreter;
    private final InputStream in;
    private final PrintStream out;

    public NovaRepl() {
        this(System.in, System.out);
    }

    public NovaRepl(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
        this.interpreter = new Interpreter(out);
    }

    public void start() {
        out.println("Nova REPL v1.0. Gõ :help để xem trợ giúp, :exit để thoát.");
        var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        var sourceBuilder = new StringBuilder();

        try {
            while (true) {
                if (sourceBuilder.isEmpty()) {
                    out.print(PROMPT);
                } else {
                    out.print(MULTILINE_PROMPT);
                }

                String line = reader.readLine();
                if (line == null) { // EOF (e.g., Ctrl+D)
                    break;
                }

                String trimmed = line.trim();

                if (sourceBuilder.isEmpty() && trimmed.startsWith(":")) {
                    if (handleInternalCommand(trimmed)) {
                        continue;
                    } else if (trimmed.equals(":exit")) {
                        out.println("Tạm biệt!");
                        break;
                    }
                }

                if (trimmed.endsWith("\\")) {
                    sourceBuilder.append(trimmed, 0, trimmed.length() - 1).append("\n");
                    continue;
                }

                sourceBuilder.append(line).append("\n");

                // Đoán xem câu lệnh đã hoàn chỉnh chưa dựa trên dấu ngoặc
                if (!isBalanced(sourceBuilder.toString())) {
                    continue;
                }

                String source = sourceBuilder.toString();
                runSource(source);
                sourceBuilder.setLength(0); // clear for next input
            }
        } catch (Exception e) {
            out.println("Lỗi REPL: " + e.getMessage());
            NovaLogger.error("Lỗi REPL", e);
        }
    }

    private boolean handleInternalCommand(String command) {
        switch (command) {
            case ":help":
                out.println(HELP_TEXT);
                return true;
            case ":vars":
                Map<String, Object> globals = interpreter.getGlobals();
                boolean hasVars = false;
                for (var value : globals.values()) {
                    if (!(value instanceof nova.interpreter.NovaCallable)) {
                        hasVars = true;
                        break;
                    }
                }

                if (!hasVars) {
                    out.println("Không có biến nào.");
                } else {
                    out.println("Các biến toàn cục:");
                    for (Map.Entry<String, Object> entry : globals.entrySet()) {
                        if (!(entry.getValue() instanceof nova.interpreter.NovaCallable)) {
                            out.println("  " + entry.getKey() + " = " + Interpreter.stringify(entry.getValue()));
                        }
                    }
                }
                return true;
            case ":reset":
                interpreter.reset();
                out.println("Đã xóa môi trường.");
                return true;
            case ":exit":
                return false; // Được xử lý ở vòng lặp
            default:
                out.println("Lệnh không hợp lệ. Gõ :help để xem danh sách lệnh.");
                return true;
        }
    }

    private void runSource(String source) {
        if (source.trim().isEmpty()) return;
        try {
            var lexer = new Lexer(source);
            List<Token> tokens = new ArrayList<>();
            while (true) {
                Token token = lexer.nextToken();
                tokens.add(token);
                if (token.type() == TokenType.EOF) {
                    break;
                }
            }
            var parser = new Parser(tokens);
            List<Stmt> statements = parser.parse();
            // Xử lý in kết quả trực tiếp cho REPL nếu là ExpressionStmt
            if (statements.size() == 1 && statements.getFirst() instanceof Stmt.Expression) {
                var result = interpreter.evaluateExpressionForRepl(((Stmt.Expression) statements.getFirst()).expression);
                if (result != null) {
                    out.println(Interpreter.stringify(result));
                }
            } else {
                interpreter.interpret(statements);
            }
        } catch (RuntimeError e) {
            out.println("[Runtime Error] " + e.getMessage());
            NovaLogger.error("REPL Runtime Error: " + e.getMessage(), e);
        } catch (Exception e) {
            out.println("[Lỗi] " + e.getMessage());
            NovaLogger.error("REPL Exception", e);
        }
    }

    private boolean isBalanced(String text) {
        var parens = 0;
        var braces = 0;
        var brackets = 0;
        var inString = false;
        var escape = false;

        for (var i = 0; i < text.length(); i++) {
            var c = text.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (c == '"') {
                inString = !inString;
                continue;
            }
            if (inString) continue;

            if (c == '(') parens++;
            else if (c == ')') parens--;
            else if (c == '{') braces++;
            else if (c == '}') braces--;
            else if (c == '[') brackets++;
            else if (c == ']') brackets--;
        }
        return parens <= 0 && braces <= 0 && brackets <= 0;
    }

    public static void main(String[] args) {
        new NovaRepl().start();
    }
}
