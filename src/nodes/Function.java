package nodes;

import java.nio.file.Path;
import java.util.List;
import tokens.Token;
import tokens.WordToken;

public record Function(WordToken name, List<Token> parameters, List<Token> inside, Path filePath) {
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

    @Override
    public String toString() {
        return name.getWord() + "(" + filePath.getFileName() + ":" + name.getLineNumber() + ")";
    }
}
