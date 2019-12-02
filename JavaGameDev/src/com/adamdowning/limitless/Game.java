package com.adamdowning.limitless;

import com.adamdowning.limitless.graphics.Screen;
import com.adamdowning.limitless.input.Keyboard;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * @author Adam Downing - Development of life - Feb, 2019
 */
public class Game extends Canvas implements Runnable {
    public static final long serialVersionUID = 1L;

    public static int width = 300;
    public static int height = width / 16 * 9;
    public static int scale = 3;
    public static String title = "Limitless";

    private Thread thread;
    private JFrame frame;
    private Keyboard key;
    private boolean running = false;

    private Screen screen;

    private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

    public Game() {
        Dimension size = new Dimension(width * scale, height * scale);
        setPreferredSize(size);

        screen = new Screen(width, height);
        frame = new JFrame();
        key = new Keyboard();

        addKeyListener(key);
    }

    public synchronized void Start() {
        running = true;
        thread = new Thread(this, "Display");
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        final double nanoseconds = 1000000000.0 / 60.0;
        double delta = 0;
        int frames = 0;
        int updates = 0;
        requestFocus();
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nanoseconds;
            lastTime = now;
            while (delta >= 1) {
                update();
                updates++;
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + " ups, " + frames + " fps");
                frame.setTitle(title + "  |  "  +updates + " ups, " + frames + " fps");
                updates = 0;
                frames = 0;
            }
        }
        stop();
    }

    int x = 0, y = 0;

    public void update() {
        key.update();
        if (key.up) y--;
        if (key.down) y++;
        if (key.left) x--;
        if (key.right) x++;
    }

    public void render() {
        BufferStrategy bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            createBufferStrategy(3);
            return;
        }
        screen.clear();
        screen.render(x, y);

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = screen.pixels[i];
        }

        Graphics graphics = bufferStrategy.getDrawGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0,0, getWidth(), getHeight());
        graphics.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        graphics.dispose();
        bufferStrategy.show();
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.frame.setResizable(false);
        game.frame.setTitle("Limitless");
        game.frame.add(game);
        game.frame.pack();
        game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.frame.setLocationRelativeTo(null);
        game.frame.setVisible(true);

        game.Start();
    }

}
