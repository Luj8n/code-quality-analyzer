public class CharacterToken implements Token {
    private final char character;

    public CharacterToken(char character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return "Char: '" + character + "'";
    }
}
