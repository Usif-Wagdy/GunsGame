package gameplay;

import Texture.TextureReader;

import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.media.opengl.*;

import java.util.ArrayList;
import java.util.BitSet;
import javax.media.opengl.glu.GLU;
import javax.sound.sampled.*;


public class GunGLEventListener extends GunListener {
    private MainMenu mainMenu;  // ربط MainMenu
    private String difficulty;  // متغير لتخزين مستوى الصعوبة
    private String playerName;  // متغير لتخزين اسم اللاعب
    private int score;


    // Constructor لتلقي MainMenu و اسم اللاعب و مستوى الصعوبة
    public GunGLEventListener(MainMenu mainMenu, String playerName, String difficulty) {
        this.mainMenu = mainMenu;
        this.difficulty = difficulty;  // تعيين مستوى الصعوبة
        this.playerName = playerName;  // تعيين اسم اللاعب
        this.score = 0;  // تهيئة النتيجة
    }
    enum Directions {
        up,
        down,
        right,
        left,
        up_left,
        up_right,
        down_left,
        down_right
    }

    Directions direction = Directions.right;
    Clip clip;
    int soldierIndex = 0;
    int maxWidth = 100;
    int maxHeight = 100;
    int soldierX = 0, soldierY = maxHeight / 3;
    int soldierX2 = 0, soldierY2 = maxHeight / 3;
    private boolean isMultiplayer = false;
    int easy;
    private boolean isPaused = false;
    int N=0;
    public int timer = 0;
    public long lastTime = System.nanoTime();

    int k1 = (int) (Math.random() * 100) + 20;//to randomize the place of each zombies1
    int k2 = (int) (Math.random() * 100) + 20;//to randomize the place of each zombies2
    int k3 = (int) (Math.random() * 100) + 20;//to randomize the place of each zombies3
    int number = 10;
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Zombie> zombies1 = new ArrayList<>();
    ArrayList<Zombie> zombies2 = new ArrayList<>();
    ArrayList<Zombie> zombies3 = new ArrayList<>();
    ArrayList<BloodEffect> bloodEffects = new ArrayList<>();
    int counter = 0;
    double speed = 0;
    boolean nextLevelSoundPlayed = false;
    boolean GameisRunning = true;
    boolean lose=true;
    String textureNames[] = {"Man1.png", "Man2.png", "Man3.png", "Man4.png",
            "Fire.png",
            "Walk (1).png", "Walk (2).png", "Walk (3).png", "Walk (4).png", "Walk (5).png",
            "Walk (6).png", "Walk (7).png", "Walk (8).png", "Walk (9).png", "Walk (10).png",
            "go_1.png", "go_2.png", "go_3.png", "go_4.png", "go_5.png", "go_6.png", "go_7.png",
            "go_8.png", "go_9.png", "go_10.png", "female1.png", "female2.png", "female3.png", "female4.png",
            "female5.png", "female6.png", "female7.png", "female8.png", "female9.png", "female10.png"
            , "0.png", "1.png", "2.png", "3.png", "4.png", "5.png", "6.png", "7.png", "8.png", "9.png","win.png",
            "over.png", "blood.png", "Background2.png"};


    TextureReader.Texture texture[] = new TextureReader.Texture[textureNames.length];
    int textures[] = new int[textureNames.length];

    /*
     5 means gun in array pos
     x and y coordinate for gun
     */
    public void init(GLAutoDrawable gld) {
     playSound("src\\Assets\\sounds\\Zombies1.wav");
        GL gl = gld.getGL();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);    //This Will Clear The Background Color To Black

        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable Texture Mapping
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glGenTextures(textureNames.length, textures, 0);

