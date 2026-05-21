package verification;

public class Verify09Loops {
    public static void main(String[] args) {
        String input = """
                // 1. Infinite loops & Expressions
                loop {
                    break 10
                }
                lặp {
                    dừng 10
                }

                // 2. Conditional loops
                loop x < 10 {
                    x = x + 1
                }
                lặp x < 10 {
                    x = x + 1
                }

                // 3. For-each loops
                for n of numbers {
                    println(n)
                }
                duyệt n của numbers {
                    in_dòng_mới(n)
                }

                // 4. Range-based loops (English symbol ranges)
                for i in 0..10 {
                    continue
                }
                for i in 1..=5 {
                    break
                }

                // 5. Range-based loops (Vietnamese word ranges)
                duyệt i từ 0 đến 10 {
                    tiếp
                }
                duyệt i từ 1 đến_hết 5 {
                    dừng
                }
                """;
        VerifyHelper.verify("09 Loops & Control Flow", input);
    }
}
