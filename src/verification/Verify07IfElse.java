package verification;

public class Verify07IfElse {
    public static void main(String[] args) {
        String input = """
                // English If-Else Statements & Expressions
                if (x > 5) {
                    var y = 10
                } else if (x == 5) {
                    var y = 5
                } else {
                    var y = 0
                }
                var valEn = if (cond) { 10 } else { 20 }

                // Vietnamese If-Else Statements & Expressions
                nếu (x > 5) thì {
                    biến y = 10
                } còn_nếu (x == 5) thì {
                    biến y = 5
                } không_thì {
                    biến y = 0
                }
                biến valVi = nếu (cond) thì { 10 } không_thì { 20 }
                """;
        VerifyHelper.verify("07 If-Else (Statements & Expressions)", input);
    }
}
