package kzclient.mod.functions.impl;

import kzclient.mod.functions.ModFunction;
import kzclient.mod.functions.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import java.awt.*;

public class SayMessage extends ModFunction {

    private GuiTextField textField;

    public SayMessage() {
        textField = new GuiTextField(0, fontRenderer, x + 10, y + 10, 80, 20);
    }

    @Override
    public void draw(GuiScreen screen, int x, int y, int mouseX, int mouseY) {
        Gui.drawRect(x, y, x + this.width, y + this.height, Color.black.getRGB());
        textField.drawTextBox();
        textField.xPosition = x + 10;
        textField.yPosition = y + 10;

        for (int i = 0; i < this.from.size(); i++) {
            Point from = this.from.get(i);
            from.x = x;
            from.y = y + (i * 20); // Adjust the y-coordinate based on the index
            from.width = 10;
            from.height = 10;
            Gui.drawRect(from.x, from.y, from.x + from.width, from.y + from.height, Color.red.getRGB());
        }

        // Draw ToPoints
        for (int i = 0; i < this.to.size(); i++) {
            Point to = this.to.get(i);
            to.width = 10;
            to.height = 10;
            to.x = x + this.width - to.width;
            to.y = y + (i * 20); // Adjust the y-coordinate based on the index
            Gui.drawRect(to.x, to.y, to.x + to.width, to.y + to.height, Color.green.getRGB());
        }

        screen.drawCenteredString(fontRenderer, "SAY MSG", x + this.width / 2, y + this.height / 2 - 10, Color.white.getRGB());

        this.x = x;
        this.y = y;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(textField != null) textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(textField != null) textField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void fire() {
        // do nothing
        Minecraft.getMinecraft().thePlayer.sendChatMessage(textField.getText());
    }

    @Override
    public SayMessage _copy() {
        SayMessage copy = new SayMessage();
        copy.x = this.x;
        copy.y = this.y;
        copy.width = this.width;
        copy.height = this.height;
        return copy;
    }

    @Override
    public int getHeight() {
        return 100;
    }

    @Override
    public void _init() {
        this.from.add(new Point(this, Point.PointType.FROM_POINT));
    }
}
