package gameplay;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.FPSAnimator;
import java.util.List;
import java.util.ArrayList;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class MainMenu extends JFrame {
    GunGLEventListener gunGLEventListener;
    GLCanvas glCanvas;
    private JLabel instructionsImageLabel;  // هنا نعرف المتغير كعضو في الكلاس
    String player1Name;
    String player2Name;
    private static final String IMAGE_PATH = "src\\Assets\\Images\\";
    private ArrayList<String> playerNames = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> highScores = new ArrayList<>();

    private JButton newGameButton;
    private JButton resumeGameButton;
    private JButton highScoresButton;
    private JButton exitButton;
    private JLabel logo;
    private JButton stopSoundButton; // زر لإيقاف الصوت
    private JPanel buttonPanel;
    private JLabel background; // خلفية الصورة
    private Clip clip;
    private boolean isSoundPlaying = false; // لتتبع حالة الصوت

    // زر Single Player و Multiplayer
    private JButton singlePlayerButton;
    private JButton multiplayerButton;

    // زر لعرض خيارات الصعوبة
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    private JButton submitButton1 , submitButton2 , submitButton3;

    private JButton createSoundButton(String imagePath, Runnable action) {
        // تحميل الصورة
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // تغيير الأبعاد هنا
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        // إضافة action لإيقاف الصوت عند الضغط على الزر
        button.addActionListener(e -> action.run());

        return button;
    }

    public MainMenu() {
        setTitle("TetroGL");
        glCanvas = new GLCanvas();
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        loadHighScores();

        // إعداد الخلفية
        ImageIcon backgroundIcon = new ImageIcon(IMAGE_PATH + "2203_w023_n001_1968b_p1_1968.jpg");
        Image backgroundImage = backgroundIcon.getImage();
        backgroundImage = backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        background = new JLabel(new ImageIcon(backgroundImage));
        background.setLayout(new BorderLayout());

        // إعداد الأزرار
        buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.gridx = 0;

        // زر New Game
        newGameButton = createImageButton(
                IMAGE_PATH + "Start 1.png",
                IMAGE_PATH + "Start 2.png",
                this::showGameModeOptions);
        newGameButton.addActionListener(e -> {
            showGameModeOptions();
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });

        gbc.gridy = 0;
        buttonPanel.add(newGameButton, gbc);

        // زر Resume Game
        resumeGameButton = createImageButton(
                IMAGE_PATH + "Resume 1.png",
                IMAGE_PATH + "Resume 2.png",
                () -> System.out.println("Resume Game Pressed"));
        resumeGameButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 1;
        buttonPanel.add(resumeGameButton, gbc);

        // زر High Scores
        highScoresButton = createImageButton(
                IMAGE_PATH + "High 1.png",
                IMAGE_PATH + "High 2.png",
                () -> showHighScores());
        highScoresButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 2;
        buttonPanel.add(highScoresButton, gbc);

        // زر Exit
        exitButton = createImageButton(
                IMAGE_PATH  + "Exit 1.png",
                IMAGE_PATH + "exit 2.png",
                () -> System.exit(0));
        exitButton.addActionListener(e -> {
            playClickSound(IMAGE_PATH + "button-click_[cut_3sec].wav");
        });
        gbc.gridy = 3;
        buttonPanel.add(exitButton, gbc);

        // إضافة الأزرار الرئيسية في المركز
        background.add(buttonPanel, BorderLayout.CENTER);

        // إعداد زر إيقاف الصوت في JPanel مع FlowLayout
        JPanel soundButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        soundButtonPanel.setOpaque(false); // حتى لا يؤثر على الخلفية

        stopSoundButton = createSoundButton(
                IMAGE_PATH + "b_Sound2_Inactive.png", // صورة الزر عند التشغيل
                this::toggleSound);

// تغيير حجم زر الصوت ليكون أصغر
        ImageIcon icon = new ImageIcon( IMAGE_PATH + "b_Sound2.png");
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        stopSoundButton.setIcon(new ImageIcon(scaledImage));

// إضافة زر الصوت إلى الـ JPanel
        soundButtonPanel.add(stopSoundButton);


// إضافة الـ JPanel إلى أعلى الخلفية في BorderLayout.NORTH
        background.add(soundButtonPanel, BorderLayout.NORTH);
// إضافة أيقونة جديدة أسفل أو بجانب زر الصوت



        // إنشاء JLabel لعرض صورة التعليمات
        ImageIcon instructionsIcon = new ImageIcon("src\\Assets\\Images\\b_Parameters.png");
        Image iconImage = instructionsIcon.getImage();
        Image scaledIconImage = iconImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // تغيير الحجم هنا
        ImageIcon scaledInstructionsIcon = new ImageIcon(scaledIconImage);// استبدل هذا باسم الصورة التي تريد عرضها
        instructionsImageLabel = new JLabel(scaledInstructionsIcon);
// تعيين مستمع للأحداث على الصورة (عند النقر عليها)
        instructionsImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // عند الضغط على الصورة، إظهار صورة التعليمات
                showInstructions();
            }
        });

