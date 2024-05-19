package kzclient.mod;

import kzclient.KZClient;
import kzclient.mod.functions.ModFunction;
import kzclient.mod.functions.impl.IsSprinting;
import kzclient.mod.functions.impl.StartSprinting;
import kzclient.mod.functions.impl.event.PreTick;
import kzclient.mod.functions.impl.event.StartSprintingEvent;
import kzclient.util.SerializeUtil;
import lombok.Getter;
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

            // save to yaml
            Yaml yaml = new Yaml();
            // only store name, author, version, and description
            Map<String, String> data = new HashMap<>();
            data.put("name", this.name);
            data.put("author", this.author);
            data.put("version", this.version);
            data.put("description", this.description);
            yaml.dump(data, Files.newBufferedWriter(new File(modFolder, "info.yaml").toPath()));

            File functions = new File(modFolder, "functions.kz");
            if(!functions.exists()) {
                functions.createNewFile();
            }
            PrintWriter writer = new PrintWriter(functions);

            writer.println(this.functions.size());
            for(ModFunction function : this.functions) {
                writer.println(function.serialize());
            }
            writer.flush();
            writer.close();
        } catch(IOException e){
            KZClient.getLogger().severe("Failed to save mod: " + this.name);
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

        try {
            Scanner scanner = new Scanner(functions);
            int size = scanner.nextInt();
            scanner.nextLine(); // Consume newline character
            for (int i = 0; i < size; i++) {
                String data = scanner.nextLine();
                String[] datad = SerializeUtil.deserialize(data).split("=!");
                ModFunction function;

                System.out.println(datad[0].toLowerCase());
                switch (datad[0].toLowerCase()) {
                    case "startsprintingevent":
                        function = new StartSprintingEvent();
                        break;
                    case "pretick":
                        function = new PreTick();
                        break;
                    case "startsprinting":
                        function = new StartSprinting();
                        break;
                    case "issprinting":
                        function = new IsSprinting();
                        break;
                    default:
                        KZClient.getLogger().severe("Failed to load function for mod: " + this.name + " (Unknown function: " + datad[0] + ")");
                        continue;
                }

                function.deserialize(data);
                this.functions.add(function);
            }
        } catch(IOException e) {
            KZClient.getLogger().severe("Failed to lo");
        }

    }

    public Collection<ModFunction> getFunctions() {
        return Collections.unmodifiableCollection(this.functions);
    }
}
