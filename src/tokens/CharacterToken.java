package tokens;

public class CharacterToken extends Token {
    private final char character;

    public CharacterToken(char character, int lineNumber) {
        this.character = character;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "Char: '" + character + "'";
    }

    public char getCharacter() {
        return character;
    }
}
