package md2html;

import java.lang.StringBuilder;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;

public class MyScanner implements Closeable {

    private BufferedReader reader;
    private StringBuilder Builder = new StringBuilder("");
    private final char lineSeparator = System.lineSeparator().charAt(System.lineSeparator().length() - 1);
    private boolean isSeparator = false;

    public MyScanner(InputStream in) {
        reader = new BufferedReader(new InputStreamReader(in));
    }

    public MyScanner(File file) {
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
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
        if (Builder.length() > 0) {
            return true;
        } else {
            try {
                int a;
                while ((a = reader.read()) != -1) {
                    char symbol = (char) a;
                    if (Character.isLetter(symbol) || Character.getType(symbol) == Character.DASH_PUNCTUATION || symbol == '\'') {
                        Builder.append(symbol);
                    } else if (symbol == lineSeparator) {
                        isSeparator = true;

                    } else {
                        return hasNextWord();
                    }
                }
                return Builder.length() > 0;
            } catch (IOException e) {
                System.err.println("Read error: " + e.getMessage());
                return false;
            }
        }
    }

    public String nextWord() {
        if (hasNextWord()) {
            String next = new String(Builder);
            Builder = new StringBuilder("");
            return next;
        } else {
            return null;
        }
    }

    public boolean hasNext() {
        if (Builder.length() > 0) {
            return true;
        } else {
            try {
                int a;
                while ((a = reader.read()) != -1) {
                    char symbol = (char) a;
                    if (Character.isLetter(symbol) || Character.getType(symbol) == Character.DASH_PUNCTUATION
                            || symbol == '\'' || Character.isDigit(symbol) || symbol == '-' || symbol == '+') {
                        Builder.append(symbol);
                    } else {
                        return hasNext();
                    }
                }
                return Builder.length() > 0;
            } catch (IOException e) {
                System.err.println("Read error: " + e.getMessage());
                return false;
            }
        }
    }

    public String next() {
        if (hasNext()) {
            String next = new String(Builder);
            Builder = new StringBuilder("");
            return next;
        } else {
            return null;
        }
    }

    public boolean hasNextInt() {
        if (Builder.length() > 0) {
            return true;
        } else {
            try {
                int a;
                while ((a = reader.read()) != -1) {
                    char symbol = (char) a;
                    if (Character.isDigit(symbol) || symbol == '-' || symbol == '+') {
                        Builder.append(symbol);
                    } else {
                        return hasNextInt();
                    }
                }
                return Builder.length() > 0;
            } catch (IOException e) {
                System.err.println("Int read error: " + e.getMessage());
                return false;
            }
        }
    }

    public Integer nextInt() {
        if (hasNextInt()) {
            String next = new String(Builder);
            Builder = new StringBuilder("");
            return Integer.parseInt(next);
        } else {
            return null;
        }
    }

    public boolean hasNextLine() {
        if (Builder.length() > 0) {
            return true;
        } else {
            try {
                int a;
                while ((a = reader.read()) != -1) {
                    char sym = (char) a;
                    if (sym == lineSeparator) {
                        break;
                    } else {
                        Builder.append(sym);
                    }
                }
                return (Builder.length() > 0);
            } catch (IOException e) {
                System.out.println("ReadLine error: " + e.getMessage());
                return false;
            }
        }
    }

    public String nextLine() {
        if (hasNextLine()) {
            String line = new String(Builder);
            Builder = new StringBuilder("");
            return line;
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

    // :NOTE: странно, что от нескольких вызовов подряд у вас меняется состояние
    public boolean getIsSeparator() {
        if (isSeparator) {
            isSeparator = false;
            return true;
        }
        return false;
    }
}