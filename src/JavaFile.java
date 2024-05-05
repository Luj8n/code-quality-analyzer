import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JavaFile {
    private final Path path;

    public JavaFile(Path path) {
        this.path = path;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        try {
            StreamTokenizer st = new StreamTokenizer(new FileReader(path.toFile()));
            // Ignore comments
            st.slashSlashComments(true);
            st.slashStarComments(true);

            // Treat dot as ordinary character
            st.ordinaryChar('.');

            // Allow underscore in words
            st.wordChars('_', '_');

            while (st.nextToken() != StreamTokenizer.TT_EOF) {
                switch (st.ttype) {
                    case StreamTokenizer.TT_WORD -> tokens.add(new WordToken(st.sval));
                    case StreamTokenizer.TT_NUMBER -> tokens.add(new NumberToken(st.nval));
                    case StreamTokenizer.TT_EOL -> {
                    }
                    default -> tokens.add(new CharacterToken((char) st.ttype));
                }
            }

            return tokens;
        } catch (IOException e) {
            System.err.println("Could not tokenize file '" + this + "': " + e);
            return new ArrayList<>();
        }
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path.toString();
    }
}
