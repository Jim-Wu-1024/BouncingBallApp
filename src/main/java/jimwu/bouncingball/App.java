package jimwu.bouncingball;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
    private static final int BALL_COUNT = 10;
    private static final int BALL_RADIUS = 15;
    private static int windowWidth = 600;
    private static int windowHeight = 600;

    public static void main(String[] args) {
        // Create Model (balls)
        List<Ball> balls = createNonOverlappingBalls(BALL_COUNT, BALL_RADIUS, windowWidth, windowHeight);

        // Create Controller
        BallController controller = getBallController(balls);

        // Start animation
        controller.start();
    }

    // Create a list of non-overlapping balls
    private static List<Ball> createNonOverlappingBalls(int ballCount, int ballRadius, int windowWidth, int windowHeight) {
        List<Ball> balls = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < ballCount; i++) {
            Ball newBall = null;
            boolean overlaps;

            // Keep trying to generate a new ball that does not overlap with the existing ones
            int attempts = 0;
            do {
                // Generate a random position for the new ball's center
                int x = random.nextInt(windowWidth - 2 * ballRadius);
                int y = random.nextInt(windowHeight - 2 * ballRadius);

                attempts++;
                if (attempts > 1000) {
                    System.out.println("Too many attempts, placing ball anyway.");
                    break;
                }

                // Create the new ball with random speed and color
                newBall = new Ball(x, y, random.nextInt(5) + 1, random.nextInt(5) + 1, ballRadius, randomColor(random));

                // Check if this new ball overlaps with any of the existing balls
                overlaps = false;
                for (Ball existingBall : balls) {
                    if (areBallsOverlapping(newBall, existingBall)) {
                        overlaps = true;
                        break;
                    }
                }
            } while (overlaps);  // If the ball overlaps, regenerate its position

            // Add the new non-overlapping ball to the list
            balls.add(newBall);
        }

        return balls;
    }

    // Check if two balls are overlapping
    private static boolean areBallsOverlapping(Ball ball1, Ball ball2) {
        int dx = ball1.getX() - ball2.getX();
        int dy = ball1.getY() - ball2.getY();
        int distanceSquared = dx * dx + dy * dy;

        // Check if the distance between centers is less than the sum of their radii
        int radiusSum = ball1.getRadius() + ball2.getRadius();
        return distanceSquared < radiusSum * radiusSum;  // Strictly less to ensure they don't touch
    }

    // Generate a random color
    private static java.awt.Color randomColor(Random random) {
        return new java.awt.Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    private static BallController getBallController(List<Ball> balls) {
        BallView view = new BallView(balls, windowWidth, windowHeight);

        // Create Controller
        BallController controller = new BallController(balls, view);

        // Setup JFrame
        JFrame frame = new JFrame("Bouncing Ball");
        frame.add(view);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Add window listener for graceful exit
        frame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.stop();
                System.exit(0);
            }
        });

        // Add KeyListener to listen for space presses
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Not needed for space functionality
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // Toggle pause when the spacebar is pressed
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    controller.togglePause();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Not needed for space functionality
            }
        });

        // Ensure the JFrame can receive keyboard input
        frame.setFocusable(true);
        frame.requestFocusInWindow();

        // Add ComponentListener to handle window resizing
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Update ball boundaries according to new window size
                Dimension newSize = frame.getSize();
                controller.updateWindowSize(newSize.width, newSize.height - 50); // Adjust for header size
            }
        });


        return controller;
    }
}