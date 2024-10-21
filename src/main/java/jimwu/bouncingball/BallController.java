package jimwu.bouncingball;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class BallController implements ActionListener, MouseListener {
    private JFrame frame;
    private BallView ballPanel;
    private List<Ball> balls;
    private Timer timer;
    private final int FRAME_DELAY = 25; // Approx. 40 FPS

    private final ExecutorService executor;
    private boolean isRunning = true;

    private int WIDTH = 800;
    private int HEIGHT = 800;

    public BallController() {
        initializeModel();
        initializeView();
        initializeController();

        // Create a fixed thread pool, size can be number of available processors
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    private void initializeModel() {
        balls = new ArrayList<>();
        Random rand = new Random();
        int radius = 16;

        for (int i = 0; i < 10; i++) {
            int x, y;
            boolean overlaps;

            do {
                x = rand.nextInt(WIDTH - radius * 2);
                y = rand.nextInt(HEIGHT - radius * 2);
                overlaps = false;

                for (Ball ball : balls) {
                    int dx = (int)(x - ball.getX());
                    int dy = (int)(y - ball.getY());
                    if (Math.hypot(dx, dy) < radius * 2) {
                        overlaps = true;
                        break;
                    }
                }
            } while (overlaps);

            int dx = rand.nextInt(4) + 1;
            int dy = rand.nextInt(4) + 1;
            dx *= rand.nextBoolean() ? 1 : -1;
            dy *= rand.nextBoolean() ? 1 : -1;

            // Generate a random color using HSB
            float hue = rand.nextFloat(); // 0.0 to 1.0
            float saturation = 0.5f + rand.nextFloat() * 0.5f; // 0.5 to 1.0
            float brightness = 0.7f + rand.nextFloat() * 0.3f; // 0.7 to 1.0
            Color color = Color.getHSBColor(hue, saturation, brightness);

            balls.add(new Ball(x, y, radius, dx, dy, color));
        }
    }

    private void initializeView() {
        frame = new JFrame("Bouncing Balls");
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ballPanel = new BallView(balls);
        frame.add(ballPanel, BorderLayout.CENTER);

        // Key listener for "Esc" key and "Space" key
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    exitApplication();
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    togglePause(); // Pause or restart the animation when Space is pressed
                }
            }
        });

        frame.setResizable(true);
        frame.setVisible(true);
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }

    private void togglePause() {
        if (isRunning) {
            timer.stop(); // Stop the timer to pause the animation
            isRunning = false;
        } else {
            timer.start(); // Restart the timer to resume the animation
            isRunning = true;

        }
    }

    private void initializeController() {
        timer = new Timer(FRAME_DELAY, e -> update());
        timer.start();
    }

    private void update() {
        Rectangle bounds = ballPanel.getBounds();

        // Submit move tasks for each ball
        for (Ball ball : balls) {
            executor.submit(() -> {
                synchronized (ball) { // Synchronize on the ball to prevent concurrent modifications
                    ball.move(bounds);
                }
            });
        }

        executor.submit(() -> {
            try {
                // Use a barrier or other synchronization method if necessary
                // For simplicity, assuming all move tasks are quick and collisions can be handled immediately
                handleCollisions();
                // Repaint should be done on the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> ballPanel.repaint());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void exitApplication() {
        if (timer != null) {
            timer.stop(); // Stop the animation timer
        }

        if (executor != null && !executor.isShutdown()) {
            executor.shutdown(); // Gracefully shut down the ExecutorService
            try {
                // Wait a few seconds for all tasks to complete
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    executor.shutdownNow(); // Force shutdown if tasks do not complete
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }

        frame.dispose(); // Close the application window
        System.exit(0);  // Terminate the program completely
    }

    private void handleCollisions() {
        int size = balls.size();
        for (int i = 0; i < size; i++) {
            Ball ballA = balls.get(i);
            for (int j = i + 1; j < size; j++) {
                Ball ballB = balls.get(j);
                double dx = ballB.getX() - ballA.getX();
                double dy = ballB.getY() - ballA.getY();
                double distance = Math.hypot(dx, dy);
                double minDist = ballA.getRadius() * 2;

                if (distance < minDist) {
                    resolveCollision(ballA, ballB);
                }
            }
        }
    }

    private void resolveCollision(Ball ballA, Ball ballB) {
        double dx = ballB.getX() - ballA.getX();
        double dy = ballB.getY() - ballA.getY();
        double distance = Math.hypot(dx, dy);

        if (distance == 0.0) {
            distance = 0.1;
            dx = 1.0;
            dy = 0.0;
        }

        double nx = dx / distance;
        double ny = dy / distance;

        double dvx = ballA.getDx() - ballB.getDx();
        double dvy = ballA.getDy() - ballB.getDy();

        double p = 2.0 * (dvx * nx + dvy * ny) / (ballA.getMass() + ballB.getMass());

        synchronized (ballA) {
            synchronized (ballB) {
                ballA.setDx(ballA.getDx() - p * ballB.getMass() * nx);
                ballA.setDy(ballA.getDy() - p * ballB.getMass() * ny);
                ballB.setDx(ballB.getDx() + p * ballA.getMass() * nx);
                ballB.setDy(ballB.getDy() + p * ballA.getMass() * ny);

                double overlap = 0.5 * (ballA.getRadius() * 2 - distance + 1);
                ballA.setX(ballA.getX() - overlap * nx);
                ballA.setY(ballA.getY() - overlap * ny);
                ballB.setX(ballB.getX() + overlap * nx);
                ballB.setY(ballB.getY() + overlap * ny);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}