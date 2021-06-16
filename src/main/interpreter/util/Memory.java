package interpreter.util;

import java.util.HashMap;
import java.util.Map;

import interpreter.value.StringValue;
import interpreter.value.Value;

public class Memory {

    private static Map<String, Value<?> > memory = new HashMap<String, Value<?> >();

    public static Value<?> read(String name) {
        Value<?> value = memory.get(name);
        if (value == null) {
            value = new StringValue("");
            Memory.write(name, value);
        }

        return value;
    }

    public static void write(String name, Value<?> value) {
        memory.put(name, value);
    }

}
