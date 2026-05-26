package nova.cli;

import java.io.IOException;

/**
 * Interface đại diện cho một lệnh CLI (Command Line Interface).
 * Mọi lệnh như 'run', 'init', 'test', 'check' đều phải implement interface này.
 */
public interface CliCommand {
    
    /**
     * Thực thi lệnh với các đối số truyền vào.
     *
     * @param args Các tham số dòng lệnh được truyền cho lệnh này.
     * @throws IOException Nếu có lỗi vào/ra xảy ra trong quá trình thực thi lệnh.
     */
    void execute(String[] args) throws IOException;
}
