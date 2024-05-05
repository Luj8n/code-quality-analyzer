import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import nodes.Function;
import tokens.CharacterToken;
import tokens.NumberToken;
import tokens.Token;
import tokens.WordToken;

public class JavaFile {
    private final Path path;

    public JavaFile(Path path) {
        this.path = path;
    }

    private List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        try {
            StreamTokenizer st = new StreamTokenizer(new FileReader(path.toFile()));
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
            int j = i;
            if (ts.get(j) instanceof WordToken functionName) {
                String w = functionName.getWord();
                // Ignore things that cannot be functions
                if (w.equals("if") || w.equals("else") || w.equals("for") || w.equals("while") ||
                    w.equals("do") || w.equals("switch") || w.equals("try") || w.equals("catch")) {
                    continue;
                }
                if (ts.get(j - 1) instanceof WordToken x) {
                    String p = x.getWord();
                    // Ignore constructors
                    if (p.equals("public") || p.equals("private") || p.equals("protected")) {
                        continue;
                    }
                    // Ignore this kind of syntax: `return new Builder() {...}`
                    if (p.equals("new")) {
                        continue;
                    }
                }
                // Check if the previous character is a word or '>'
                if (ts.get(j - 1) instanceof WordToken ||
                    (ts.get(j - 1) instanceof CharacterToken c && c.getCharacter() == '>')) {

                    if (ts.get(j + 1) instanceof CharacterToken c && c.getCharacter() == '(') {
                        j += 2;
                        int paramStart = j;
                        int counter = 1;
                        while (counter != 0) {
                            if (ts.get(j) instanceof CharacterToken x && x.getCharacter() == '(') {
                                counter += 1;
                            } else if (ts.get(j) instanceof CharacterToken x &&
                                x.getCharacter() == ')') {
                                counter -= 1;
                            }

                            j += 1;
                        }

                        int paramEnd = j - 2;

                        if (ts.get(j) instanceof CharacterToken o && o.getCharacter() == '{') {
                            j += 1;
                            int functionStart = j;

                            int counter2 = 1;
                            while (counter2 != 0) {
                                if (ts.get(j) instanceof CharacterToken x &&
                                    x.getCharacter() == '{') {
                                    counter2 += 1;
                                } else if (ts.get(j) instanceof CharacterToken x &&
                                    x.getCharacter() == '}') {
                                    counter2 -= 1;
                                }

                                j += 1;
                            }

                            int functionEnd = j - 2;

                            functions.add(
                                new Function(functionName, ts.subList(paramStart, paramEnd + 1),
                                    ts.subList(functionStart, functionEnd + 1), path));
                        }
                    }
                }
            }
        }

        return functions;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
