package kzclient.mod.function;

import java.util.HashMap;
import java.util.Map;

public class ObjectRegistry {

    private final Map<Integer, RegistryObject> data = new HashMap<>();

    public int getSize() {
        return data.size();
    }

    public RegistryObject get(int index) {
        return data.get(index);
    }

    public void put(int index, RegistryObject object) {
        data.put(index, object);
    }

    public static ObjectRegistry createWith(Object... objects) {
        ObjectRegistry registry = new ObjectRegistry();
        for(int i = 0; i < objects.length; i++) {
            registry.put(i, new RegistryObject(objects[i]));
        }
        return registry;
    }



    public static class RegistryObject {
        private Object object;

        public RegistryObject(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }

        public boolean asBoolean() {
            return (boolean) object;
        }

        public int asInt() {
            return (int) object;
        }

        public float asFloat() {
            return (float) object;
        }

        public double asDouble() {
            return (double) object;
        }

        public String asString() {
            return (String) object;
        }

    }
}
