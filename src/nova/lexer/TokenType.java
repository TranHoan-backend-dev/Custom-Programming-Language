package nova.lexer;

public enum TokenType {
    // <editor-fold> desc="==================== Kieu du lieu ===================="
    // Tự suy luận
    IDENTIFIER,

    // <editor-fold> desc="Chuỗi"
    TYPE_STRING,
    TYPE_CHAR,
    // </editor-fold>

    // Đúng/Sai
    TYPE_BOOLEAN,

    // <editor-fold> desc="Số"
    TYPE_INT,
    TYPE_INT16,
    TYPE_INT32,
    TYPE_INT64,
    TYPE_DOUBLE,
    TYPE_FLOAT,
    // </editor-fold>

    VOID, // Trống
    NULL,
    // </editor-fold>

    // <editor-fold> desc="Gia tri (Literal)"
    // <editor-fold> desc="Chuỗi"
    LITERAL_STRING,
    LITERAL_CHAR,
    // </editor-fold>

    // Đúng/Sai
    LITERAL_BOOLEAN,

    // <editor-fold> desc="Số"
    LITERAL_INTEGER,
    LITERAL_DOUBLE,
    LITERAL_FLOAT,
    // </editor-fold>
    // </editor-fold>

    // <editor-fold> desc="==================== Keyword ===================="
    VAR, // Kiểu dữ liệu tự suy luận
    MUT, // Mutable
    CONST, // Hằng số
    FUNCTION, // Từ khóa khai báo hàm (tùy chọn)
    RETURN, // Từ khóa trả về giá trị của hàm

    // <editor-fold> desc="If statement"
    IF,
    THEN, // thì
    ELSE,
    ELSE_IF, // còn_nếu
    OR_NOT, // hoặc không
    // </editor-fold>

    SWITCH, // Switch case

    // <editor-fold> desc="Loop"
    LOOP, // Vòng lặp
    FOR,
    OF,
    IN, // từ
    BREAK, // dừng
    CONTINUE, // tiếp
    // </editor-fold>

    // Boolean keywords
    TRUE,  // đúng
    FALSE, // sai
    // </editor-fold>

    // <editor-fold> desc="==================== Toán tử ===================="
    ASSIGN, // Gán
    PLUS, // Dấu cộng
    MINUS, // Dấu trừ
    STAR, // Dấu nhân
    SLASH, // Dấu chia
    PERCENTAGE, // Dấu chia lấy dư

    EQUAL, // ==
    NOT_EQUAL, // !=

    GREATER_THAN, // >
    GREATER_THAN_EQUAL, // >=
    LESS_THAN, // <
    LESS_THAN_EQUAL, // <=

    AND, // &&
    OR, // |

    NOT, // !
    ARROW, // ->
    RANGE_EXCLUSIVE, // .. hoặc đến
    RANGE_INCLUSIVE, // ..= hoặc đến_hết

    // Null safety
    QUESTION,        // ?
    // </editor-fold>

    // <editor-fold> desc="==================== Delimiters ===================="
    // <editor-fold> desc="()"
    LEFT_PAREN, // (
    RIGHT_PAREN, // )
    // </editor-fold>

    // <editor-fold> desc="{}"
    LEFT_RACE, // {
    RIGHT_RACE, // }
    // </editor-fold>

    // <editor-fold> desc="[]"
    LEFT_BRACKET, // [
    RIGHT_BRACKET, // ]
    // </editor-fold>
    // </editor-fold>

    // <editor-fold> desc="==================== Khác ===================="
    COMMA, // Dấu phẩy
    DOT, // Dấu chấm
    COLON, // Dấu 2 chấm
    SEMICOLON, // Dấu chấm phẩy

    EOF,
    // </editor-fold>
}
