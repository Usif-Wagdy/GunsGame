package gameplay;
import gameplay.GunGLEventListener2.Directions2;
public class Bullet2 {
    GunGLEventListener2.Directions2 directions;
    double x,y;
    boolean isFired;

    public Bullet2(Directions2 directions, double x, double y) {
        this.directions = directions;
        this.x = x;
        this.y = y;
        this.isFired=true;
    }

}