        for (int i = 0; i < textureNames.length; i++) {
            try {
                texture[i] = TextureReader.readTexture(assetsFolderName + "//" + textureNames[i], true);
                gl.glBindTexture(GL.GL_TEXTURE_2D, textures[i]);

//                mipmapsFromPNG(gl, new GLU(), texture[i]);
                new GLU().gluBuild2DMipmaps(
                        GL.GL_TEXTURE_2D,
                        GL.GL_RGBA, // Internal Texel Format,
                        texture[i].getWidth(), texture[i].getHeight(),
                        GL.GL_RGBA, // External format from image,
                        GL.GL_UNSIGNED_BYTE,
                        texture[i].getPixels() // Imagedata
                );
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        if (difficulty.equals("Easy")) {
            easy = 2;
        } else if (difficulty.equals("Medium")) {
            easy = 3;
        } else if (difficulty.equals("Hard")) {
            easy = 5;
        }

        for (int i = 0; i < easy; i++) {
            zombies1.add(new Zombie(mainMenu, playerName, difficulty,maxWidth + k1, (int) (Math.random() * (maxHeight - 30)), 5));
            k1 = (int) (Math.random() * 100) + 20;
        }
        for (int i = 0; i < easy; i++) {
            zombies2.add(new Zombie(mainMenu, playerName, difficulty,maxWidth + k2, (int) (Math.random() * (maxHeight - 30)), 17));
            k2 = (int) (Math.random() * 100) + 20;
        }
        for (int i = 0; i < easy; i++) {
            zombies3.add(new Zombie(mainMenu, playerName, difficulty,maxWidth + k3, (int) (Math.random() * (maxHeight - 30)), 29));
            k3 = (int) (Math.random() * 100) + 20;
        }

    }









    boolean canShoot = true;

    public void handleKeyPress() {
        // controls of bullets
        if (isKeyPressed(KeyEvent.VK_SPACE) && canShoot) {
            double bulletX = soldierX;
            double bulletY = soldierY;

            switch (direction) {
                case up:
                    bulletY += 4;
                    bulletX += 1;
                    break;
                case down:
                    bulletY -= 4;
                    bulletX -= 1;
                    break;
                case right:
                    bulletX += 4;
                    bulletY -= 1;
                    break;
                case left:
                    bulletX -= 4;
                    bulletY += 1;
                    break;
                case up_left:
                    bulletY += 4;
                    bulletX -= 2.7;
                    break;
                case up_right:
                    bulletY += 2.3;
                    bulletX += 4;
                    break;
                case down_left:
                    bulletY -= 2.7;
                    bulletX -= 4;
                    break;
                case down_right:
                    bulletY -= 4;
                    bulletX += 2.3;
                    break;
            }
            bullets.add(new Bullet(direction, bulletX, bulletY));
            playSound("src\\Assets\\sounds\\pistol.wav");
            canShoot = false;
        }
        if (!isKeyPressed(KeyEvent.VK_SPACE)) canShoot = true;

        // controls of slodier
        if (isKeyPressed(KeyEvent.VK_DOWN) && isKeyPressed(KeyEvent.VK_LEFT)) {
            if (soldierX > 0) {
                soldierX--;
            }
            if (soldierY > 0) {
                soldierY--;
            }
            direction = Directions.down_left;
            soldierIndex++;
        } else if (isKeyPressed(KeyEvent.VK_DOWN) && isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (soldierX < maxWidth - 10) {
                soldierX++;
            }
            if (soldierY > 0) {
                soldierY--;
            }
            direction = Directions.down_right;
            soldierIndex++;

        } else if (isKeyPressed(KeyEvent.VK_UP) && isKeyPressed(KeyEvent.VK_LEFT)) {
            if (soldierY < maxHeight - 37) {
                soldierY++;
            }
            if (soldierX > 0) {
                soldierX--;
            }
            direction = Directions.up_left;
            soldierIndex++;
        } else if (isKeyPressed(KeyEvent.VK_UP) && isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (soldierY < maxHeight - 37) {
                soldierY++;
            }
            if (soldierX < maxWidth - 10) {
                soldierX++;
            }
            direction = Directions.up_right;
            soldierIndex++;
        } else if (isKeyPressed(KeyEvent.VK_LEFT)) {
            if (soldierX > 0) {
                soldierX--;
            }
            direction = Directions.left;
            soldierIndex++;
        } else if (isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (soldierX < maxWidth - 10) {
                soldierX++;
            }
            direction = Directions.right;
            soldierIndex++;

        } else if (isKeyPressed(KeyEvent.VK_DOWN)) {
            if (soldierY > 0) {
                soldierY--;
            }
            direction = Directions.down;
            soldierIndex++;
        } else if (isKeyPressed(KeyEvent.VK_UP)) {
            if (soldierY < maxHeight - 37) {
                soldierY++;
            }
            direction = Directions.up;
            soldierIndex++;
        }


    }

    private void togglePause() {
        if (isPaused) {
            // استئناف اللعبة
            isPaused = false;
            resumeGameSound();  // استئناف الصوت
        } else {
            // إيقاف اللعبة
            isPaused = true;
            stopGameSound();  // إيقاف الصوت
        }
    }

    private void stopGameSound() {
        if (clip != null && clip.isRunning()) {
            clip.stop();  // إيقاف الصوت
        }
    }

    private void resumeGameSound() {
        if (clip != null && !clip.isRunning()) {
            clip.start();  // استئناف الصوت
        }
    }
}