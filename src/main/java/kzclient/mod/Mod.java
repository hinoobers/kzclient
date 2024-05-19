package kzclient.mod;

import kzclient.KZClient;
import kzclient.mod.functions.FunctionManager;
import kzclient.mod.functions.ModFunction;
import kzclient.mod.functions.impl.IsSprinting;
import kzclient.mod.functions.impl.StartSprinting;
import kzclient.mod.functions.impl.event.PreTick;
import kzclient.mod.functions.impl.event.StartSprintingEvent;
import kzclient.util.SerializeUtil;
import lombok.Getter;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;

public class Mod {

    @Getter private String id, name, author, version, description;
    private List<ModFunction> functions = new ArrayList<>();

    public Mod(String id, String name, String author, String version, String description) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.version = version;
        this.description = description;
        this.loadFunctions();
    }

    public void save(){
        try {
            File modFolder = new File(KZClient.STATIC_FOLDER, "mods/" + this.id);
            if(!modFolder.exists()) {
                modFolder.mkdir();
            }

            File info = new File(modFolder, "info.yaml");
            if(!info.exists()) {
                info.createNewFile();
            }

            YamlConfiguration config = YamlConfiguration.loadConfiguration(info);
            config.set("name", this.name);
            config.set("author", this.author);
            config.set("version", this.version);
            config.set("description", this.description);
            config.save(info);


            File functions = new File(modFolder, "functions.kz");
            if(!functions.exists()) {
                functions.createNewFile();
            }
            YamlConfiguration functionsConfig = YamlConfiguration.loadConfiguration(functions);
            functionsConfig.set("functions", null);
            Map<String, ModFunction> functionIdList = new HashMap<>(); // generate random unique id for each function
            for(ModFunction function : this.functions) {
                String id = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
                while (functionIdList.containsKey(id))
                    id = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
                functionIdList.put(id, function);
            }

            for(Map.Entry<String, ModFunction> entry : functionIdList.entrySet()) {
                functionsConfig.createSection("functions." + entry.getKey());
                entry.getValue().save(functionsConfig.getConfigurationSection("functions." + entry.getKey()));
            }

            functionsConfig.save(functions);
        } catch(IOException e){
            KZClient.getLogger().severe("Failed to save mod: " + this.name);
            e.printStackTrace();
        }
    }

    public void loadFunctions(List<ModFunction> functions) {
        this.functions = functions;
    }

    public void loadFunctions() {
        File functions = new File(KZClient.STATIC_FOLDER, "mods/" + this.id + "/functions.kz");
        if(!functions.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(functions);
        if(!config.contains("functions")) {
            KZClient.getLogger().info("No functions found for mod: " + this.name);
            return;
        }

        for(String key : config.getConfigurationSection("functions").getKeys(false)) {
            ModFunction function = FunctionManager.getFunction(config.getInt("functions." + key + ".id"));
            function.deserialize(config.getConfigurationSection("functions." + key));
            this.functions.add(function);
        }

    }

    public Collection<ModFunction> getFunctions() {
        return Collections.unmodifiableCollection(this.functions);
    }
}
