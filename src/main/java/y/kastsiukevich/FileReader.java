package y.kastsiukevich;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileReader {

    public String readFile(String fileName) {
        try {
            File file = new File(fileName);
            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while reading file " + fileName, e);
        }
    }
}
