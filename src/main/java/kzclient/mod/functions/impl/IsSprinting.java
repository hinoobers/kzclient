package kzclient.mod.functions.impl;

import kzclient.mod.functions.ModFunction;
import kzclient.mod.functions.point.FromPoint;
import kzclient.mod.functions.point.ToPoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class IsSprinting extends ModFunction {

    private ToPoint yes;
    private ToPoint no;

    public IsSprinting() {

    }

    @Override
    public void draw(GuiScreen screen, int x, int y, int mouseX, int mouseY) {
        Gui.drawRect(x, y, x + this.width, y + this.height, Color.black.getRGB());

        for (int i = 0; i < this.from.size(); i++) {
            FromPoint from = this.from.get(i);
            from.x = x;
            from.y = y + (i * 20); // Adjust the y-coordinate based on the index
            from.width = 10;
            from.height = 10;
            Gui.drawRect(from.x, from.y, from.x + from.width, from.y + from.height, Color.red.getRGB());
        }

        // Draw ToPoints
        for (int i = 0; i < this.to.size(); i++) {
            ToPoint to = this.to.get(i);
            to.width = 10;
            to.height = 10;
            to.x = x + this.width - to.width;
            to.y = y + (i * 20); // Adjust the y-coordinate based on the index
            Gui.drawRect(to.x, to.y, to.x + to.width, to.y + to.height, Color.green.getRGB());
        }

        screen.drawCenteredString(fontRenderer, "IS SPRINTING", x + this.width / 2, y + this.height / 2 - 10, Color.white.getRGB());

        this.x = x;
        this.y = y;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void fire() {
        if(Minecraft.getMinecraft().thePlayer.isSprinting()) {
            yes.connections.forEach(point -> point.parent.fire());
        } else {
            no.connections.forEach(point -> point.parent.fire());
        }
    }

    @Override
    public String _serialize() {
        return null;
    }

    @Override
    public void _deserialize(String data) {

    }

    @Override
    public IsSprinting _copy() {
        IsSprinting copy = new IsSprinting();
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
        this.from.add(new FromPoint(this));
        this.to.add(yes = new ToPoint(this));
        this.to.add(no = new ToPoint(this));
        yes.color = Color.green.getRGB();
        no.color = Color.red.getRGB();

        this.width = 100;
        this.height = 100;
    }
}