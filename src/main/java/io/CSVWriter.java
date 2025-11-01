package graph.io;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
    public static void writeResults(String filename, List<String[]> data) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            for (String[] row : data) {
                writer.write(String.join(",", row));
                writer.write("\n");
            }
        }
    }
}