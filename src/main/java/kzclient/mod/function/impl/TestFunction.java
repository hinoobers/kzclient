package kzclient.mod.function.impl;

import kzclient.mod.function.ModFunction;
import kzclient.mod.function.ObjectRegistry;
import kzclient.mod.function.slot.InputSlot;
import kzclient.mod.function.slot.OutputSlot;
import net.minecraft.client.gui.Gui;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

public class TestFunction extends ModFunction {


    @Override
    public void draw(int mouseX, int mouseY, int x, int y) {
        Gui.drawRect(x, y, x + 100, y + 75, 0xFF00FF00);

        int i = 0;
        for(InputSlot input : this.inputs) {
            input.draw(mouseX, mouseY, x, y + i);

            i += 15; // 10 + 5
        }

        i = 0;
        for(OutputSlot output : outputs) {
            output.draw(mouseX, mouseY, x + 100, y + i);
            i += 15;
        }
    }

    @Override
    public void drawForDisplay(int mouseX, int mouseY, int x, int y) {
        Gui.drawRect(x, y, x + 100, y + 75, 0xFF00FF00);
        // draw inputs & outputs, this is for display,
        Gui.drawRect(x, y, x + 10, y + 10, 0xFF0000FF);

        // output
        Gui.drawRect(x + 90, y + 65, x + 100, y + 75, 0xFFFF0000);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void _load(ConfigurationSection section) {

    }

    @Override
    public void _save(ConfigurationSection section) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void fire(ObjectRegistry registry) {

    }

    @Override
    public void prepare() {

    }

    @Override
    public ModFunction _copy() {
        TestFunction function = new TestFunction();
        function.setId(this.getId());
        return function;
    }

    @Override
    public void _loadSlots() {
        this.inputs.add(new InputSlot());
        this.outputs.add(new OutputSlot());
    }
}
