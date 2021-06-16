package interpreter.value;

import java.util.Vector;

public class ArrayValue extends Value<Vector<Value<?>>> {

    private Vector<Value<?>> value;

    public ArrayValue(Vector<Value<?>> value) {
        this.value = value;
    }

    @Override
    public Vector<Value<?>> value() {
        return value;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < value.size(); i++) {
            Value<?> v = value.get(i);
            sb.append(i == 0 ? " " : ", ");
            sb.append(v == null ? "" : v.toString());
        }
        sb.append(" ]");
        return sb.toString();
    }

}
