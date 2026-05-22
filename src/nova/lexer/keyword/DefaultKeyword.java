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
        booleanLiteral(registry);
        integer(registry);
        doubleType(registry);
        floatType(registry);
        voidType(registry);
        nullType(registry);
        mut(registry);
        constType(registry);
        ifType(registry);
        thenType(registry);
        elseType(registry);
        elseIfType(registry);
        orNot(registry);
        switchType(registry);
        loop(registry);
        forType(registry);
        of(registry);
        functionType(registry);
        inType(registry);
        rangeExclusive(registry);
        rangeInclusive(registry);
        breakType(registry);
        continueType(registry);
        returnType(registry);
        assign(registry);
        plus(registry);
        minus(registry);
        star(registry);
        slash(registry);
        percentage(registry);
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
        arrow(registry);
    }

    /**
     * Biến tự suy luận
     *
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
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void string(KeywordRegistry registry) {
        registry.registry(
                "string",
                EN,
                TokenType.TYPE_STRING
        );
        registry.registry(
                "chuỗi",
                VI,
                TokenType.TYPE_STRING
        );
    }

    /**
     * Kiểu dữ liệu ký tự
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void charType(KeywordRegistry registry) {
        registry.registry(
                "char",
                EN,
                TokenType.TYPE_CHAR
        );
        registry.registry(
                "ký_tự",
                VI,
                TokenType.TYPE_CHAR
        );
    }

    /**
     * Kiểu dữ liệu logic (đúng/sai)
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void booleanType(KeywordRegistry registry) {
        registry.registry(
                "boolean",
                EN,
                TokenType.TYPE_BOOLEAN
        );
        registry.registry(
                "logic",
                VI,
                TokenType.TYPE_BOOLEAN
        );
    }

    /**
     * Hằng số logic (đúng/sai)
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void booleanLiteral(KeywordRegistry registry) {
        registry.registry(
                "true",
                EN,
                TokenType.TRUE
        );
        registry.registry(
                "false",
                EN,
                TokenType.FALSE
        );
        registry.registry(
                "đúng",
                VI,
                TokenType.TRUE
        );
        registry.registry(
                "sai",
                VI,
                TokenType.FALSE
        );
    }

    /**
     * Kiểu dữ liệu số nguyên
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void integer(KeywordRegistry registry) {
        registry.registry(
                "int",
                EN,
                TokenType.TYPE_INT
        );
        registry.registry(
                "int16",
                EN,
                TokenType.TYPE_INT16
        );
        registry.registry(
                "int32",
                EN,
                TokenType.TYPE_INT32
        );
        registry.registry(
                "int64",
                EN,
                TokenType.TYPE_INT64
        );
        registry.registry(
                "số_nguyên",
                VI,
                TokenType.TYPE_INT
        );
        registry.registry(
                "số_nguyên_16",
                VI,
                TokenType.TYPE_INT16
        );
        registry.registry(
                "số_nguyên_32",
                VI,
                TokenType.TYPE_INT32
        );
        registry.registry(
                "số_nguyên_64",
                VI,
                TokenType.TYPE_INT64
        );
    }

    /**
     * Kiểu dữ liệu số thực kép
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void doubleType(KeywordRegistry registry) {
        registry.registry(
                "double",
                EN,
                TokenType.TYPE_DOUBLE
        );
        registry.registry(
                "số_thực_kép",
                VI,
                TokenType.TYPE_DOUBLE
        );
    }

    /**
     * Kiểu dữ liệu số thực đơn
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void floatType(KeywordRegistry registry) {
        registry.registry(
                "float",
                EN,
                TokenType.TYPE_FLOAT
        );
        registry.registry(
                "số_thực_đơn",
                VI,
                TokenType.TYPE_FLOAT
        );
    }

    /**
     * Kiểu rỗng (trống)
     *
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
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void nullType(KeywordRegistry registry) {
        registry.registry(
                "null",
                EN,
                TokenType.NULL
        );
        registry.registry(
                "k_tồn_tại",
                VI,
                TokenType.NULL
        );
    }

    /**
     * Từ khóa khai báo biến khả biến
     *
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
     *
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
     *
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
     *
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
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void elseType(KeywordRegistry registry) {
        registry.registry(
                "else",
                EN,
                TokenType.ELSE
        );
        registry.registry(
                "không_thì",
                VI,
                TokenType.ELSE
        );
    }

    private static void elseIfType(KeywordRegistry registry) {
        registry.registry(
                "còn_nếu",
                VI,
                TokenType.ELSE_IF
        );
    }

    /**
     * Cấu trúc điều kiện hoặc không
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void orNot(KeywordRegistry registry) {
        registry.registry(
                "or_not",
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     * Phép chia lấy dư
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void percentage(KeywordRegistry registry) {
        registry.registry(
                "%",
                EN,
                TokenType.PERCENTAGE
        );
        registry.registry(
                "%",
                VI,
                TokenType.PERCENTAGE
        );
    }

    /**
     * Phép so sánh bằng
     *
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
     *
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
        registry.registry(
                "is_not",
                EN,
                TokenType.NOT_EQUAL
        );
        registry.registry(
                "khác",
                VI,
                TokenType.NOT_EQUAL
        );
    }

    /**
     * Phép so sánh lớn hơn
     *
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
     *
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
     *
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
     *
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
     *
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
        registry.registry(
                "and",
                EN,
                TokenType.AND
        );
        registry.registry(
                "và",
                VI,
                TokenType.AND
        );
    }

    /**
     * Phép toán logic HOẶC
     *
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
        registry.registry(
                "or",
                EN,
                TokenType.OR
        );
        registry.registry(
                "hoặc",
                VI,
                TokenType.OR
        );
    }

    /**
     * Phép toán logic PHỦ ĐỊNH
     *
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
        registry.registry(
                "not",
                EN,
                TokenType.NOT
        );
        registry.registry(
                "không",
                VI,
                TokenType.NOT
        );
    }

    /**
     * Dấu gạch ngang
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void hyphen(KeywordRegistry registry) {
        registry.registry(
                "-",
                EN,
                TokenType.ARROW
        );
        registry.registry(
                "-",
                VI,
                TokenType.ARROW
        );
    }

    /**
     * Dấu mở ngoặc đơn
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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

    /**
     * Dấu mũi tên
     *
     * @param registry object lưu trữ từ khóa và token của nó
     */
    private static void arrow(KeywordRegistry registry) {
        registry.registry(
                "->",
                EN,
                TokenType.ARROW
        );
        registry.registry(
                "->",
                VI,
                TokenType.ARROW
        );
    }

    private static void functionType(KeywordRegistry registry) {
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

    private static void inType(KeywordRegistry registry) {
        registry.registry(
                "in",
                EN,
                TokenType.IN
        );
        registry.registry(
                "từ",
                VI,
                TokenType.IN
        );
    }

    private static void rangeExclusive(KeywordRegistry registry) {
        registry.registry(
                "đến",
                VI,
                TokenType.RANGE_EXCLUSIVE
        );
    }

    private static void rangeInclusive(KeywordRegistry registry) {
        registry.registry(
                "đến_hết",
                VI,
                TokenType.RANGE_INCLUSIVE
        );
    }

    private static void breakType(KeywordRegistry registry) {
        registry.registry(
                "break",
                EN,
                TokenType.BREAK
        );
        registry.registry(
                "dừng",
                VI,
                TokenType.BREAK
        );
    }

    private static void continueType(KeywordRegistry registry) {
        registry.registry(
                "continue",
                EN,
                TokenType.CONTINUE
        );
        registry.registry(
                "tiếp",
                VI,
                TokenType.CONTINUE
        );
    }

    private static void returnType(KeywordRegistry registry) {
        registry.registry(
                "return",
                EN,
                TokenType.RETURN
        );
        registry.registry(
                "trả_về",
                VI,
                TokenType.RETURN
        );
    }
}
