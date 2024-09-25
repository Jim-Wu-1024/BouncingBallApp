package jimwu.bouncingball;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class BallController implements ActionListener, MouseListener {
    private BallView view;
    private List<Ball> balls;
    private Timer timer;
    private boolean isPaused = false;
    private int windowWidth;
    private int windowHeight;

    public BallController(List<Ball> balls, BallView view) {
        this.balls = balls;
        this.view = view;
        this.windowWidth = view.getWidth();
        this.windowHeight = view.getHeight();
        this.view.addMouseListener(this);
        this.timer = new Timer(1000 / 40, this);
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    public void togglePause() {
        isPaused = !isPaused;
    }

    // Update window size when the user resizes the frame
    public void updateWindowSize(int newWidth, int newHeight) {
        this.windowWidth = newWidth;
        this.windowHeight = newHeight;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused) {
            for (Ball ball : balls) {
                ball.move(view.getWidth(), view.getHeight());
            }
            checkCollisions();
        }
        view.repaint();
    }

    // Method to check collisions between balls
    private void checkCollisions() {
        for (int i = 0; i < balls.size(); i++) {
            Ball ball1 = balls.get(i);
            for (int j = i + 1; j < balls.size(); j++) {
                Ball ball2 = balls.get(j);

                // Check if the two balls are colliding
                if (areColliding(ball1, ball2)) {
                    handleCollision(ball1, ball2);
                }
            }
        }
    }

    // Check if two balls are colliding
    private boolean areColliding(Ball ball1, Ball ball2) {
        int dx = ball1.getX() - ball2.getX();
        int dy = ball1.getY() - ball2.getY();
        int distanceSquared = dx * dx + dy * dy;

        // Check if the distance between the centers is less than the sum of their radii
        int radiusSum = ball1.getRadius() + ball2.getRadius();
        return distanceSquared <= radiusSum * radiusSum;
    }

    // Handle the collision by reversing the velocities
    private void handleCollision(Ball ball1, Ball ball2) {
        // Calculate the vector between the two balls' centers
        int dx = ball2.getX() - ball1.getX();
        int dy = ball2.getY() - ball1.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Calculate the overlap (how much the balls are intersecting)
        double overlap = (ball1.getRadius() + ball2.getRadius()) - distance;

        // Separate the balls so that they no longer overlap
        if (overlap > 0) {
            // Normalized direction vector between the balls
            double nx = dx / distance;
            double ny = dy / distance;

            // Move each ball away from the collision point by half of the overlap distance
            ball1.setX(ball1.getX() - (int)(nx * overlap / 2));
            ball1.setY(ball1.getY() - (int)(ny * overlap / 2));
            ball2.setX(ball2.getX() + (int)(nx * overlap / 2));
            ball2.setY(ball2.getY() + (int)(ny * overlap / 2));
        }

        // Simple collision response: swap velocities of the balls
        ball1.reverseDirection();
        ball2.reverseDirection();
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        for (Ball ball : balls) {
            if (ball.isClicked(mouseX, mouseY)) {
                ball.reverseDirection();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
