package kzclient.mod.function;

import kzclient.mod.Mod;
import kzclient.mod.function.slot.InputSlot;
import kzclient.mod.function.slot.OutputSlot;
import kzclient.mod.function.slot.Slot;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public abstract class ModFunction {

    @Getter protected final List<InputSlot> inputs = new ArrayList<>();
    @Getter protected final List<OutputSlot> outputs = new ArrayList<>();

    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
    private int id;
    private Integer uniqueId;
    public Mod parentMod;

    // Visual
    public int x, y, width = 100, height = 75;

    public boolean isMouseOver(int mouseX, int mouseY) {
        for(InputSlot input : this.inputs) {
            if(input.isMouseOver(mouseX, mouseY)) {
                return false;
            }
        }
        for(OutputSlot output : this.outputs) {
            if(output.isMouseOver(mouseX, mouseY)) {
                return false;
            }
        }

        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Slot getSlot(int mouseX, int mouseY) {
        for(InputSlot input : this.inputs) {
            if(input.isMouseOver(mouseX, mouseY)) {
                return input;
            }
        }
        for(OutputSlot output : this.outputs) {
            if(output.isMouseOver(mouseX, mouseY)) {
                return output;
            }
        }

        return null;
    }

    public void loadSlots() {
        _loadSlots();
        this.inputs.forEach(e -> e.parentFunction = this);
        this.outputs.forEach(e -> e.parentFunction = this);
    }


    public abstract void draw(int mouseX, int mouseY, int x, int y);
    public abstract void drawForDisplay(int mouseX, int mouseY, int x, int y);
    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);
    public abstract void keyTyped(char typedChar, int keyCode);
    public abstract void fire(ObjectRegistry registry);
    public abstract void prepare();
    public abstract void _loadSlots(); // inputs & outputs
    public abstract void _save(ConfigurationSection section);
    public abstract void _load(ConfigurationSection section);
    public abstract ModFunction _copy();

    public void save(ConfigurationSection section) {
        section.set("uniq_id", this.uniqueId);
        section.set("x", this.x);
        section.set("y", this.y);
        section.set("width", this.width);
        section.set("height", this.height);
        section.set("inputs", this.inputs.size());

        int i = 0;
        for(InputSlot input : this.inputs) {
            input.save(section.createSection("input." + i));
            i++;
        }

        i = 0;
        section.set("outputs", this.outputs.size());
        for(OutputSlot output : this.outputs) {
            output.save(section.createSection("output." + i));
            i++;
        }

        _save(section.createSection("data"));
    }

    public void deserialize(ConfigurationSection section) {
        this.uniqueId = section.getInt("uniq_id");
        this.x = section.getInt("x");
        this.y = section.getInt("y");
        this.width = section.getInt("width");
        this.height = section.getInt("height");

        int inputs = section.getInt("inputs");
        for(int i = 0; i < inputs; i++) {
            InputSlot input = new InputSlot();
            input.parentFunction = this;
            input.deserialize(section.getConfigurationSection("input." + i));
            this.inputs.add(input);
        }

        int outputs = section.getInt("outputs");
        for(int i = 0; i < outputs; i++) {
            OutputSlot output = new OutputSlot();
            output.parentFunction = this;
            output.deserialize(section.getConfigurationSection("output." + i));
            this.outputs.add(output);
        }

        if(section.isConfigurationSection("data")) {
            _load(section.getConfigurationSection("data"));
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public Integer getUniqueId() {
        System.out.println("Get unique id: " + this.uniqueId + " (" + this + ")");
        return this.uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        System.out.println("Setting unique id: " + uniqueId + " (" + this + ")");
        this.uniqueId = uniqueId;
    }

    public void setMod(Mod mod) {
        this.parentMod = mod;
    }
}
