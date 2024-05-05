import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import nodes.Function;
import tokens.CharacterToken;
import tokens.NumberToken;
import tokens.Token;
import tokens.WordToken;

public record JavaFile(Path filePath) {
    private List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        try {
            StreamTokenizer st = new StreamTokenizer(new FileReader(filePath.toFile()));
            // Ignore comments
            st.slashSlashComments(true);
            st.slashStarComments(true);

            // Treat dot as ordinary character
            st.ordinaryChar('.');

            // Don't treat a single slash as a comment: https://bugs.openjdk.org/browse/JDK-4217680
            st.ordinaryChar('/');

            // Allow underscore in words
            st.wordChars('_', '_');

            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                int ln = st.lineno();
                switch (st.ttype) {
                    case StreamTokenizer.TT_WORD -> tokens.add(new WordToken(st.sval, ln));
                    case StreamTokenizer.TT_NUMBER -> tokens.add(new NumberToken(st.nval, ln));
                    case StreamTokenizer.TT_EOL -> {
                    }
                    default -> tokens.add(new CharacterToken((char) st.ttype, ln));
                }
            }

            return tokens;
        } catch (IOException e) {
            System.err.println("Could not tokenize file '" + this + "': " + e);
            return new ArrayList<>();
        }
    }

    public List<Function> getFunctions() {
        List<Token> ts = tokenize();
        List<Function> functions = new ArrayList<>();

        for (int i = 1; i < ts.size() - 1; i++) {
            if (ts.get(i) instanceof WordToken functionName) {
                // Ignore things that cannot be functions
                if (isReservedFunctionName(functionName) || isConstructor(ts, i) ||
                    isAnonymousClass(ts, i) || !hasReturnType(ts, i)) {
                    continue;
                }

                int[] paramRange = findParameterRange(ts, i);

                if (paramRange == null) {
                    continue;
                }

                int[] functionBodyRange = findFunctionBodyRange(ts, paramRange[1] + 1);

                if (functionBodyRange == null) {
                    continue;
                }

                functions.add(
                    new Function(functionName, ts.subList(paramRange[0], paramRange[1] + 1),
                        ts.subList(functionBodyRange[0], functionBodyRange[1] + 1), filePath));
            }
        }

        return functions;
    }

    private boolean isReservedFunctionName(WordToken functionName) {
        String word = functionName.getWord();
        return Set.of("if", "else", "for", "while", "do", "switch", "try", "catch").contains(word);
    }

    private boolean isConstructor(List<Token> tokens, int index) {
        if (index > 0 && tokens.get(index - 1) instanceof WordToken prevToken) {
            return Set.of("public", "private", "protected").contains(prevToken.getWord());
        }
        return false;
    }

    private boolean isAnonymousClass(List<Token> tokens, int index) {
        if (index > 0 && tokens.get(index - 1) instanceof WordToken prevToken) {
            return "new".equals(prevToken.getWord());
        }
        return false;
    }

    private boolean hasReturnType(List<Token> tokens, int index) {
        if (index <= 0) {
            return false;
        }
        // Check if the previous character is a word or '>'
        return tokens.get(index - 1) instanceof WordToken ||
            (tokens.get(index - 1) instanceof CharacterToken c && c.getCharacter() == '>');
    }

    private int[] findParameterRange(List<Token> tokens, int index) {
        return findBracketRange(tokens, index, '(', ')');
    }

    private int[] findFunctionBodyRange(List<Token> tokens, int index) {
        return findBracketRange(tokens, index, '{', '}');
    }

    private int[] findBracketRange(List<Token> tokens, int index, char open, char close) {
        boolean nextIsOpen =
            tokens.get(index + 1) instanceof CharacterToken c && c.getCharacter() == open;

        if (!nextIsOpen) {
            return null;
        }

        // Skip current character and the opening bracket
        int start = index + 2;

        int counter = 1;
        for (int i = start; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            if (token instanceof CharacterToken characterToken) {
                char character = characterToken.getCharacter();
                if (character == open) {
                    counter++;
                } else if (character == close) {
                    counter--;
                }

                if (counter == 0) {
                    // `i` is pointing to the matching closing bracket
                    return new int[] {start, i - 1};
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return filePath.toString();
    }
}
