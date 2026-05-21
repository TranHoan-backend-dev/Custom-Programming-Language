package verification;

public class Verify05VariablesConstants {
    public static void main(String[] args) {
        String input = """
                // English variable declarations
                var x = 10
                int y = 20
                var mut mutableVar = 30
                int mut mutableInt = 40
                const string MY_CONST = "hello"

                // Vietnamese variable declarations
                biến x_vi = 10
                số_nguyên y_vi = 20
                biến khả_biến bien_kha_bien = 30
                số_nguyên khả_biến so_kha_bien = 40
                hằng_số chuỗi HANG_SO = "xin chào"

                // Block scopes and shadowing
                if (true) {
                    var x = 99
                    in_dòng_mới(x)
                }
                nếu (đúng) thì {
                    biến x_vi = 99
                    in_dòng_mới(x_vi)
                }
                """;
        VerifyHelper.verify("05 Variables, Constants & Scopes", input);
    }
}
