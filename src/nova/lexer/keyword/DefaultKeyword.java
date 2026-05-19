package nova.lexer.keyword;

import nova.lexer.TokenType;

public class DefaultKeyword {
    private static final String EN = "en";
    private static final String VI = "vi";

    public static void load(KeywordRegistry registry) {
        identifier(registry);
        string(registry);
        charType(registry);
        booleanType(registry);
        integer(registry);
        doubleType(registry);
        floatType(registry);
        voidType(registry);
        nullType(registry);
        function(registry);
        mut(registry);
        constType(registry);
        ifType(registry);
        thenType(registry);
        elseType(registry);
        orNot(registry);
        switchType(registry);
        loop(registry);
        forType(registry);
        of(registry);
        assign(registry);
        plus(registry);
        minus(registry);
        star(registry);
        slash(registry);
        equal(registry);
        notEqual(registry);
        greaterThan(registry);
        greaterThanEqual(registry);
        lessThan(registry);
        lessThanEqual(registry);
        and(registry);
        or(registry);
        not(registry);
        hyphen(registry);
        leftParen(registry);
        rightParen(registry);
        leftRace(registry);
        rightRace(registry);
        leftBracket(registry);
        rightBracket(registry);
        comma(registry);
        dot(registry);
        colon(registry);
    }

