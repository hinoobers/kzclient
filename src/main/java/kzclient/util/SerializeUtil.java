package kzclient.util;

import java.util.Base64;

public class SerializeUtil {

    public static String serialize(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    public static String deserialize(String s) {
        return new String(Base64.getDecoder().decode(s));
    }
}
