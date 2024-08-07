package com.trainDelay.calculator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CalculatorFileUtils {

    public static void clearOldData(boolean clearOldData) throws IOException {
        if (clearOldData) {
            List<String> listFileDirectories = List.of(
                    ServiceMetrics.OUTBOUND_ATTRIBUTE_MESSAGE_DIR,
                    ServiceMetrics.INBOUND_ATTRIBUTE_MESSAGE_DIR,
                    ServiceMetrics.OUTBOUND_SERVICE_MESSAGE_DIR,
                    ServiceMetrics.INBOUND_SERVICE_MESSAGE_DIR
            );
            for (String dir : listFileDirectories) {
                Files.walk(Paths.get(dir))
                        .map(java.nio.file.Path::toFile)
                        .forEach(file -> {
                            if (file.isFile() || file.isDirectory()) {
                                file.delete();
                            }
                        });
            }
        }
    }
    public static void writeFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }
}
