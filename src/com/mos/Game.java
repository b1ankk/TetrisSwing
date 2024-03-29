package com.mos;

import com.mos.scene.GameScene;
import com.mos.scene.Scene;
import com.mos.util.KeyHandler;
import com.mos.util.GameTime;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game implements Runnable
{
    private final String title;
    private final int width;
    private final int height;
    
    private boolean running = false;
    
    private final Window window;
    private Thread thread;
    
    private KeyHandler keyHandler;
    
    private BufferedImage bufferedImage;
    private Graphics2D g;
    private Canvas canvas;
    
    private BufferStrategy bufferStrategy;
    
    private Scene activeScene;
    
    
    public Game(int width, int height, String title)
    {
        window = new Window(width, height, title);
        this.title = title;
        this.width = width;
        this.height = height;
        
    }
    
    
    public void start()
    {
        thread = new Thread(this);
        thread.setName("GameThread");
        thread.start();
    }
    
    private void update()
    {
        activeScene.update();
    }
    
    private void render()
    {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);
        
        activeScene.render(g);
    }
    
    private void draw()
    {
        Graphics2D g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
        g2.drawImage(bufferedImage, 0, 0, null);
        g2.dispose();
        bufferStrategy.show();
    }
    
    private void input()
    {
        activeScene.input();
    }
    
    private void init()
    {
        running = true;
        canvas = window.getCanvas();
        keyHandler = new KeyHandler(canvas);
        
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = (Graphics2D) bufferedImage.getGraphics();
        
        bufferStrategy = canvas.getBufferStrategy();
        
        activeScene = new GameScene(keyHandler);
    }
    
    @Override
    public void run()
    {
        init();
        
        double goalFrameRate = 60;
        double frameTime = 1_000_000_000 / goalFrameRate;
        long lastFrame = System.nanoTime();
        
        GameTime.setDeltaTime(frameTime);
        
        long timer = System.currentTimeMillis();
        int frames = 0;
        
        while (running)
        {
            long now = System.nanoTime();
            
            input();
            keyHandler.clearActiveStates();
            while (now - lastFrame < frameTime)
            {
                update();
                now = System.nanoTime();
            }
            
            GameTime.setDeltaTime(System.nanoTime() - lastFrame);
            lastFrame = System.nanoTime();
            render();
            draw();
            frames++;
            
            if (System.currentTimeMillis() - timer >= 1000)
            {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
    }
}
