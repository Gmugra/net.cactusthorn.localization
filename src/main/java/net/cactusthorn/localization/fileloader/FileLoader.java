package net.cactusthorn.localization.fileloader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface FileLoader {

    default Charset fileCharset() {
        return UTF_8;
    }

    String filenameExtension();

    Map<String, String> asMap(InputStream inputStream) throws IOException;
}
