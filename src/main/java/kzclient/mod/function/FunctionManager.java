package kzclient.mod.function;

import kzclient.mod.function.impl.SayMessageFunction;
import kzclient.mod.function.impl.event.ChatMessageFunction;
import kzclient.mod.function.impl.event.PostMotionFunction;
import kzclient.mod.function.impl.TestFunction;
import kzclient.mod.function.impl.string.ContainsFunction;

import java.util.HashMap;
import java.util.Map;

public class FunctionManager {

    // This is general ID, same for EACH mod, #function.getId() is unique for each function
    private static final Map<Integer, ModFunction> functions = new HashMap<>();

    public static void registerFunction( ModFunction function) {
        functions.put(functions.size() + 1, function);
    }

    private static void registerFunctions() {
        registerFunction(new TestFunction());
        registerFunction(new PostMotionFunction());
        registerFunction(new SayMessageFunction());
        registerFunction(new ChatMessageFunction());
        registerFunction(new ContainsFunction());
    }

    public static ModFunction getFunction(int id) {
        if(functions.isEmpty()) {
            registerFunctions();
        }

        return functions.get(id)._copy();
    }

    public static int getFunctionId(ModFunction function) {
        return getFunctionId(function.getClass());
    }

    public static int getFunctionId(Class<?> clazz) {
        if(functions.isEmpty()) {
            registerFunctions();
        }

        for(Map.Entry<Integer, ModFunction> entry : functions.entrySet()) {
            if(entry.getValue().getClass().equals(clazz)) {
                return entry.getKey();
            }
        }

        return -1;
    }

    public static Map<Integer, ModFunction> getFunctions() {
        if(functions.isEmpty()) {
            registerFunctions();
        }

        return functions;
    }


}
