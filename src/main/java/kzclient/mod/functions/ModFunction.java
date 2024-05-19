package kzclient.mod.functions;

import kzclient.util.SerializeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public abstract class ModFunction {

    public final List<Point> from = new ArrayList<>();
    public final List<Point> to = new ArrayList<>();
    public int x, y, width = 100, height = 100;
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


    public void save(ConfigurationSection section) {
        section.set("id", FunctionManager.getFunctionId(this));
        section.set("x", this.x);
        section.set("y", this.y);
        section.set("width", this.width);
        section.set("height", this.height);
        section.set("fromSize", this.from.size());
        for (int i = 0; i < this.from.size(); i++) {
            this.from.get(i).save(section.createSection("from." + i));
        }
        section.set("toSize", this.to.size());
        for (int i = 0; i < this.to.size(); i++) {
            this.to.get(i).save(section.createSection("to." + i));
        }
    }

    public void deserialize(ConfigurationSection section) {
        this.x = section.getInt("x");
        this.y = section.getInt("y");
        this.width = section.getInt("width");
        this.height = section.getInt("height");
        int from = section.getInt("fromSize");
        for (int i = 0; i < from; i++) {
            Point point = new Point(this, Point.PointType.FROM_POINT);
            point.deserialize(section.getConfigurationSection("from." + i));
            this.from.add(point);
        }
        int to = section.getInt("toSize");
        for (int i = 0; i < to; i++) {
            Point point = new Point(this, Point.PointType.TO_POINT);
            point.deserialize(section.getConfigurationSection("to." + i));
            this.to.add(point);
        }
    }

    public void init() {

        this._init();
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        if(from.stream().anyMatch(point -> point.isMouseOver(mouseX, mouseY))) return false;
        if(to.stream().anyMatch(point -> point.isMouseOver(mouseX, mouseY))) return false;

        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Point getPointAt(int mouseX, int mouseY) {
        for (Point point : from) {
            if (point.isMouseOver(mouseX, mouseY)) {
                return point;
            }
        }
        for (Point point : to) {
            if (point.isMouseOver(mouseX, mouseY)) {
                return point;
            }
        }
        return null;
    }

}
