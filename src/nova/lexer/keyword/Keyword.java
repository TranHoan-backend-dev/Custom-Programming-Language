package nova.lexer.keyword;

import nova.lexer.TokenType;

/**
 * Định nghĩa từ khóa
 * @param word Nội dung từ khóa
 * @param local Ngôn ngữ của từ khóa
 * @param type Loại token của từ khoá đó
 */
public record Keyword(
        String word,
        String local,
        TokenType type
) {
}
