package nova.lexer;

import nova.lexer.keyword.DefaultKeyword;
import nova.lexer.keyword.KeywordRegistry;

public class Lexer {
    private final String src;
    private int pos = 0;
    private static final KeywordRegistry registry = new KeywordRegistry();

    static {
        DefaultKeyword.load(registry);
    }

    public Lexer(String src) {
        this.src = src;
    }
}
