package jimwu.bouncingball;

import java.awt.*;

// Ball Model
public class Ball {
    private double x, y;;
    private int radius;
    private double dx, dy;
    private Color color;

    public Ball(int x, int y, int radius, int dx, int dy, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.dx = dx;
        this.dy = dy;
        this.color = color;
    }

    public void move(Rectangle bounds) {
        x += dx;
        y += dy;

        // Bounce off left and right walls
        if (x <= bounds.x || x >= bounds.width - radius * 2) {
            x = Math.max(1, Math.min(x, bounds.width - radius * 2 - 1));
            dx = -dx;
        }
        // Bounce off top and bottom walls
        if (y <= bounds.y || y >= bounds.height - radius * 2) {
            y = Math.max(1, Math.min(y, bounds.height - radius * 2 - 1));
            dy = -dy;
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public double getMass() {
        return radius * radius;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}