package jimwu.bouncingball;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BallView extends JPanel{
    private List<Ball> balls;
    private int width ;
    private int height;

    public BallView(List<Ball> balls, int width, int height) {
        this.balls = balls;
        this.width = width;
        this.height = height;
        this.setPreferredSize(new Dimension(this.width, this.height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Ball ball : balls) {
            g.setColor(ball.getColor());
            g.fillOval(ball.getX(), ball.getY(), 2 * ball.getRadius(), 2 * ball.getRadius());
        }
    }
}