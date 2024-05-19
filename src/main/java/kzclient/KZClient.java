package kzclient;

import com.google.gson.JsonObject;
import kzclient.event.EventManager;
import kzclient.mod.ModListeners;
import kzclient.mod.ModManager;
import kzclient.util.HTTPUtil;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.util.logging.Logger;

public class KZClient {

    @Getter @Setter private static KZClient instance;

    @Getter private static Logger logger = Logger.getLogger("KZCLIENT-LOG");
    public static File STATIC_FOLDER;

    @Getter private ModManager modManager;
    @Getter private final EventManager eventManager = new EventManager();

    public void load() {
        eventManager.register(new ModListeners());
        
        modManager = new ModManager();
        modManager.loadMods();
    }

    public void preLoad() {
        Display.setTitle("KZClient");

        if(!new File("kzclient").exists()) {
            new File("kzclient").mkdir();
        }
        STATIC_FOLDER = new File("kzclient");
    }

    public boolean shouldRun() {
        JsonObject info = HTTPUtil.runApiEndpoint("info");
        if(info == null) {
            logger.warning("Failed to connect to the API server.");
            return false;
        }

        return true;
    }
}
