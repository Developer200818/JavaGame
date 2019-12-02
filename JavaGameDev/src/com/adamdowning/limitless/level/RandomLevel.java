package com.adamdowning.limitless.level;

import java.util.Random;

/**
 * @author Adam Downing - Development of life - Feb, 2019
 */
public class RandomLevel extends Level {

    private final Random random = new Random();

    public RandomLevel(int width, int height) {
        super(width, height);
    }

    protected void generateLevel() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[x+y* width] = random.nextInt(4);
            }
        }
    }
}
