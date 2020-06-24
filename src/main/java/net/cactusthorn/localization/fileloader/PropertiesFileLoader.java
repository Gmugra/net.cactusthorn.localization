package net.cactusthorn.localization.fileloader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

public class PropertiesFileLoader implements FileLoader {

    @Override
    public String filenameExtension() {
        return "properties";
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, String> asMap(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        try (InputStreamReader reader = new InputStreamReader(inputStream, fileCharset());
                BufferedReader buf = new BufferedReader(reader)) {

            properties.load(buf);
            return (Map) properties;
        }
    }

}
