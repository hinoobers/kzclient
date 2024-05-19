package kzclient.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kzclient.Settings;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.net.URL;
import java.net.URLConnection;

public class HTTPUtil {

    public static JsonObject read(String urll) throws Exception{
        URL url = new URL(urll);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();

        return new Gson().fromJson(builder.toString(), JsonObject.class);
    }

    public static JsonObject runApiEndpoint(String endpoint) {
        try {
            return read(Settings.API_URL + endpoint);
        } catch (Exception e) {
            return null;
        }
    }
}
