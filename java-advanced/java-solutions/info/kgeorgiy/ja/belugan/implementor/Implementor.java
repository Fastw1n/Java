package info.kgeorgiy.ja.belugan.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.nio.file.Paths;
import java.util.Arrays;

import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Generates implementation of interface.
 */
public class Implementor implements JarImpler {

    /**
     * {@link String} constant {@link System#lineSeparator()} : the system-dependent line separator string.
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();
    /**
     * {@link String} constant {@link File#separatorChar} : the system-dependent default name-separator character.
     */
    private static final char FILE_SEPARATOR = File.separatorChar;
    /**
     * {@link String} contains file extensions .java.
     */
    private static final String JAVA_SUFFIX = ".java";
    /**
     * {@link String} contains file extensions .class.
     */
    private static final String CLASS_SUFFIX = ".class";

    /**
     * {@link String} contains file suffix Impl.
     */
    private static final String IMPL = "Impl";

    /**
     * default constructor
     */
    public Implementor() {
        
    }



    /**
     * Main function for checking passed arguments and runs {@link #implement(Class, Path)}
     * if the arguments are valid.
     *
     * @param args {@link String} array of arguments.
     */

    public static void main(String[] args) {
        if (args == null || args[0] == null || args[1] == null || args.length != 2) {
            System.err.println("Invalid arguments");
        }
        try {
            assert args != null;
            new Implementor().implementJar(Class.forName(args[0]), Paths.get(args[1]));
        } catch (ImplerException e) {
            System.err.println("Implement error " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found " + e.getMessage());
        }
    }

    /**
     * Writes the interface implementation to a file using {@link Class type token}
     * by {@link #generateInterface(Class)}.
     *
     * @param token - class or interface to implement, type: {@link Class}
     * @param root  - path to root directory, type: Path
     * @throws ImplerException - if cant implement this class or interface.
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        final String simpleNameImpl = token.getSimpleName() + IMPL;
        final String packageName = token.getPackageName();
        final String canonicalName = token.getCanonicalName();

        if (!token.isInterface()) {
            throw new ImplerException("Cant implement classes");
        }
        if (Modifier.isPrivate(token.getModifiers()) || Modifier.isFinal(token.getModifiers())) {
            throw new ImplerException("Cant implement this interface");
        }

        try {
            Path path = root.resolve(Path.of(packageName.replace('.', FILE_SEPARATOR)).
                    resolve(simpleNameImpl + JAVA_SUFFIX));
            Files.createDirectories(path.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                if (packageName.isEmpty()) {
                    writer.write("");
                } else {
                    writer.write(asciiToUnicode(String.format("package %s;", packageName)
                            + LINE_SEPARATOR
                            + String.format("public class %s implements %s {%s}"
                            , simpleNameImpl
                            , canonicalName
                            , generateInterface(token))));
                }
            }
            // :NOTE: Stringformat
        } catch (IOException e) {
            throw new ImplerException("IOException error", e);
        }
    }

    /**
     * Creates {@link String} with all interface's Methods
     * using {@link #generateParams(Method)} to get Parameters,
     * {@link #generateExceptions(Method)} yo get Exceptions
     * and {@link #generateReturn(Method)} to get return type by {@link StringBuilder}.
     *
     * @param token with {@link Class} type
     * @return {@link String} with all Methods.
     */

    private String generateInterface(Class<?> token) {
        Method[] methods = token.getMethods();
        if (methods.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Method method : methods) {
            stringBuilder.append(String.format("public %s %s%s%s%s"
                    , method.getReturnType().getCanonicalName()
                    , method.getName()
                    , generateParams(method)
                    , generateExceptions(method)
                    , generateReturn(method)));
        }
        return stringBuilder.toString();
    }

    /**
     * Creates {@link String} with all {@link  Method}'s Parameters
     * using {@link #paramsToString(Parameter)}.
     *
     * @param method - {@link Method} from which Parameters are taken
     * @return {@link String} with all Parameters.
     */

    private String generateParams(Method method) {
        Parameter[] parameters;
        parameters = method.getParameters();
        if (parameters.length == 0) {
            return "()";
        }
        return "(" + Arrays.stream(parameters)
                .map(this::paramsToString)
                .collect(Collectors.joining(", ")) + ")";
    }

    /**
     * Create a {@link String} containing the name of {@link Parameter}.
     *
     * @param parameter - name of {@link Parameter}.
     * @return parameter to {@link String}.
     */
    private String paramsToString(Parameter parameter) {
        return String.format("%s %s", parameter.getType().getCanonicalName(), parameter.getName());
    }

    /**
     * Creates {@link String} with all {@link  Method}'s Exceptions.
     *
     * @param method - {@link Method} from which Exceptions are taken
     * @return {@link String} with all Exceptions.
     */

    private String generateExceptions(Method method) {
        Class<?>[] exceptions;
        exceptions = method.getExceptionTypes();
        if (exceptions.length == 0) {
            return "";
        }
        return " throws " + Arrays.stream(exceptions)
                .map(Class::getCanonicalName)
                .collect(Collectors.joining(", "));
    }

    /**
     * Creates {@link String} with {@link  Method}'s
     * return type using {@link #getTypeOfReturn(Method)}
     * and LINE_SEPARATOR for correcting file content.
     *
     * @param method - {@link Method} from which return type are taken.
     * @return {@link String} with {@link  Method}'s return type.
     */
    private String generateReturn(Method method) {
        return LINE_SEPARATOR +
                String.format("{return %s;}", getTypeOfReturn(method))
                + LINE_SEPARATOR;
    }

    /**
     * Creates {@link String} with {@link  Method}'s return type.
     *
     * @param method - {@link Method} from which return type are taken.
     * @return {@link String} with {@link  Method}'s return type.
     */
    private String getTypeOfReturn(Method method) {
        Class<?> tClass = method.getReturnType();
        if (tClass == void.class) {
            return "";
        } else if (tClass == boolean.class) {
            return "false";
        } else if (tClass.isPrimitive()) {
            return "0";
        }
        return null;
    }

    /**
     * provided a .jar file with the implementation of a traditional class (interface).
     *
     * @param token   - class or interface to implement, type: {@link Class}
     * @param jarFile - path to root directory, type: {@link Path}
     * @throws ImplerException - if cant implement this interface.
     */

    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        final String simpleNameImpl = token.getSimpleName() + IMPL;
        final String packageName = token.getPackageName();

        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        Path path = Paths.get("");
        implement(token, path);
        String classToString = String.valueOf(Paths.get("",
                packageName.replace(".", File.separator), simpleNameImpl));

        String tokenPath = null;
        try {
            tokenPath = Path.of(token
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI())
                    .toString();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        final String[] args = {"-cp", tokenPath, classToString + JAVA_SUFFIX, "-encoding", "utf-8"};
        final int exitCode = compiler.run(null, null, null, args);
        if (exitCode != 0) {
            throw new ImplerException("Compilation error");
        }
        writeJarContent(jarFile, manifest, classToString);
    }

    /**
     * Write the contents of a JAR file.
     * @param jarFile - path to root directory, type: Path
     * @param manifest type : {@link Manifest} is used to maintain Manifest entry names and their associated Attributes
     * @param classToString {@link String} path of {@link Class}
     * @throws ImplerException - if cant implement this interface
     */
    private void writeJarContent(Path jarFile, Manifest manifest, String classToString) throws ImplerException {
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            String classToJar = classToString.replace(FILE_SEPARATOR, '/') + CLASS_SUFFIX;
            jos.putNextEntry(new ZipEntry(classToJar));
            Files.copy(Paths.get(classToJar), jos);
        } catch (IOException e) {
            throw new ImplerException("Failed to write to jar file", e);
        }
    }

    /**
     * Convert a string containing ASCII to Unicode.
     * @param string the string to be converted, type : {@link String}
     * @return {@link String} converted to Unicode
     */
    private String asciiToUnicode(String string) {
        return string.chars()
                .mapToObj(c -> c >= 128 ? String.format("\\u%04X", c) : String.valueOf((char) c))
                .collect(Collectors.joining());
    }
}
