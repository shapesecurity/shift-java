package com.shapesecurity.shift.es2017.reducer;

import junit.framework.TestCase;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class VisitorTestCase extends TestCase {
    private static final String BASE_PATH = System.getenv("CONFIG_DIR") == null ? "src/test/resources" : System.getenv("CONFIG_DIR");

    protected static Path getPath(String path) {
        Path pathObj = Paths.get(BASE_PATH + '/' + path);
        if (Files.exists(pathObj)) {
            return pathObj;
        } else {
            return Paths.get(path);
        }
    }

    @Nonnull
    protected static String readFile(@Nonnull String path) throws IOException {
        byte[] encoded = Files.readAllBytes(getPath(path));
        return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(encoded)).toString();
    }
}
