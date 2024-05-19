package kzclient.mod.functions;

import kzclient.mod.functions.point.FromPoint;
import kzclient.mod.functions.point.Point;
import kzclient.mod.functions.point.ToPoint;
import kzclient.util.SerializeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public abstract class ModFunction {

    public final List<FromPoint> from = new ArrayList<>();
    public final List<ToPoint> to = new ArrayList<>();
    public int x, y, width, height;
    protected final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

    public ModFunction() {
    }

    public abstract void draw(GuiScreen screen, int x, int y, int mouseX, int mouseY);
    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);
    public abstract void keyTyped(char typedChar, int keyCode);
    public abstract int getHeight();
    public abstract void _init();
    public abstract ModFunction _copy();
    public abstract void fire();

    public abstract String _serialize();
    public abstract void _deserialize(String data);

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append("=!");
        builder.append(this.x);
        builder.append("=!");
        builder.append(this.y);
        builder.append("=!");
        builder.append(this.width);
        builder.append("=!");
        builder.append(this.height);
        builder.append("=!");
        // points
        builder.append(this.from.size());
        builder.append("=!");
        for (FromPoint point : this.from) {
            builder.append(point.serialize());
            builder.append("=!");
        }
        builder.append(this.to.size());
        builder.append("=!");
        for (ToPoint point : this.to) {
            builder.append(point.serialize());
            builder.append("=!");
        }
        return SerializeUtil.serialize(builder.toString());
    }

    public void deserialize(String data) {
        String[] parts = SerializeUtil.deserialize(data).split("=!");
        // skip first part

        this.x = Integer.parseInt(parts[1]);
        this.y = Integer.parseInt(parts[2]);
        this.width = Integer.parseInt(parts[3]);
        this.height = Integer.parseInt(parts[4]);
        // points
        int fromSize = Integer.parseInt(parts[5]);
        System.out.println("From size: " + fromSize);
        int index = 6;
        for (int i = 0; i < fromSize; i++) {
            FromPoint point = new FromPoint(this);
            point.deserialize(parts[index]);
            this.from.add(point);
            index++;
        }
        int toSize = Integer.parseInt(parts[index]);
        System.out.println("To size: " + toSize);
        index++;
        for (int i = 0; i < toSize; i++) {
            ToPoint point = new ToPoint(this);
            point.deserialize(parts[index]);
            this.to.add(point);
            index++;
        }
    }

    public void init() {
        this.to.clear();
        this.from.clear();
        this._init();
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        if(from.stream().anyMatch(point -> point.isMouseOver(mouseX, mouseY))) return false;
        if(to.stream().anyMatch(point -> point.isMouseOver(mouseX, mouseY))) return false;

        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Point getPointAt(int mouseX, int mouseY) {
        for (FromPoint point : from) {
            if (point.isMouseOver(mouseX, mouseY)) {
                return point;
            }
        }
        for (ToPoint point : to) {
            if (point.isMouseOver(mouseX, mouseY)) {
                return point;
            }
        }
        return null;
    }

}
