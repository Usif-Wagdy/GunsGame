package gameplay;
import gameplay.GunGLEventListener.Directions;
public class Bullet {
    Directions directions;
    double x,y;
    boolean isFired;

    public Bullet(Directions directions, double x, double y) {
        this.directions = directions;
        this.x = x;
        this.y = y;
        this.isFired=true;
    }
}