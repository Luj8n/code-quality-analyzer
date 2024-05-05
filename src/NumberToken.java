public class NumberToken extends Token {
    private final double number;

    public NumberToken(double number, int lineNumber) {
        this.number = number;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "Number: '" + number + "'";
    }

    public double getNumber() {
        return number;
    }
}
