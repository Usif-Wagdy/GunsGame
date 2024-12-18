//package gameplay;
//import com.sun.opengl.util.*;
//import com.sun.opengl.util.j2d.TextRenderer;
//
//import java.awt.*;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalTime;
//import java.util.Date;
//import java.util.concurrent.TimeUnit;
//import javax.media.opengl.*;
//import javax.swing.*;
//
//public class Gun extends JFrame {
//    public static void main(String[] args) {
//        new Gun ();
//    }
//
//
//    public Gun () {
//        GLCanvas glcanvas;
//        Animator animator;
//
//        GunListener listener = new GunGLEventListener();
//        glcanvas = new GLCanvas();
//        glcanvas.addGLEventListener(listener);
//        glcanvas.addKeyListener(listener);
//        getContentPane().add(glcanvas, BorderLayout.CENTER);
//        animator = new FPSAnimator(15);
//        animator.add(glcanvas);
//        animator.start();
//
//        setTitle("Guns Game");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setSize(700, 700);
//        setLocationRelativeTo(null);
//        setVisible(true);
//        setFocusable(true);
//        glcanvas.requestFocus();
//    }
//}
//
