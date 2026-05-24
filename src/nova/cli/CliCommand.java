package nova.cli;

import java.io.IOException;

public interface CliCommand {
    void execute(String[] args) throws IOException;
}
