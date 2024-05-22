package kzclient.mod.function.impl;

import kzclient.mod.function.ModFunction;
import kzclient.mod.function.ObjectRegistry;
import kzclient.mod.function.impl.event.PostMotionFunction;
import kzclient.mod.function.slot.InputSlot;
import kzclient.mod.function.slot.OutputSlot;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

public class SayMessageFunction extends ModFunction {

    private GuiTextField messageField;

    @Override
    public void draw(int mouseX, int mouseY, int x, int y) {
        Gui.drawRect(x, y, x + 100, y + 75, 0xFF00FF00);
        messageField.xPosition = x + 5;
        messageField.yPosition = y + 5;
        messageField.drawTextBox();

        font.drawString("Say Message", x + 5, y + 15, 0xFFFFFFFF);

        int i = 0;
        for(InputSlot input : this.inputs) {
            input.draw(mouseX, mouseY, x, y + i);

            i += 15; // 10 + 5
        }
    }

    @Override
    public void drawForDisplay(int mouseX, int mouseY, int x, int y) {
        Gui.drawRect(x, y, x + 100, y + 75, 0xFF00FF00);


        font.drawString("Say Message", x + 5, y + 15, 0xFFFFFFFF);
        // draw inputs & outputs, this is for display,
        Gui.drawRect(x, y, x + 10, y + 10, 0xFF0000FF);
    }

    @Override
    public void fire(ObjectRegistry registry) {
        mc.thePlayer.sendChatMessage(messageField.getText());
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(messageField != null) {
            messageField.textboxKeyTyped(typedChar, keyCode);

        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(messageField != null) {
            messageField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void _load(ConfigurationSection section) {
        messageField.setText(section.getString("message"));
    }

    @Override
    public void _save(ConfigurationSection section) {
        section.set("message", messageField.getText());
    }

    @Override
    public void prepare() {
        messageField = new GuiTextField(0, font, x + 5, y + 5, 90, 10);
    }

    @Override
    public void _loadSlots() {
        this.inputs.add(new InputSlot());
    }

    @Override
    public ModFunction _copy() {
        SayMessageFunction function = new SayMessageFunction();
        function.setId(this.getId());
        return function;
    }
}
