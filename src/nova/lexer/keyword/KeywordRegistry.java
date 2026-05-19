package nova.lexer.keyword;

import nova.lexer.TokenType;

import java.util.HashMap;
import java.util.Map;

public class KeywordRegistry {
    private final Map<String, Keyword> registry = new HashMap<>();

    public void registry(
            String word,
            String locale,
            TokenType type
    ) {
        registry.put(word, new Keyword(word, locale, type));
    }

    public Keyword getKeyword(String word) {
        return registry.get(word);
    }
}
