package kzclient.mod.functions.point;

import kzclient.mod.functions.ModFunction;
import kzclient.util.SerializeUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Point {

    public ModFunction parent;
    public int x, y, width, height;
    public int color = Color.RED.getRGB();
    public List<Point> connections = new ArrayList<>();

    public Point(ModFunction parent) {
        this.parent = parent;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.x);
        builder.append("=!");
        builder.append(this.y);
        builder.append("=!");
        builder.append(this.width);
        builder.append("=!");
        builder.append(this.height);
        builder.append("=!");
        builder.append(this.color);
        builder.append("=!");
        builder.append(this.connections.size());
        builder.append("=!");
        for (Point point : this.connections) {
            builder.append(point.getClass().getSimpleName());
            builder.append(point.serialize());
            builder.append("=!");
        }
        return SerializeUtil.serialize(builder.toString());
    }

    public void deserialize(String data) {
        String[] parts = SerializeUtil.deserialize(data).split("=!");
        this.x = Integer.parseInt(parts[0]);
        this.y = Integer.parseInt(parts[1]);
        this.width = Integer.parseInt(parts[2]);
        this.height = Integer.parseInt(parts[3]);
        this.color = Integer.parseInt(parts[4]);
        int connections = Integer.parseInt(parts[5]);
        int index = 6;
        for (int i = 0; i < connections; i++) {
            String type = parts[index];
            Point point;
            switch (type) {
                case "FromPoint":
                    point = new FromPoint(this.parent);
                    break;
                case "ToPoint":
                    point = new ToPoint(this.parent);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown point type: " + type);
            }
            point.deserialize(parts[index + 1]);
            this.connections.add(point);
            index += 2;
        }
    }
}
