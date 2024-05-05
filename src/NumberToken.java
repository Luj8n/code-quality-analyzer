public class NumberToken implements Token {
    private final double number;

    public NumberToken(double number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Number: '" + number + "'";
    }
}
