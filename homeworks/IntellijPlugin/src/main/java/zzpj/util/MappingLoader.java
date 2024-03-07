package zzpj.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MappingLoader {

    private final static String mappingFilename = "fileExtensionsMapping.json";
    public static Map<String, String> loadMapping() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = MappingLoader.class.getClassLoader().getResourceAsStream(mappingFilename)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + mappingFilename);
            }
            JsonNode root = objectMapper.readTree(inputStream);
            Map<String, String> map = new HashMap<>();

            Iterator<Map.Entry<String, JsonNode>> fieldsIter = root.fields();
            while (fieldsIter.hasNext()) {
                Map.Entry<String, JsonNode> field = fieldsIter.next();
                map.put(field.getKey(), field.getValue().asText());
            }
            return map;
        }

    }
}
