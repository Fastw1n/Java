import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WsppPosition {

    public static void main(String[] args) {

        try {
            BufferedReader in = new BufferedReader(new FileReader(args[0], StandardCharsets.UTF_8));
            Map<String, List<String>> words = new LinkedHashMap<>();
            try {
                int stringcount = 1;
                String line = in.readLine();
                while (line != null) {
                    int wordscount = 1;
                    MyScanner scanner = new MyScanner(line);
                    while (scanner.hasNextWord()) {
                        String word = scanner.nextWord().toLowerCase();
                        words.computeIfAbsent(word, w -> new ArrayList<>()).add("" + stringcount + ":" + wordscount);
                        wordscount += 1;
                    }
                    line = in.readLine();
                    stringcount++;
                }
            } finally {
                in.close();
            }
            try (BufferedWriter out = new BufferedWriter(new FileWriter(args[1], StandardCharsets.UTF_8))) {
                for (Map.Entry<String, List<String>> entry : words.entrySet()) {
                    out.write(entry.getKey() + " " + entry.getValue().size());
                    for (String pos : entry.getValue()) {
                        out.write(" " + pos);
                    }
                    out.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open file:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Cannot read or write file:" + e.getMessage());
        }
    }
}
