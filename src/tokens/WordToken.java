package tokens;

public class WordToken extends Token {
    private final String word;

    public WordToken(String word, int lineNumber) {
        this.word = word;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "Word: '" + word + "'";
    }

    public String getWord() {
        return word;
    }
}
