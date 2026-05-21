package verification;

public class Verify11Strings {
    public static void main(String[] args) {
        String input = """
                // English strings & comparisons
                var greeting = "Hello, World!"
                string name = "An"
                var ageStr = 25.to_string()
                var fullName = "Nguyen" + " " + "Van A"
                
                var eq1 = (greeting == "Hello, World!")
                var eq2 = (greeting != "hi")
                
                var len = name.length()
                var uppercase = name.toUpperCase()

                // Tiếng Việt strings & comparisons
                biến loiChao = "Xin chào!"
                chuỗi ten = "An"
                biến tuoiStr = 25.to_string()
                biến hoTen = "Nguyễn" + " " + "Văn A"
                
                biến eq3 = (loiChao == "Xin chào!")
                biến eq4 = (loiChao khác "hi")
                
                biến doDai = ten.length()
                """;
        VerifyHelper.verify("11 Strings & Comparisons", input);
    }
}
