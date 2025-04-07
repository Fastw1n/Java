import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

public class Wspp {
    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(args[0], StandardCharsets.UTF_8));
            Map<String, List<Integer>> words = new LinkedHashMap<String, List<Integer>>();
            try {
                StringBuilder wordBulder = new StringBuilder();
                int wordscount = 0;
                while (true) {
                    String line = in.readLine();
                    if (line == null) {
                        break;
                    } else {
                        line = line + " ";
                    }
                    for (int i = 0; i < line.length(); i++) {
                        if (Character.isLetter(line.charAt(i)) || Character.getType(line.charAt(i)) == Character.DASH_PUNCTUATION || line.charAt(i) == '\'') {
                            wordBulder.append(line.charAt(i));
                        } else if (wordBulder.length() > 0) {
                            wordscount+=1;
                            String word = wordBulder.toString().toLowerCase();
                            words.computeIfAbsent(word, w -> new ArrayList<>()).add(wordscount);
                            wordBulder = new StringBuilder();
                        }
                    }
                }
            } finally {
                in.close();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(args[1], StandardCharsets.UTF_8));
            try {
                for (Map.Entry<String, List<Integer>> entry : words.entrySet()) {
                    out.write(entry.getKey() + " " + entry.getValue().size());
                    for (Integer pos : entry.getValue()){
                        out.write(" " + pos);
                    }
                    out.newLine();
                }
            } finally {
                out.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Cannot open file:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("Cannot read or write file:" + e.getMessage());
        }
    }
}

