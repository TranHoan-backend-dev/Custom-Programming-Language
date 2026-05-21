package verification;

public class Verify08SwitchCase {
    public static void main(String[] args) {
        String input = """
                // English Switch-Case Statements & Expressions
                switch (x) {
                    1 | 3 -> "Odd"
                    2 | 4 -> "Even"
                    _ -> "Other"
                }
                var resEn = switch (x) {
                    1 -> "One"
                    _ -> "Many"
                }

                // Vietnamese Switch-Case Statements & Expressions
                trường_hợp (x) {
                    1 | 3 -> "Lẻ"
                    2 | 4 -> "Chẵn"
                    _ -> "Khác"
                }
                biến resVi = trường_hợp (x) {
                    1 -> "Một"
                    _ -> "Nhiều"
                }
                """;
        VerifyHelper.verify("08 Switch-Case / Match", input);
    }
}
