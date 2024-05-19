package kzclient.mod.functions;

import kzclient.mod.functions.impl.IsSprinting;
import kzclient.mod.functions.impl.SayMessage;
import kzclient.mod.functions.impl.StartSprinting;
import kzclient.mod.functions.impl.event.PreTick;
import kzclient.mod.functions.impl.event.StartSprintingEvent;

import java.util.HashMap;
import java.util.Map;

public class FunctionManager {

    public static Map<Integer, ModFunction> functionMap = new HashMap<>();

    private static void load() {
        functionMap.put(0, new IsSprinting());
        functionMap.put(1, new StartSprinting());
        functionMap.put(2, new PreTick());
        functionMap.put(3, new StartSprintingEvent());
        functionMap.put(4, new SayMessage());
    }

    public static ModFunction getFunction(int id) {
        if(functionMap.isEmpty()) {
            load();
        }
        return functionMap.get(id)._copy();
    }

    public static int getFunctionId(ModFunction function) {
        if(functionMap.isEmpty()) {
            load();
        }

        // TODO: Will throw an error/crash if something is very wrong
        return functionMap.entrySet().stream().filter(entry -> entry.getValue().getClass().equals(function.getClass())).findFirst().orElse(null).getKey();
    }
}
