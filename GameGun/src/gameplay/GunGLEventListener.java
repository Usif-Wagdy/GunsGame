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

    public void display(GLAutoDrawable gld) {
        if (isPaused) {
            return;
        }

        GL gl = gld.getGL();
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glLoadIdentity();
        DrawBackground(gl);
        displayScore(gl);
        DrawSprite(gl, 53, 90, 40 - N, 0.35f, Directions.up);
        handleKeyPress();
        gl.glColor3f(1.0f, 1.0f, 1.0f);

        soldierIndex = soldierIndex % 4;
        if (GameisRunning) {
            displayTimer(gl);
            DrawSprite(gl, soldierX, soldierY, soldierIndex, 1, direction);
            for (Bullet bullet : bullets) {
                if (bullet.isFired) {
                    switch (bullet.directions) {
                        case up:
                            bullet.y += 1.5;
                            break;
                        case down:
                            bullet.y -= 1.5;
                            break;
                        case right:
                            bullet.x += 1.5;
                            break;
                        case left:
                            bullet.x -= 1.5;
                            break;
                        case up_left:
                            bullet.y += 1.5;
                            bullet.x -= 1.5;
                            break;
                        case up_right:
                            bullet.y += 1.5;
                            bullet.x += 1.5;
                            break;
                        case down_left:
                            bullet.y -= 1.5;
                            bullet.x -= 1.5;
                            break;
                        case down_right:
                            bullet.y -= 1.5;
                            bullet.x += 1.5;
                            break;
                    }
                    DrawSprite(gl, bullet.x, bullet.y, 4, 0.3f, bullet.directions);

                }
            }
            for (Zombie zombie : zombies1) {
                zombie.move1(speed);
                DrawSprite(gl, zombie.x, zombie.y, zombie.index, 1.2f, Directions.up);
                // stage 1
                if (checkCollisionWithSoldier(zombie)) {
                    playSound("src\\Assets\\sounds\\zombieBite.wav");
                }
                if (counter == 10 && !nextLevelSoundPlayed) {
                    speed = 0.5;
                    playSound("src\\Assets\\sounds\\level2.wav");
                    nextLevelSoundPlayed = true; // Mark the sound as played
                }

                // stage 2
                if (counter == 20 && nextLevelSoundPlayed) {
                    speed = 1;
                    playSound(  "src\\Assets\\sounds\\level2.wav");
                    nextLevelSoundPlayed = false; // Mark the sound as played
                }

            }
            for (Zombie zombie : zombies2) {
                zombie.move2(speed);
                DrawSprite(gl, zombie.x, zombie.y, zombie.index, 1f, Directions.up);
                // stage 1
                if (checkCollisionWithSoldier(zombie)) {
                    playSound( "src\\Assets\\sounds\\zombieBite.wav");
                }
                if (counter == 10) {
                    speed = 0.5;
                }
                // stage 2
                if (counter == 20) {
                    speed = 1;
                }
            }
            for (Zombie zombie : zombies3) {
                zombie.move3(speed);
                DrawSprite(gl, zombie.x, zombie.y, zombie.index, 1.2f, Directions.up);
                if (checkCollisionWithSoldier(zombie)) {
                    playSound("src\\Assets\\sounds\\zombieBite.wav");
                }
                // stage 1
                if (counter == 10) {
                    speed = 0.5;
                }
                // stage 2
                if (counter == 20) {
                    speed = 1;
                }
            }
            handleCollisions(gl);
            for (int i = 0; i < bloodEffects.size(); i++) {
                BloodEffect blood = bloodEffects.get(i);
                // رسم الدم
                DrawSprite(gl, blood.x, blood.y, texture.length - 2, 1, Directions.up); // Blood texture
                blood.timer--;
                if (blood.timer <= 0) {
                    bloodEffects.remove(i);
                    i--;
                }
            }
            if (N==5) {
                GameisRunning = false;
                lose=true;
                mainMenu.addPlayerScore(playerName, score);
            }
            if (score ==30){
                GameisRunning = false;
                lose=false;
                mainMenu.addPlayerScore(playerName, score);
            }
        }
        if (!GameisRunning&&lose ) {
            DrawSprite(gl, 45, 50, texture.length - 3, 5f, Directions.up);
            stopGameSound();
        }

        if(!GameisRunning && !lose){
            DrawSprite(gl, 45, 50, texture.length - 4, 5f, Directions.up);
            stopGameSound();
        }
        long currentTime = System.nanoTime();
        if (currentTime - lastTime >= 1000_000_000) {
            timer++;
            lastTime = currentTime;
            if (timer > 99) {
                timer = 0;
            }
        }

    }





    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public void DrawSprite(GL gl, double x, double y, int index, float scale, Directions dir) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[index]);    // Turn Blending On

        int angle = 0;
        switch (dir) {
            case up:
                angle = 0;
                break;
            case down:
                angle = 180;
                break;
            case right:
                angle = -90;
                break;
            case left:
                angle = 90;
                break;
            case up_left:
                angle = 45;
                break;
            case up_right:
                angle = -45;
                break;
            case down_left:
                angle = 135;
                break;
            case down_right:
                angle = -135;
                break;
            default:
                angle = 0;
        }
        gl.glPushMatrix();
        gl.glTranslated(x / (maxWidth / 2.0) - 0.9, y / (maxHeight / 2.0) - 0.9, 0);
        gl.glScaled(0.1 * scale, 0.1 * scale, 1);
        gl.glRotated(angle, 0, 0, 1);
        //System.out.println(x +" " + y);
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }


    public void DrawBackground(GL gl) {
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textures[textures.length - 1]);    // Turn Blending On

        gl.glPushMatrix();
        gl.glBegin(GL.GL_QUADS);
        // Front Face
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();

        gl.glDisable(GL.GL_BLEND);
    }

    /*
     * KeyListener
     */
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

    //yousef ashraf (handleCollisions)
    private void handleCollisions(GL gl) {
        // List to store zombies and bullets to be removed
        ArrayList<Zombie> zombiesToRemove = new ArrayList<>();
        ArrayList<Bullet> bulletsToRemove = new ArrayList<>();

        // Check for bullet-zombie collisions
        for (Bullet bullet : bullets) {
            if (bullet.isFired) {
                // Check collision with zombies1
                for (Zombie zombie : zombies1) {
                    if (checkCollision(bullet, zombie)) {
                        zombiesToRemove.add(zombie);
                        bulletsToRemove.add(bullet);
                        bloodEffects.add(new BloodEffect(zombie.x, zombie.y, 60)); // Add blood effect
                        counter++; // Increase score
                        score++;
                        playSound("src\\Assets\\sounds\\zombiehit.wav");

                    }
                }

                // Repeat collision check for zombies2 and zombies3
                for (Zombie zombie : zombies2) {
                    if (checkCollision(bullet, zombie)) {
                        zombiesToRemove.add(zombie);
                        bulletsToRemove.add(bullet);
                        bloodEffects.add(new BloodEffect(zombie.x, zombie.y, 60));
                        counter++;
                        score++;
                        playSound("src\\Assets\\sounds\\zombiehit.wav");
                    }
                }
                for (Zombie zombie : zombies3) {
                    if (checkCollision(bullet, zombie)) {
                        zombiesToRemove.add(zombie);
                        bulletsToRemove.add(bullet);
                        bloodEffects.add(new BloodEffect(zombie.x, zombie.y, 60));
                        counter++;
                        score++;
                        playSound("src\\Assets\\sounds\\zombiehit.wav");
                    }

                }

            }
        }
        for (Zombie zombie : zombies1) {
            if (checkCollisionWithSoldier(zombie) || checkZombieOutOfBounds(zombie)) {
                zombiesToRemove.add(zombie);
                N++;
            }
        }

        for (Zombie zombie : zombies2) {
            if (checkCollisionWithSoldier(zombie) || checkZombieOutOfBounds(zombie)) {
                zombiesToRemove.add(zombie);
                N++;
            }
        }

        for (Zombie zombie : zombies3) {
            if (checkCollisionWithSoldier(zombie) || checkZombieOutOfBounds(zombie)) {
                zombiesToRemove.add(zombie);
                N++;
            }
        }
        zombies1.removeAll(zombiesToRemove);
        zombies2.removeAll(zombiesToRemove);
        zombies3.removeAll(zombiesToRemove);
        bullets.removeAll(bulletsToRemove);
        regenerateZombies();
    }

    private boolean checkCollision(Bullet bullet, Zombie zombie) {
        return Math.abs(bullet.x - zombie.x) < 5 && Math.abs(bullet.y - zombie.y) < 5;
    }

    private void regenerateZombies() {
        if (zombies1.isEmpty()) {
            zombies1.add(new Zombie(mainMenu, playerName, difficulty,maxWidth + (int) (Math.random() * 100), (int) (Math.random() * (maxHeight - 30)), 5));
        }
        if (zombies2.isEmpty()) {
            zombies2.add(new Zombie(mainMenu, playerName, difficulty,maxWidth + (int) (Math.random() * 100), (int) (Math.random() * (maxHeight - 30)), 17));
        }
        if (zombies3.isEmpty()) {
            zombies3.add(new Zombie(mainMenu, playerName, difficulty,maxWidth + (int) (Math.random() * 100), (int) (Math.random() * (maxHeight - 30)), 29));
        }
    }

    //    boolean flag=false;// fix the motion of soldier
    public BitSet keyBits = new BitSet(256);
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_ESCAPE) {
            togglePause();  // التعامل مع إيقاف اللعبة عند الضغط على ESC
        }
        keyBits.set(keyCode);
    }

    @Override
    public void keyReleased(final KeyEvent event) {
        int keyCode = event.getKeyCode();
        keyBits.clear(keyCode);
    }

    @Override
    public void keyTyped(final KeyEvent event) {
        // don't care
    }

    public boolean isKeyPressed(final int keyCode) {
        return keyBits.get(keyCode);
    }

    public void playSound(String soundFileName) {
        try {
            File soundFile = new File(soundFileName);  // حدد المسار الكامل للملف الصوتي
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();  // تشغيل الصوت
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private boolean checkCollisionWithSoldier(Zombie zombie) {
        return Math.abs(zombie.x - soldierX) < 5 && Math.abs(zombie.y - soldierY) < 5;
    }

    private boolean checkZombieOutOfBounds(Zombie zombie) {
        return zombie.x <= 1; // Assuming the left edge of the screen is x = 1
    }

    public void displayScore(GL gl) {
        String scoreString = String.valueOf(score);
        double x = 13;
        double y = 90;
        for (int i = 0; i < scoreString.length(); i++) {
            char digit = scoreString.charAt(i);
            int digitIndex = digit - '0' + 35;
            DrawSprite(gl, x, y, digitIndex, 0.4f, Directions.up);
            x += 5;
        }
    }
    public void displayTimer(GL gl) {
        String timeToString = String.valueOf(timer);
        double x = 85;
        double y = 90;
        for (int i = 0; i < timeToString.length(); i++) {
            char digit = timeToString.charAt(i);
            int digitIndex = digit - '0' + 35;
            DrawSprite(gl, x, y, digitIndex, 0.4f, Directions.up);
            x += 5; //Distance between numbers
        }
    }



}