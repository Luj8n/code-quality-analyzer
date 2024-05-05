import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import nodes.Function;

/**
 * The main class.
 */
public class Main {
    /**
     * Main method to analyze Java files in a directory.
     *
     * @param args Command-line arguments. The first argument should be the directory to analyze.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide a directory name to analyze");
            System.exit(1);
        }
        String directoryName = args[0];

        List<JavaFile> files = getJavaFiles(Path.of(directoryName));
        List<Function> functions = getAllFunctions(files);

        analyzeFunctions(functions);
    }

    /**
     * Analyzes a list of functions, printing out their complexity and other metrics.
     *
     * @param functions List of functions to analyze.
     */
    private static void analyzeFunctions(List<Function> functions) {
        if (functions.isEmpty()) {
            System.out.println("No functions found");
            return;
        }

        System.out.println(functions.size() + " functions found");

        // Sort functions by their complexity
        functions.sort(Comparator.comparingInt(Function::calculateComplexity).reversed());

        // Print the 3 most complex methods
        System.out.println("Highest complexity scores:");
        for (int i = 0; i < 3 && i < functions.size(); i++) {
            Function function = functions.get(i);
            System.out.println(
                "Score = " + function.calculateComplexity() + ", function = " + function);
        }

        // Count number of functions whose names are not camelCase.
        int notCamelCase = 0;
        for (Function function : functions) {
            if (!function.isCamelCase()) {
                notCamelCase += 1;
            }
        }

        double percentageNotCamelCase = 100.0 * notCamelCase / functions.size();
        System.out.printf("Not camelcase: %.2f%%", percentageNotCamelCase);
    }

    /**
     * Retrieves all functions from a list of Java files.
     *
     * @param files List of Java files.
     * @return List of functions extracted from the Java files.
     */
    private static List<Function> getAllFunctions(List<JavaFile> files) {
        List<Function> functions = new ArrayList<>();

        for (JavaFile file : files) {
            functions.addAll(file.getFunctions());
        }

        return functions;
    }

    /**
     * Recursively retrieves all Java files from a directory.
     *
     * @param directory Path of the directory to search for Java files.
     * @return List of Java files found in the directory.
     */
    private static List<JavaFile> getJavaFiles(Path directory) {
        List<JavaFile> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path path : stream) {
                if (path.toFile().isDirectory()) {
                    files.addAll(getJavaFiles(path));
                } else {
                    if (path.getFileName().toString().endsWith(".java")) {
                        files.add(new JavaFile(path));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Could not get files of a directory: " + e);
            System.exit(1);
        }
        return files;
    }
}
