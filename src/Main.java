import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import nodes.Function;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please provide a directory name to analyze");
            System.exit(1);
        }
        String directoryName = args[0];
        List<JavaFile> files = getJavaFiles(Path.of(directoryName));

        analyze(files);
    }

    private static void analyze(List<JavaFile> files) {
        List<Function> functions = new ArrayList<>();

        for (JavaFile file : files) {
            functions.addAll(file.getFunctions());
        }

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

        int notCamelCase = 0;
        for (Function function : functions) {
            if (!function.isCamelCase()) {
                notCamelCase += 1;
            }
        }

        double percentageNotCamelCase = 100.0 * notCamelCase / functions.size();

        System.out.printf("Not camelcase: %.2f%%", percentageNotCamelCase);
    }

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
