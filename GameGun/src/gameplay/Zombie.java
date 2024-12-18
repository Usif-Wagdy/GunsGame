package gameplay;

public class Zombie extends GunGLEventListener {
    double x, y;
    int index;
    int maxWidth = 100;
    int maxHeight = 100;
    int k = (int) (Math.random() * 100) + 20;
    private boolean isPaused = false; // متغير لتحديد حالة الإيقاف

    // دالة setter لتحديث حالة الإيقاف

    // هنا نقوم بتمرير المعلمات إلى الكونستركتور
    public Zombie(MainMenu mainMenu, String playerName, String difficulty, int x, int y, int index) {
        super(mainMenu, playerName, difficulty);  // تمرير المعلمات المطلوبة إلى GunGLEventListener
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public void move1(double speed) {
        if (!isPaused) {
            x -= 0.6 + speed;
            index++;
            if (index >= 15) {
                index = 5;
            }
            if (x < 0) {
                x = maxWidth + k;
                y = (int) (Math.random() * (maxHeight - 35));
            }
        }
    }

    public void move2(double speed) {
        if (!isPaused) {
            x -= 0.6 + speed;
            index++;
            if (index >= 25) {
                index = 15;
            }
            if (x < 0) {
                x = maxWidth + k2;
                y = (int) (Math.random() * (maxHeight - 35));
            }
        }
    }

    public void move3(double speed) {
        if (!isPaused) {
            x -= 0.6 + speed;
            index++;
            if (index >= 35) {
                index = 25;
            }
            if (x < 0) {
                x = maxWidth + k3;
                y = (int) (Math.random() * (maxHeight - 35));
            }
        }
    }

}