// إضافة الصورة إلى الـ panel الموجود في MainMenu
        soundButtonPanel.add(instructionsImageLabel);  // يمكنك إضافة هذه الصورة بجانب زر الصوت

        setContentPane(background);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeBackgroundImage();
            }
        });

        setVisible(true);
    }

    public JButton createImageButton(String imagePath, String hoverImagePath, Runnable action) {
        // تحميل الصورة
        ImageIcon icon = new ImageIcon(imagePath);
        Image image = icon.getImage();
        // تعديل الأبعاد هنا (على سبيل المثال 400x150)
        Image scaledImage = image.getScaledInstance(400, 100, Image.SCALE_SMOOTH); // تغيير الأبعاد هنا
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JButton button = new JButton(scaledIcon);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setMargin(new Insets(0, 0, 0, 0)); // تقليل المساحة حول الزر

        // تغيير الصورة عند مرور الماوس
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // تحميل صورة hover
                ImageIcon hoverIcon = new ImageIcon(hoverImagePath);
                Image hoverImage = hoverIcon.getImage();
                Image scaledHoverImage = hoverImage.getScaledInstance(400, 100, Image.SCALE_SMOOTH); // تغيير الأبعاد هنا
                button.setIcon(new ImageIcon(scaledHoverImage)); // تغيير الصورة
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(scaledIcon); // إعادة الصورة الأصلية بعد الخروج من الزر
            }
        });

        // عند الضغط على الزر، يتم تنفيذ الإجراء
        button.addActionListener(e -> action.run());
        return button;
    }

    public void showInstructions() {
        // إخفاء الأزرار الحالية (مثل New Game و Resume Game) وصورة التعليمات نفسها
        buttonPanel.setVisible(false);
        instructionsImageLabel.setVisible(false);  // إخفاء الأيقونة أيضًا

        // تحميل صورة التعليمات (التي ستصبح الخلفية)
        ImageIcon instructionsImage = new ImageIcon(IMAGE_PATH + "HOW TO PLAY.png");  // استبدل بـ اسم الصورة
        Image instructionsImg = instructionsImage.getImage();
        Image scaledImg = instructionsImg.getScaledInstance((getWidth() / 2) + 50 , (getHeight() / 2) + 50  , Image.SCALE_SMOOTH);  // جعل الصورة تملأ الشاشة

        // إضافة الصورة إلى الخلفية
        JLabel instructionsLabel = new JLabel(new ImageIcon(scaledImg));
        instructionsLabel.setLayout(new BorderLayout());  // التأكد من استخدام BorderLayout لتحديد مكان الزر

        // زر العودة (الذي يكون صورة)
        JButton returnButton = createImageButton(
                IMAGE_PATH + "Return 1.png",  // استبدل بـ صورة زر العودة
                IMAGE_PATH + "Return 2.png",  // استبدل بـ نفس الصورة
                () -> {
                    // إخفاء تعليمات والعودة للواجهة الرئيسية
                    instructionsLabel.setVisible(false); // إخفاء صورة التعليمات
                    buttonPanel.setVisible(true);  // إظهار الأزرار الأصلية مرة أخرى
                    instructionsImageLabel.setVisible(true);  // إعادة الأيقونة لتكون مرئية
                }
        );

        returnButton.setPreferredSize(new Dimension(100, 100));  // تحديد حجم زر العودة
        returnButton.setBorderPainted(false);
        returnButton.setContentAreaFilled(false);

        // إضافة زر العودة إلى الـ instructionsLabel بدلاً من background
        instructionsLabel.add(returnButton, BorderLayout.SOUTH); // إضافة الزر داخل التعليمات

        // إضافة الصورة إلى الـ background
        background.add(instructionsLabel, BorderLayout.CENTER);

        // إعادة رسم الواجهة لتحديث التغييرات
        revalidate();
        repaint();
    }

    private void resizeBackgroundImage() {
        ImageIcon backgroundIcon = new ImageIcon(IMAGE_PATH + "2203_w023_n001_1968b_p1_1968.jpg");
        Image backgroundImage = backgroundIcon.getImage();
        backgroundImage = backgroundImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        background.setIcon(new ImageIcon(backgroundImage));
    }

    public void playSound(String soundFileName) {
        try {
            if (clip == null || !clip.isOpen()) {
                File soundFile = new File(soundFileName);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
            }
            clip.setFramePosition(0); // إعادة الموضع إلى بداية الملف
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MainMenu mainMenu = new MainMenu();
        mainMenu.playSound("src\\Assets\\sounds\\Counter Strike Theme Song (1.6 Main Menu) by Counter- Strike-Valve Games_[cut_109sec].wav");
        mainMenu.isSoundPlaying = true; // تحديد أن الصوت بدأ بالفعل

    }
}