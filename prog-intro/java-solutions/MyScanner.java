import java.lang.StringBuilder;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.TreeMap;

public class MyScanner implements Closeable {

    private BufferedReader reader;
    private StringBuilder buffer = new StringBuilder("");
    private char deltaBuffer = '\0';
    private final char lineSeparator = System.lineSeparator().charAt(System.lineSeparator().length() - 1);
    private boolean isNewLine = false;

    public MyScanner(InputStream in) {
        reader = new BufferedReader(new InputStreamReader(in));
    }

    public MyScanner(File file) {
        try {
            reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Input file not found in this derrectory: " + e.getMessage());
        }
    }

    public MyScanner(File file, String charsetType) {
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetType));
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found in this derrectory: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding like this: " + charsetType + " is unsupportable: " + e.getMessage());
        }
    }

    public MyScanner(String line) {
        reader = new BufferedReader(new StringReader(line));
    }

    public boolean hasNextWord() {
        if (buffer.length() > 0) {
            return true;
        } else {
            addDeltaToBuffer();
            try {
                int chNum;
                while ((chNum = reader.read()) != -1) {
                    char symbol = (char) chNum;
                    if (Character.isLetter(symbol) || Character.getType(symbol) == Character.DASH_PUNCTUATION || symbol == '\'') {
                        buffer.append(symbol);
                    } else if (symbol == '\r' || symbol == '\n') {
                        isNewLine = true;
                        //skipLineSeparatorSymbols();
                        return hasNextWord();
                    } else {
                        return hasNextWord();
                    }
                }
                return buffer.length() > 0;
            } catch (IOException e) {
                System.err.println("Read error: " + e.getMessage());
                return false;
            }
        }
    }

    public String nextWord() {
        if (hasNextWord()) {
            String next = new String(buffer);
            buffer = new StringBuilder("");
            return next;
        } else {
            return null;
        }
    }

    public boolean getIsNewLine() {
        if (isNewLine) {
            isNewLine = false;
            return true;
        } else {
            return false;
        }
    }

    public boolean hasNext() {
        if (buffer.length() > 0) {
            return true;
        } else {
            addDeltaToBuffer();
            try {
                int chNum;
                while ((chNum = reader.read()) != -1) {
                    char symbol = (char) chNum;
                    if (Character.isLetter(symbol) || Character.getType(symbol) == Character.DASH_PUNCTUATION
                            || symbol == '\'' || Character.isDigit(symbol) || symbol == '-' || symbol == '+') {
                        buffer.append(symbol);
                    } else if (symbol == '\r' || symbol == '\n') {
                        isNewLine = true;
                        skipLineSeparatorSymbols();
                        return hasNext();
                    } else {
                        return hasNext();
                    }
                }
                return buffer.length() > 0;
            } catch (IOException e) {
                System.err.println("Read error: " + e.getMessage());
                return false;
            }
        }
    }

    public String next() {
        if (hasNext()) {
            String next = new String(buffer);
            buffer = new StringBuilder("");
            return next;
        } else {
            return null;
        }
    }

    public boolean hasNextInt() {
        if (buffer.length() > 0) {
            return true;
        } else {
            addDeltaToBuffer();
            try {
                int chNum;
                while ((chNum = reader.read()) != -1) {
                    char symbol = (char) chNum;
                    if (Character.isDigit(symbol) || symbol == '-' || symbol == '+') {
                        buffer.append(symbol);
                    } else if (symbol == '\r' || symbol == '\n') {
                        isNewLine = true;
                        skipLineSeparatorSymbols();
                        return hasNextInt();
                    } else {
                        return hasNextInt();
                    }
                }
                return buffer.length() > 0;
            } catch (IOException e) {
                System.err.println("Int read error: " + e.getMessage());
                return false;
            }
        }
    }

    public Integer nextInt() {
        if (hasNextInt()) {
            String next = new String(buffer);
            buffer = new StringBuilder("");
            return Integer.parseInt(next);
        } else {
            return null;
        }
    }

    public boolean hasNextConvert() {
        /*
         * This method convert leters to int
         * a - 0
         * b - 1
         * c - 2
         * d - 3
         * e - 4
         * f - 5
         * g - 6
         * h - 7
         * i - 8
         * j - 9
         * and other :)
         * - - -
         * + - +
         * */
        if (buffer.length() > 0) {
            return true;
        } else {
            addDeltaToBuffer();
            try {
                int chNum;
                while ((chNum = reader.read()) != -1) {
                    char symbol = (char) chNum;
                    if (Character.isLetter(symbol)) {
                        buffer.append(chNum - 97);
                    } else if (symbol == '-' || symbol == '+') {
                        buffer.append(symbol);
                    } else if (symbol == '\r' || symbol == '\n') {
                        isNewLine = true;
                        skipLineSeparatorSymbols();
                        return hasNextConvert();
                    } else {
                        return hasNextConvert();
                    }
                }
                return buffer.length() > 0;
            } catch (IOException e) {
                System.err.print("Read symbol error: " + e.getMessage());
                return false;
            }
        }
    }

    public boolean hasNextLine() {
        if (buffer.length() > 0) {
            return true;
        } else {
            addDeltaToBuffer();
            try {
                int chNum;
                while ((chNum = reader.read()) != -1) {
                    char sym = (char) chNum;
                    if (sym == '\r' || sym == '\n') {
                        isNewLine = true;
                        skipLineSeparatorSymbols();
                        return hasNextLine();
                    } else {
                        buffer.append(sym);
                    }
                }
                return (buffer.length() > 0);
            } catch (IOException e) {
                System.out.println("ReadLine error: " + e.getMessage());
                return false;
            }
        }
    }

    private void addDeltaToBuffer() {
        if (deltaBuffer != '\0') {
            buffer.append(deltaBuffer);
            deltaBuffer = '\0';
        }
    }

    public String nextLine() {
        if (hasNextLine()) {
            String line = new String(buffer);
            buffer = new StringBuilder("");
            return line.strip();
        } else {
            return null;
        }
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            System.err.print("Close error: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Class MyScaner: " + reader.toString() +
                " with buffer, which consist of " + buffer.toString();
    }

    private void skipLineSeparatorSymbols() {
        try {
            int chNum;
            if (System.lineSeparator().length() >= 2) {
                for (int x = 1; x < System.lineSeparator().length(); x++) {
                    if ((chNum = reader.read()) != -1) {
                        char ch = (char) chNum;
                        if (ch != '\r' && ch != '\n') {
                            deltaBuffer = ch;
                            break;
                        }
                    }

                }
            }
        } catch (IOException e) {
            System.err.println("Reader has exception when try to read from input resource: " + e.getMessage());
        }
    }

}

