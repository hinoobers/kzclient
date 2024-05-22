package kzclient.mod;

import kzclient.KZClient;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class ModManager {

    private File modsFolder;
    private final List<Mod> mods = new ArrayList<>();

    public ModManager() {
        this.modsFolder = new File(KZClient.STATIC_FOLDER, "mods");
        if(!this.modsFolder.exists()) {
            this.modsFolder.mkdir();
        }
    }

    public void loadMods() {
        for(File file : this.modsFolder.listFiles()) {
            if(file.isDirectory()) {
                try {
                    loadMod(file);
                } catch(Exception e){
                    e.printStackTrace();
                    KZClient.getLogger().severe("Failed to load mod: " + file.getName());
                }
            }
        }
    }

    private void loadMod(File file) throws IOException {
        org.yaml.snakeyaml.Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(Files.newInputStream(new File(file, "info.yaml").toPath()));

        Mod mod = new Mod(file.getName(), data.get("name").toString(), data.get("author").toString(), data.get("version").toString(), data.get("description").toString());
        KZClient.getLogger().info("Loaded mod: " + data.get("name").toString() + " by " + data.get("author").toString() + " v" + data.get("version").toString() + " - " + data.get("description").toString());
        this.mods.add(mod);
    }

    public void loadMod(Mod mod) {
        this.mods.add(mod);
    }

    public Collection<Mod> getMods() {
        return Collections.unmodifiableCollection(this.mods);
    }

    public String generateNewModID() {
        while(true) {
            String id = UUID.randomUUID().toString().replace("-", "");
            if(mods.stream().noneMatch(mod -> mod.getId().equals(id))) {
                return id;
            }
        }
    }
}
