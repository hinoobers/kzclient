package kzclient.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {

    private final Map<Method, Object> listeners = new HashMap<>();

    public void register(Object clazz) {
        for(Method method : clazz.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(Listener.class)) {
                Class<?>[] params = method.getParameterTypes();
                if(params.length == 1) {
                    if(Event.class.isAssignableFrom(params[0])) {
                        listeners.put(method, clazz);
                    }
                }
            }
        }
    }

    public void call(Event event) {
        listeners.forEach((method, clazz) -> {
            try {
                if(method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                    method.invoke(clazz, event);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
