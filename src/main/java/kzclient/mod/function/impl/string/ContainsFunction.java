package kzclient.mod.function.impl.string;

import kzclient.mod.function.ModFunction;
import kzclient.mod.function.ObjectRegistry;
import kzclient.mod.function.slot.InputSlot;
import kzclient.mod.function.slot.OutputSlot;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

public class ContainsFunction extends ModFunction {


    private GuiTextField messageField;

    @Override
    public void draw(int mouseX, int mouseY, int x, int y) {
        Gui.drawRect(x, y, x + 100, y + 75, 0xFF00FF00);
        messageField.xPosition = x + 5;
        messageField.yPosition = y + 5;
        messageField.drawTextBox();

        font.drawString("Contains", x + 5, y + 15, 0xFFFFFFFF);

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

        font.drawString("Contains", x + 5, y + 15, 0xFFFFFFFF);

        // output
        Gui.drawRect(x + 90, y + 65, x + 100, y + 75, 0xFFFF0000);
        Gui.drawRect(x + 90, y + 50, x + 100, y + 60, 0xFFFF0000);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(messageField != null) {
            messageField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(messageField != null) {
            messageField.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void fire(ObjectRegistry registry) {
        String message = registry.get(0).asString();
        if (message != null) {
            System.out.println("Message: '" + message + "' contains: '" + messageField.getText() + "'");
            if(message.contains(messageField.getText())) {
                this.outputs.get(0).getLines().forEach(e -> e.input.parentFunction.fire(null));
            } else {
                this.outputs.get(1).getLines().forEach(e -> e.input.parentFunction.fire(null));
            }
        }
    }

    @Override
    public void prepare() {
        messageField = new GuiTextField(0, font, x + 5, y + 5, 90, 10);
    }

    @Override
    public void _loadSlots() {
        this.inputs.add(new InputSlot());
        this.outputs.add(new OutputSlot());
        this.outputs.add(new OutputSlot());
    }

    @Override
    public void _save(ConfigurationSection section) {
        section.set("message", messageField.getText());
    }

    @Override
    public void _load(ConfigurationSection section) {
        messageField.setText(section.getString("message"));
    }

    @Override
    public ModFunction _copy() {
        ContainsFunction function = new ContainsFunction();
        function.setId(getId());
        return function;
    }
}
