package jimwu.bouncingball;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        int numBalls = 100;
        int poolSize = 8;

        if (args.length >= 1) {
            try {
                numBalls = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number of balls. Using default: 10");
            }
        }

        if (args.length >= 2) {
            try {
                poolSize = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid thread pool size. Using default: Available processors");
            }
        }

        BallController controller = new BallController(numBalls, poolSize);
    }
}
