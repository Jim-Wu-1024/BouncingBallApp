package jimwu.bouncingball;

import java.awt.*;

public class Ball {
    private int x, y, speedX, speedY, radius;
    private Color color;

    public Ball(int x, int y, int speedX, int speedY, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.radius = radius;
        this.color = color;
    }

    public void move(int windowWidth, int windowHeight) {
        x += speedX;
        y += speedY;

        // Bounce off the walls
        if (x <= 0 || x >= windowWidth - 2 * radius) {
            x = Math.max(1, Math.min(x, windowWidth - 2 * radius - 1));
            speedX = -speedX;
        }
        if (y <= 0 || y >= windowHeight - 2 * radius) {
            y = Math.max(1, Math.min(y, windowHeight - 2 * radius - 1));
            speedY = -speedY;
        }
    }

    public void reverseDirection() {
        speedX = -speedX;
        speedY = -speedY;
    }

    public boolean isClicked(int mouseX, int mouseY) {
        int tolerance = 15;
        return mouseX >= x - tolerance && mouseX <= x + 2 * radius + tolerance  &&
                mouseY >= y - tolerance && mouseY <= y + 2 * radius + tolerance;
    }

    // Setters
    public void setX(int x) {this.x = x;}
    public void setY(int y) {this.y = y;}

    // Getters
    public int getX() {return x;}
    public int getY() {return y;}
    public int getRadius() {return radius;}
    public Color getColor() {return color;}
}
