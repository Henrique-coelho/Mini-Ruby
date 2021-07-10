package interpreter.value;

public class IntegerValue extends Value<Integer> {

    private Integer value;

    public IntegerValue(Integer value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
