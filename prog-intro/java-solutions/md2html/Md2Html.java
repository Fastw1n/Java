package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class Md2Html {
    public static boolean endStrong = false;
    public static boolean endEm = false;
    public static boolean endS = false;
    public static boolean endCode = false;
    public static boolean endVar = false;

    public static String parseLine(String line) {
        StringBuilder stringBuilder = new StringBuilder("");
        char lastSymbol = '\0';
        int i = 0;
        while (i < line.length()) {
            char symbol = line.charAt(i);
            if (symbol == '*' || symbol == '_') {
                if ((i+1) < line.length() && (symbol == line.charAt(i + 1))) {
                    if (endStrong == true) {
                        stringBuilder.append("</strong>");
                        endStrong = false;
                    } else {
                        stringBuilder.append("<strong>");
                        endStrong = true;
                    }
                    i++;
                } else {
                    if ( ((i+1) < line.length() && ((Character.isWhitespace(lastSymbol) && Character.isWhitespace(line.charAt(i + 1)))
                            || lastSymbol == '\\'))) {
                        stringBuilder.append(symbol);
                    } else  if (i + 1 == line.length() && endEm == false) {
                        stringBuilder.append(symbol);
                    } else {
                        if (endEm == true) {
                            stringBuilder.append("</em>");
                            endEm = false;
                        } else {
                            stringBuilder.append("<em>");
                            endEm = true;
                        }
                    }
                }
            } else if (((i+1) < line.length()) && (symbol == '-' && line.charAt(i + 1) == symbol)) {
                if (endS == true) {
                    stringBuilder.append("</s>");
                    endS = false;
                } else {
                    stringBuilder.append("<s>");
                    endS = true;
                }
                i++;
            } else if (symbol == '`') {
                if (endCode == true) {
                    stringBuilder.append("</code>");
                    endCode = false;
                } else {
                    stringBuilder.append("<code>");
                    endCode = true;
                }
            } else if (symbol == '%' ) {
                if ((i+1) < line.length() && (lastSymbol == '\\' || (Character.isWhitespace(lastSymbol) && Character.isWhitespace(line.charAt(i + 1))))) {
                    stringBuilder.append(symbol);
                } else  if (i + 1 == line.length() && endVar == false) {
                    stringBuilder.append(symbol);
                } else {
                    if (endVar == true) {
                        stringBuilder.append("</var>");
                        endVar = false;
                    } else {
                        stringBuilder.append("<var>");
                        endVar = true;
                    }
                }

            } else {
                if (symbol == '&') {
                    stringBuilder.append("&amp;");
                } else if (symbol == '<') {
                    stringBuilder.append("&lt;");
                } else if (symbol == '>') {
                    stringBuilder.append("&gt;");
                } else if (symbol != '\\' && symbol != '\r') {
                    stringBuilder.append(symbol);
                }
            }
            lastSymbol = symbol;
            i++;
        }
        return stringBuilder.toString();

    }

    public static void main(String[] args) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(args[0], StandardCharsets.UTF_8));
            StringBuilder convert = new StringBuilder("");

            boolean endP = false;
            boolean endH = false;
            byte headLevel = 1;

            try {
                String line = bufferedReader.readLine();
                while (line != null) {
                    if (!line.equals("")) {
                        line = parseLine(line);
                        if (endP == true) {
                            convert.append("\n").append(line);
                        } else if (endH == true) {
                            convert.append("\n").append(line);
                        } else if (line.charAt(0) != '#') {
                            if (endP == true) {
                                convert.append("\n").append(line);
                            } else {
                                convert.append("<p>").append(line);
                            }
                            endP = true;
                        } else if (line.charAt(0) == '#') {
                            while (headLevel < line.length()) {
                                if (line.charAt(headLevel) == '#') {
                                    headLevel++;
                                } else {
                                    break;
                                }
                            }
                            if (!Character.isWhitespace(line.charAt(headLevel))) {
                                if (endP == true) {
                                    convert.append("\n").append(line);
                                } else {
                                    convert.append("<p>").append(line);
                                }
                                endP = true;
                            } else {
                                if (endH == true) {
                                    convert.append("\n").append(line);
                                } else {
                                    convert.append("<h").append(headLevel).append(">").append(line.substring(headLevel + 1));
                                }
                                endH = true;
                            }
                        }
                    } else {
                        if (endH == true) {
                            convert.append("</h").append(headLevel).append(">\n");
                            headLevel = 1;
                            endH = false;
                        } else if (endP == true) {
                            convert.append("</p>\n");
                            endP = false;
                        }
                    }
                    line = bufferedReader.readLine();
                }
                if (endH == true) {
                    convert.append("</h").append(headLevel).append(">\n");
                } else if (endP == true) {
                    convert.append("</p>\n");
                }


            }finally {
                bufferedReader.close();
            }
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[1]), StandardCharsets.UTF_8));
                try {
                    writer.write(convert.toString());
                } finally {
                    writer.close();
                }
            } catch (IOException e) {
                System.out.println("Write error: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Reader error :" + e.getMessage());
        }
    }
}
