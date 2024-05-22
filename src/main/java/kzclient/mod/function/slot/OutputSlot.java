package kzclient.mod.function.slot;

import jdk.internal.util.xml.impl.Input;
import kzclient.mod.function.ConnectionLine;
import kzclient.mod.function.ModFunction;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class OutputSlot implements Slot{

    @Getter
    private final List<ConnectionLine> lines = new ArrayList<>();

    public ModFunction parentFunction;
    public int uniqueId;
    public int x, y, width = 10, height = 10;

    @Override
    public void connectInput(InputSlot input) {
        ConnectionLine line = new ConnectionLine();
        line.input = input;
        line.output = this;
        this.lines.add(line);
    }

    public void draw(int mouseX, int mouseY, int x, int y) {
        // draw 10x10 square

        Gui.drawRect(x - 10, y, x, y + 10, 0xFFFF0000);

        this.x = x - 10;
        this.y = y;
    }

    public void save(ConfigurationSection section) {
        section.set("x", x);
        section.set("y", y);
        section.set("uniq_id", this.uniqueId);
        section.set("connection_lines", this.lines.size());
        int i = 0;
        for(ConnectionLine l : this.lines) {
            l.save(section.createSection("connection_line." + i));
            i++;
        }
    }

    public void deserialize(ConfigurationSection section) {
        this.x = section.getInt("x");
        this.y = section.getInt("y");
        this.uniqueId = section.getInt("uniq_id");
        int size = section.getInt("connection_lines");

        this.lines.clear(); // might be called twice
        for(int i = 0; i < size; i++) {
            ConnectionLine line = new ConnectionLine();
            line.load(this.parentFunction.parentMod, section.getConfigurationSection("connection_line." + i));
            this.lines.add(line);
        }
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
