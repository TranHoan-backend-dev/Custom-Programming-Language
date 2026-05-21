package verification;

public class Verify02Output {
    public static void main(String[] args) {
        String input = """
                // English
                var name = "An"
                var age = 18
                println("Hello {name}, you are {age} years old.")
                println("Hello {}, you are {} years old.", name, age)

                // Tiếng Việt
                biến ten = "An"
                biến tuoi = 18
                in_dòng_mới("Chào {ten}, bạn {tuoi} tuổi.")
                in_dòng_mới("Chào {}, bạn {} tuổi.", ten, tuoi)
                """;
        VerifyHelper.verify("02 Output & String Interpolation", input);
    }
}
