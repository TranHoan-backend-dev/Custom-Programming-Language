package verification;

import nova.lexer.Lexer;
import nova.lexer.Token;
import nova.lexer.TokenType;
import nova.lexer.LexerError;

public class VerifyHelper {
    public static void verify(String title, String input) {
        System.out.println("================================================================================");
        System.out.println("VERIFICATION: " + title);
        System.out.println("-------------------------------- Mã nguồn ----------------------------------");
        System.out.println(input);
        System.out.println("---------------------------------- Tokens ----------------------------------");
        
        Lexer lexer = new Lexer(input);
        int count = 0;
        try {
            while (true) {
                Token token = lexer.nextToken();
                count++;
                System.out.printf("%3d: Type = %-20s | Lexeme = \"%s\"\n", count, token.type(), escape(token.lexeme()));
                if (token.type() == TokenType.EOF) {
                    break;
                }
            }
            System.out.println("-------------------------------- Kế thúc ----------------------------------");
            System.out.println("Trạng thái: THÀNH CÔNG");
        } catch (LexerError e) {
            System.out.println("----------------------------------------------------------------------------");
            System.err.println("LỖI PHÂN TÍCH TỪ VỰNG: " + e.getMessage());
            System.out.println("Trạng thái: THẤT BẠI");
        }
        System.out.println("================================================================================\n");
    }

    private static String escape(String lexeme) {
        return lexeme.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
