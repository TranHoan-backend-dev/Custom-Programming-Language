package verification;

public class Verify10Functions {
    public static void main(String[] args) {
        String input = """
                // 1. English function declarations (with & without function keyword)
                greet() -> void {
                    println("Hello!")
                }
                function add(int a, int b) -> int {
                    return a + b
                }
                function divAndMod(int a, int b) -> (int quotient, int remainder) {
                    return (a / b, a % b)
                }

                // 2. Vietnamese function declarations (with & without hàm keyword)
                chaoHoi() -> trống {
                    in_dòng_mới("Xin chào!")
                }
                hàm tổng(số_nguyên a, số_nguyên b) -> số_nguyên {
                    trả_về a + b
                }
                hàm chiaCóDư(số_nguyên a, số_nguyên b) -> (số_nguyên thương, số_nguyên dư) {
                    trả_về (a / b, a % b)
                }
                """;
        VerifyHelper.verify("10 Functions & Returns", input);
    }
}
