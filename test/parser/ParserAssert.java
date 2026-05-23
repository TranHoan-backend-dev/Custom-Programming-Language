package nova.parser;

/**
 * Lớp hỗ trợ kiểm thử tự động cho Parser, cung cấp các hàm so sánh (assertion) cơ bản.
 * Nếu điều kiện kiểm tra thất bại, một lỗi {@link AssertionError} sẽ được ném ra để đánh dấu ca kiểm thử không thành công.
 * 
 * @author XUAN HOAN
 */
public class ParserAssert {

    /**
     * Kiểm tra tính bằng nhau của hai đối tượng.
     * Nếu giá trị thực tế không bằng giá trị kỳ vọng, phương thức sẽ ném ra {@link AssertionError}.
     * 
     * @param expected Giá trị kỳ vọng mong muốn nhận được
     * @param actual Giá trị thực tế thu được trong quá trình chạy
     * @throws AssertionError nếu hai giá trị không bằng nhau
     */
    public static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError("KỲ VỌNG: <" + expected + "> NHƯNG NHẬN ĐƯỢC: <" + actual + ">");
    }

    /**
     * Kiểm tra một điều kiện logic phải có giá trị là đúng (true).
     * Nếu điều kiện có giá trị là sai (false), phương thức sẽ ném ra {@link AssertionError}.
     * 
     * @param condition Điều kiện logic cần kiểm tra
     * @throws AssertionError nếu điều kiện là sai (false)
     */
    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Kỳ vọng điều kiện là ĐÚNG nhưng thực tế là SAI");
        }
    }
}
