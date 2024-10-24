package jimwu.bouncingball;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Ball View
public class BallView extends JPanel {
    private List<Ball> balls;

    public BallView(List<Ball> balls) {
        this.balls = balls;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (Ball ball : balls) {
            g2.setColor(ball.getColor());
            g2.fillOval((int)ball.getX(), (int)ball.getY(), ball.getRadius() * 2, ball.getRadius() * 2);
        }
    }
}