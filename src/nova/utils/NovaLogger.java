package nova.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Lớp tiện ích ghi log cho ngôn ngữ Nova.
 * Log sẽ được ghi vào file 'nova.log' tại thư mục hiện tại dưới dạng append.
 */
public class NovaLogger {
    private static final Logger logger = Logger.getLogger("NovaLogger");

    static {
        try {
            // Thiết lập FileHandler với chế độ append = true
            var fileHandler = new FileHandler("nova.log", true);
            // Sử dụng định dạng văn bản thông thường
            var formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            // Tắt việc in log ra console (vì CLI đã tự in lỗi theo ý muốn)
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Cảnh báo: Không thể khởi tạo ghi log (nova.log). Lỗi: " + e.getMessage());
        }
    }

    /**
     * Ghi log thông tin (INFO).
     */
    public static void info(String message) {
        logger.info(message);
    }

    /**
     * Ghi log lỗi (SEVERE) với thông báo.
     */
    public static void error(String message) {
        logger.severe(message);
    }

    /**
     * Ghi log lỗi (SEVERE) kèm theo StackTrace của Exception.
     */
    public static void error(String message, Throwable t) {
        logger.log(Level.SEVERE, message, t);
    }
}
