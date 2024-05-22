package kzclient.mod.function.impl.event;

import kzclient.mod.function.ModFunction;
import kzclient.mod.function.ObjectRegistry;
import kzclient.mod.function.slot.InputSlot;
import kzclient.mod.function.slot.OutputSlot;
import net.minecraft.client.gui.Gui;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

public class ChatMessageFunction extends ModFunction {


    @Override
    public void draw(int mouseX, int mouseY, int x, int y) {
        Gui.drawRect(x, y, x + 100, y + 75, 0xFF00FF00);

        font.drawString("Chat Msg detect", x + 5, y + 15, 0xFFFFFFFF);
        int i = 0;
        for(OutputSlot output : outputs) {
            output.draw(mouseX, mouseY, x + 100, y + i);
            i += 15;
        }
    }

    @Override
    public void drawForDisplay(int mouseX, int mouseY, int x, int y) {
        Gui.drawRect(x, y, x + 100, y + 75, 0xFF00FF00);
        // draw inputs & outputs, this is for display,
        font.drawString("Chat Msg detect", x + 5, y + 15, 0xFFFFFFFF);

        // output
        Gui.drawRect(x + 90, y + 65, x + 100, y + 75, 0xFFFF0000);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void fire(ObjectRegistry registry) {
        this.outputs.forEach(out -> {
            out.getLines().forEach(e -> e.input.parentFunction.fire(registry));
        });
    }

    @Override
    public void _load(ConfigurationSection section) {

    }

    @Override
    public void _save(ConfigurationSection section) {

    }

    @Override
    public void prepare() {

    }

    @Override
    public void _loadSlots() {
        this.outputs.add(new OutputSlot());
    }

    @Override
    public ModFunction _copy() {
        ChatMessageFunction function = new ChatMessageFunction();
        function.setId(this.getId());
        return function;
    }
}
