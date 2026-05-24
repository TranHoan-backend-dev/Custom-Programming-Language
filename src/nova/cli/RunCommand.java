package nova.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class RunCommand implements CliCommand {

    @Override
    public void execute(String[] args) throws IOException {
        java.nio.file.Path currentPath = Paths.get("").toAbsolutePath();
        java.nio.file.Path srcDir = currentPath.resolve("src");
        java.nio.file.Path mainFile = srcDir.resolve("main.nova");
        
        if (!Files.exists(mainFile)) {
            System.out.println("Error: Cannot find src/main.nova. Please run this command from the project root.");
            return;
        }
        
        runFile(mainFile.toString());
    }

    public void runFile(String path) throws IOException {
        if (!path.endsWith(".nova")) {
            path += ".nova";
        }

        java.nio.file.Path filePath = Paths.get(path).toAbsolutePath();
        String expectedLocale = CommandUtils.getExpectedLocale(filePath);

        byte[] bytes = Files.readAllBytes(filePath);
        CommandUtils.run(new String(bytes, StandardCharsets.UTF_8), expectedLocale);
    }
}
