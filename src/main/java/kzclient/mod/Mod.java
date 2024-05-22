package kzclient.mod;

import kzclient.KZClient;
import kzclient.mod.function.FunctionManager;
import kzclient.mod.function.ModFunction;
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
    private final Map<Integer, ModFunction> functions = new HashMap<>();

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

            for(Map.Entry<Integer, ModFunction> entry : this.functions.entrySet()) {
                functionsConfig.createSection("functions." + entry.getKey());
                functionsConfig.set("functions." + entry.getKey() + ".id", FunctionManager.getFunctionId(entry.getValue()));
                entry.getValue().save(functionsConfig.getConfigurationSection("functions." + entry.getKey()));
            }

            functionsConfig.save(functions);
        } catch(IOException e){
            KZClient.getLogger().severe("Failed to save mod: " + this.name);
            e.printStackTrace();
        }
    }

    // This is for a new mod
    public void loadFunctions(List<ModFunction> functions) {
        for(ModFunction function : functions) {
            if(function.getUniqueId() == null) {
                int id = -6969 + (this.functions.size());
                while(this.functions.containsKey(id)) {
                    id++;
                }
                function.setUniqueId(id);
            }
            function.setId(FunctionManager.getFunctionId(function));
            function.setMod(this);
            this.functions.put(function.getUniqueId(), function);
        }
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
            function.setMod(this);
            function.setUniqueId(Integer.parseInt(key));
            //function.loadSlots(); saved in deserialize (might change it later on how it handles)
            function.prepare();
            function.deserialize(config.getConfigurationSection("functions." + key));

            this.functions.put(Integer.parseInt(key), function);
            function.setId(FunctionManager.getFunctionId(function));
        }

        // post-load, so all functions are loaded & found
        for(ModFunction function : this.functions.values()) {
            function.getOutputs().forEach(e -> e.deserialize(config.getConfigurationSection("functions." + function.getUniqueId() + ".output." + e.uniqueId)));
            function.getInputs().forEach(e -> e.deserialize(config.getConfigurationSection("functions." + function.getUniqueId() + ".input." + e.uniqueId)));
        }

        System.out.println("Functions");
        for(Map.Entry<Integer, ModFunction> entry : this.functions.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue().getId());
        }
    }

    public Collection<ModFunction> getFunctions() {
        return Collections.unmodifiableCollection(this.functions.values());
    }
}
