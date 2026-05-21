package verification;

public class Verify01EntryPoint {
    public static void main(String[] args) {
        String input = """
                // Phiên bản tiếng Anh (English version)
                main() -> void {
                }

                // Phiên bản tiếng Việt (Vietnamese version)
                main() -> trống {
                }
                """;
        VerifyHelper.verify("01 Entry Point", input);
    }
}
