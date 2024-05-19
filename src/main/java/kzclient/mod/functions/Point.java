package kzclient.mod.functions;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import kzclient.gui.VisualInterface;
import kzclient.util.SerializeUtil;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Point extends VisualInterface {

    public ModFunction parent;
    public PointType type;
    public List<Point> connections = new ArrayList<>();

    public Point(ModFunction parent, PointType type) {
        this.parent = parent;
        this.type = type;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void save(ConfigurationSection section) {
        section.set("x", this.x);
        section.set("y", this.y);
        section.set("width", this.width);
        section.set("height", this.height);
        section.set("color", this.color);
        if(this.type != null) {
            section.set("type", this.type.name());
        }
        section.set("connections", this.connections.size());
        for (int i = 0; i < this.connections.size(); i++) {
            this.connections.get(i).save(section.createSection("connection." + i));
        }
    }

    public void deserialize(ConfigurationSection section) {
        this.x = section.getInt("x");
        this.y = section.getInt("y");
        this.width = section.getInt("width");
        this.height = section.getInt("height");
        this.color = section.getInt("color");
        if(section.contains("type")) {
            this.type = PointType.valueOf(section.getString("type").toUpperCase());
        }
        int connections = section.getInt("connections");
        for (int i = 0; i < connections; i++) {
            Point point = new Point(this.parent, this.type);
            point.deserialize(section.getConfigurationSection("connection." + i));
            this.connections.add(point);

            System.out.println("Added a connection! " + this.type);
        }
    }

    public enum PointType {
        FROM_POINT,
        TO_POINT
    }
}
