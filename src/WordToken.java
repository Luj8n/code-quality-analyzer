public class WordToken implements Token {
    private final String word;

    public WordToken(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "Word: '" + word + "'";
    }
}
