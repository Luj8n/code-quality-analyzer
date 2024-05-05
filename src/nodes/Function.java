package nodes;

import java.nio.file.Path;
import java.util.List;
import tokens.Token;
import tokens.WordToken;

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

    public boolean isCamelCase() {
        String word = name.getWord();
        return word.matches("^[a-z][a-z0-9]*([A-Z][a-z0-9]+)*[A-Za-z0-9]?$");
    }

    public WordToken getName() {
        return name;
    }

    public List<Token> getParameters() {
        return parameters;
    }

    public List<Token> getInside() {
        return inside;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return name.getWord() + "(" + path.getFileName() + ":" + name.getLineNumber() + ")";
    }
}
