package daluai.stml.stml_render.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class BashDataLoader implements DataLoader {

    private static final Logger LOG = LoggerFactory.getLogger(BashDataLoader.class);
    private static final String BASH = "bash";
    public static final String ERROR_PREFIX = "ERROR: ";

    private final File script;

    public BashDataLoader(File script) {
        if (!script.exists()) {
            throw new IllegalArgumentException("Script '" + script.getAbsolutePath() + "' does not exist");
        }
        if (!script.canExecute()) {
            throw new RuntimeException("Cannot execute script '" + script.getAbsolutePath() + "'");
        }
        this.script = script;
    }

    @Override
    public String load() {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder(BASH, script.getAbsolutePath());
            pb.redirectErrorStream(true); // merge stdout and stderr
            Process process = pb.start();

            try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                LOG.debug("Data loaded from script: {}", script.getName());
            } else {
                LOG.error("Non-zero returned from script: {}", script.getName());
            }
            return output.toString();
        } catch (Exception e) {
            LOG.error("Error during loading", e);
            return ERROR_PREFIX + output;
        }
    }
}
