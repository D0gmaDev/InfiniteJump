package fr.d0gma.infinite.parkour;

import java.util.Random;

public class Curve {

    private static final int MAX_Y_OFFSET = 50;
    private static final double DIRECTION_SLOPE = 100. / MAX_Y_OFFSET;

    private final Random random;

    private final int baseX, baseY;
    private int X, Y;

    public Curve(Random random, int baseX, int baseY) {
        this.random = random;
        this.baseX = baseX;
        this.baseY = baseY;
        this.X = baseX;
        this.Y = baseY;
    }

    public NextPoint nextPoint(float maxDist, int variance, int maxAngle, int maxDescent, int maxAscent) {

        int diffY = diffY(maxDescent, maxAscent);

        float dist = maxDist - random.nextInt(variance + 1) - diffY;

        int absMaxAngle = Math.abs(maxAngle);
        int deg = absMaxAngle == 0 ? 0 : random.nextInt(absMaxAngle * 2) - absMaxAngle;

        int diffZ = (int) Math.round(Math.cos(Math.toRadians(deg)) * dist);
        int diffX = (int) Math.round(Math.sin(Math.toRadians(deg)) * dist);

        moveCurve(diffX, diffY);

        return new NextPoint(diffX, diffY, diffZ, dist + diffY);
    }

    public void moveCurve(int diffX, int diffY) {
        this.X += diffX;
        this.Y += diffY;
    }

    private int diffY(int maxDescent, int maxAscent) {
        return switch (getRandomDirection(maxDescent, maxAscent)) {
            case UP -> random.nextInt(maxAscent) + 1;
            case STRAIGHT -> 0;
            case DOWN -> -(random.nextInt(maxDescent) + 1);
        };
    }

    private Direction getRandomDirection(int maxDescent, int maxAscent) {
        int currentDiff = Y - baseY;
        int threshold = (int) (DIRECTION_SLOPE * currentDiff);

        int choice = random.nextInt(101);

        if (choice < threshold - 15 && maxDescent > 0) {
            return Direction.DOWN;
        }
        if (choice >= threshold + 15 && maxAscent > 0) {
            return Direction.UP;
        }
        return Direction.STRAIGHT;
    }

    private enum Direction {
        UP,
        STRAIGHT,
        DOWN
    }

    public record NextPoint(int diffX, int diffY, int diffZ, float score) {

    }
}
