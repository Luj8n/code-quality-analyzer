import java.nio.file.Path;
import java.util.List;

public class Function {
    private final WordToken name;
    private final List<Token> parameters;
    private final List<Token> inside;
    private final Path path;

    public Function(WordToken name, List<Token> parameters, List<Token> inside, Path path) {
        this.name = name;
        this.parameters = parameters;
        this.inside = inside;
        this.path = path;
    }

    public int calculateComplexity() {
        int count = 0;
        for (Token token : inside) {
            if (token instanceof WordToken wt) {
                String w = wt.getWord();
                if (w.equals("if") || w.equals("switch") || w.equals("for") || w.equals("while")) {
                    count += 1;
                }
            }
        }
        return count;
    }

    public WordToken getName() {
        return name;
    }

    public List<Token> getInside() {
        return inside;
    }

    @Override
    public String toString() {
        return name.getWord() + "(" + path.getFileName() + ":" + name.getLineNumber() + ")";
    }
}