    /**
     * Biến tự suy luận
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void identifier(KeywordRegistry registry) {
        registry.registry(
                "var",
                EN,
                TokenType.VAR
        );
        registry.registry(
                "biến",
                VI,
                TokenType.VAR
        );
    }

    /**
     * Kiểu dữ liệu chuỗi
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void string(KeywordRegistry registry) {
        registry.registry(
                "string",
                EN,
                TokenType.STRING
        );
        registry.registry(
                "chuỗi",
                VI,
                TokenType.STRING
        );
    }

    /**
     * Kiểu dữ liệu ký tự
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void charType(KeywordRegistry registry) {
        registry.registry(
                "char",
                EN,
                TokenType.CHAR
        );
        registry.registry(
                "ký_tự",
                VI,
                TokenType.CHAR
        );
    }

    /**
     * Kiểu dữ liệu logic (đúng/sai)
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void booleanType(KeywordRegistry registry) {
        registry.registry(
                "boolean",
                EN,
                TokenType.BOOLEAN
        );
        registry.registry(
                "boolean",
                VI,
                TokenType.BOOLEAN
        );
    }

    /**
     * Kiểu dữ liệu số nguyên
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void integer(KeywordRegistry registry) {
        registry.registry(
                "int",
                EN,
                TokenType.INTEGER
        );
        registry.registry(
                "số_nguyên",
                VI,
                TokenType.INTEGER
        );
    }

    /**
     * Kiểu dữ liệu số thực kép
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void doubleType(KeywordRegistry registry) {
        registry.registry(
                "double",
                EN,
                TokenType.DOUBLE
        );
        registry.registry(
                "số_thực_kép",
                VI,
                TokenType.DOUBLE
        );
    }

    /**
     * Kiểu dữ liệu số thực đơn
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void floatType(KeywordRegistry registry) {
        registry.registry(
                "float",
                EN,
                TokenType.FLOAT
        );
        registry.registry(
                "số_thực_đơn",
                VI,
                TokenType.FLOAT
        );
    }

    /**
     * Kiểu rỗng (trống)
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void voidType(KeywordRegistry registry) {
        registry.registry(
                "void",
                EN,
                TokenType.VOID
        );
        registry.registry(
                "trống",
                VI,
                TokenType.VOID
        );
    }

    /**
     * Giá trị rỗng (null)
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void nullType(KeywordRegistry registry) {
        registry.registry(
                "null",
                EN,
                TokenType.NULL
        );
        registry.registry(
                "rỗng",
                VI,
                TokenType.NULL
        );
    }

    /**
     * Khai báo hàm
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void function(KeywordRegistry registry) {
        registry.registry(
                "function",
                EN,
                TokenType.FUNCTION
        );
        registry.registry(
                "hàm",
                VI,
                TokenType.FUNCTION
        );
    }

    /**
     * Từ khóa khai báo biến khả biến
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void mut(KeywordRegistry registry) {
        registry.registry(
                "mut",
                EN,
                TokenType.MUT
        );
        registry.registry(
                "khả_biến",
                VI,
                TokenType.MUT
        );
    }

    /**
     * Từ khóa khai báo hằng số
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void constType(KeywordRegistry registry) {
        registry.registry(
                "const",
                EN,
                TokenType.CONST
        );
        registry.registry(
                "hằng_số",
                VI,
                TokenType.CONST
        );
    }

    /**
     * Cấu trúc điều kiện nếu
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void ifType(KeywordRegistry registry) {
        registry.registry(
                "if",
                EN,
                TokenType.IF
        );
        registry.registry(
                "nếu",
                VI,
                TokenType.IF
        );
    }

    /**
     * Cấu trúc điều kiện thì
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void thenType(KeywordRegistry registry) {
        registry.registry(
                "then",
                EN,
                TokenType.THEN
        );
        registry.registry(
                "thì",
                VI,
                TokenType.THEN
        );
    }

    /**
     * Cấu trúc điều kiện khác
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void elseType(KeywordRegistry registry) {
        registry.registry(
                "else",
                EN,
                TokenType.ELSE
        );
        registry.registry(
                "còn_nếu",
                VI,
                TokenType.ELSE
        );
    }

    /**
     * Cấu trúc điều kiện hoặc không
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void orNot(KeywordRegistry registry) {
        registry.registry(
                "or not",
                EN,
                TokenType.OR_NOT
        );
        registry.registry(
                "hoặc_không",
                VI,
                TokenType.OR_NOT
        );
    }

    /**
     * Cấu trúc rẽ nhánh
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void switchType(KeywordRegistry registry) {
        registry.registry(
                "switch",
                EN,
                TokenType.SWITCH
        );
        registry.registry(
                "trường_hợp",
                VI,
                TokenType.SWITCH
        );
    }

    /**
     * Vòng lặp
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void loop(KeywordRegistry registry) {
        registry.registry(
                "loop",
                EN,
                TokenType.LOOP
        );
        registry.registry(
                "lặp",
                VI,
                TokenType.LOOP
        );
    }

    /**
     * Vòng lặp cho
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void forType(KeywordRegistry registry) {
        registry.registry(
                "for",
                EN,
                TokenType.FOR
        );
        registry.registry(
                "duyệt",
                VI,
                TokenType.FOR
        );
    }

    /**
     * Từ khóa sử dụng trong vòng for, dùng để chỉ định danh sách được duyệt
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void of(KeywordRegistry registry) {
        registry.registry(
                "of",
                EN,
                TokenType.OF
        );
        registry.registry(
                "của",
                VI,
                TokenType.OF
        );
    }

    /**
     * Phép gán
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void assign(KeywordRegistry registry) {
        registry.registry(
                "=",
                EN,
                TokenType.ASSIGN
        );
        registry.registry(
                "=",
                VI,
                TokenType.ASSIGN
        );
    }

    /**
     * Phép cộng
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void plus(KeywordRegistry registry) {
        registry.registry(
                "+",
                EN,
                TokenType.PLUS
        );
        registry.registry(
                "+",
                VI,
                TokenType.PLUS
        );
    }

    /**
     * Phép trừ
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void minus(KeywordRegistry registry) {
        registry.registry(
                "-",
                EN,
                TokenType.MINUS
        );
        registry.registry(
                "-",
                VI,
                TokenType.MINUS
        );
    }

    /**
     * Phép nhân
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void star(KeywordRegistry registry) {
        registry.registry(
                "*",
                EN,
                TokenType.STAR
        );
        registry.registry(
                "*",
                VI,
                TokenType.STAR
        );
    }

    /**
     * Phép chia
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void slash(KeywordRegistry registry) {
        registry.registry(
                "/",
                EN,
                TokenType.SLASH
        );
        registry.registry(
                "/",
                VI,
                TokenType.SLASH
        );
    }

    /**
     * Phép so sánh bằng
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void equal(KeywordRegistry registry) {
        registry.registry(
                "==",
                EN,
                TokenType.EQUAL
        );
        registry.registry(
                "==",
                VI,
                TokenType.EQUAL
        );
    }

    /**
     * Phép so sánh không bằng
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void notEqual(KeywordRegistry registry) {
        registry.registry(
                "!=",
                EN,
                TokenType.NOT_EQUAL
        );
        registry.registry(
                "!=",
                VI,
                TokenType.NOT_EQUAL
        );
    }

    /**
     * Phép so sánh lớn hơn
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void greaterThan(KeywordRegistry registry) {
        registry.registry(
                ">",
                EN,
                TokenType.GREATER_THAN
        );
        registry.registry(
                ">",
                VI,
                TokenType.GREATER_THAN
        );
    }

    /**
     * Phép so sánh lớn hơn hoặc bằng
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void greaterThanEqual(KeywordRegistry registry) {
        registry.registry(
                ">=",
                EN,
                TokenType.GREATER_THAN_EQUAL
        );
        registry.registry(
                ">=",
                VI,
                TokenType.GREATER_THAN_EQUAL
        );
    }

    /**
     * Phép so sánh nhỏ hơn
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void lessThan(KeywordRegistry registry) {
        registry.registry(
                "<",
                EN,
                TokenType.LESS_THAN
        );
        registry.registry(
                "<",
                VI,
                TokenType.LESS_THAN
        );
    }

    /**
     * Phép so sánh nhỏ hơn hoặc bằng
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void lessThanEqual(KeywordRegistry registry) {
        registry.registry(
                "<=",
                EN,
                TokenType.LESS_THAN_EQUAL
        );
        registry.registry(
                "<=",
                VI,
                TokenType.LESS_THAN_EQUAL
        );
    }

    /**
     * Phép toán logic VÀ
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void and(KeywordRegistry registry) {
        registry.registry(
                "&&",
                EN,
                TokenType.AND
        );
        registry.registry(
                "&&",
                VI,
                TokenType.AND
        );
    }

    /**
     * Phép toán logic HOẶC
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void or(KeywordRegistry registry) {
        registry.registry(
                "||",
                EN,
                TokenType.OR
        );
        registry.registry(
                "||",
                VI,
                TokenType.OR
        );
    }

    /**
     * Phép toán logic PHỦ ĐỊNH
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void not(KeywordRegistry registry) {
        registry.registry(
                "!",
                EN,
                TokenType.NOT
        );
        registry.registry(
                "!",
                VI,
                TokenType.NOT
        );
    }

    /**
     * Dấu gạch ngang
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void hyphen(KeywordRegistry registry) {
        registry.registry(
                "-",
                EN,
                TokenType.Hyphen
        );
        registry.registry(
                "-",
                VI,
                TokenType.Hyphen
        );
    }

    /**
     * Dấu mở ngoặc đơn
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void leftParen(KeywordRegistry registry) {
        registry.registry(
                "(",
                EN,
                TokenType.LEFT_PAREN
        );
        registry.registry(
                "(",
                VI,
                TokenType.LEFT_PAREN
        );
    }

    /**
     * Dấu đóng ngoặc đơn
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void rightParen(KeywordRegistry registry) {
        registry.registry(
                ")",
                EN,
                TokenType.RIGHT_PAREN
        );
        registry.registry(
                ")",
                VI,
                TokenType.RIGHT_PAREN
        );
    }

    /**
     * Dấu mở ngoặc nhọn
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void leftRace(KeywordRegistry registry) {
        registry.registry(
                "{",
                EN,
                TokenType.LEFT_RACE
        );
        registry.registry(
                "{",
                VI,
                TokenType.LEFT_RACE
        );
    }

    /**
     * Dấu đóng ngoặc nhọn
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void rightRace(KeywordRegistry registry) {
        registry.registry(
                "}",
                EN,
                TokenType.RIGHT_RACE
        );
        registry.registry(
                "}",
                VI,
                TokenType.RIGHT_RACE
        );
    }

    /**
     * Dấu mở ngoặc vuông
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void leftBracket(KeywordRegistry registry) {
        registry.registry(
                "[",
                EN,
                TokenType.LEFT_BRACKET
        );
        registry.registry(
                "[",
                VI,
                TokenType.LEFT_BRACKET
        );
    }

    /**
     * Dấu đóng ngoặc vuông
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void rightBracket(KeywordRegistry registry) {
        registry.registry(
                "]",
                EN,
                TokenType.RIGHT_BRACKET
        );
        registry.registry(
                "]",
                VI,
                TokenType.RIGHT_BRACKET
        );
    }

    /**
     * Dấu phẩy
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void comma(KeywordRegistry registry) {
        registry.registry(
                ",",
                EN,
                TokenType.COMMA
        );
        registry.registry(
                ",",
                VI,
                TokenType.COMMA
        );
    }

    /**
     * Dấu chấm
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void dot(KeywordRegistry registry) {
        registry.registry(
                ".",
                EN,
                TokenType.DOT
        );
        registry.registry(
                ".",
                VI,
                TokenType.DOT
        );
    }

    /**
     * Dấu hai chấm
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void colon(KeywordRegistry registry) {
        registry.registry(
                ":",
                EN,
                TokenType.COLON
        );
        registry.registry(
                ":",
                VI,
                TokenType.COLON
        );
    }
}
