package nova.lexer;

public record Token(TokenType type, String lexeme) {

    @Override
    public String toString() {
        return type + " -> " + lexeme;
    }
}
